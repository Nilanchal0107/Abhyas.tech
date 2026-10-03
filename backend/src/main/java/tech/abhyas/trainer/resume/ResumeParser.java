package tech.abhyas.trainer.resume;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.ai.Scrubber;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;

/**
 * PDF fallback parse (FR-2, trd 7.1 step 3): PDFBox text and hidden links, phone and email removed, Groq at
 * temperature 0 for the ParsedResume shape. The PDF lives only in memory for the request.
 */
@Service
public class ResumeParser {
    static final int MAX_BYTES = 1024 * 1024;
    static final int MAX_CHARS = 30_000; // keeps the prompt inside the model's limits

    private final AiClient ai;

    public ResumeParser(AiClient ai) { this.ai = ai; }

    public Map<String, Object> parse(byte[] pdf) {
        if (pdf.length > MAX_BYTES) {
            throw new ApiException(HttpStatus.CONTENT_TOO_LARGE, "file-too-large", "This file is over 1 MB. Please choose a smaller PDF.");
        }
        if (pdf.length < 5 || !new String(pdf, 0, 5, java.nio.charset.StandardCharsets.ISO_8859_1).equals("%PDF-")) {
            throw notAPdf();
        }
        String text = extract(pdf);
        String prompt = """
                Read this resume text and return it as JSON in exactly this shape. Use null for anything that is missing.
                Do not invent anything. Copy bullet text as written.
                {"profile": {"name": null, "location": null, "links": [{"label": "GitHub", "url": "https://..."}], "summary": null},
                 "education": [{"school": null, "degree": null, "date": null, "details": []}],
                 "experience": [{"company": null, "title": null, "date": null, "bullets": []}],
                 "projects": [{"name": null, "date": null, "bullets": []}],
                 "skills": []}

                Resume text:
                """ + Scrubber.scrub(text);
        Optional<JsonNode> reply = ai.json(Provider.GROQ_CHAT, prompt, JsonNode::isObject);
        if (reply.isEmpty()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ai-unavailable", "We could not read this resume automatically.");
        }
        Map<String, Object> resume = shape(reply.get());
        // email and phone never go to the AI, so they are read from the text here
        @SuppressWarnings("unchecked") Map<String, Object> profile = (Map<String, Object>) resume.get("profile");
        profile.put("email", Scrubber.firstEmail(text));
        profile.put("phone", Scrubber.firstPhone(text));
        return resume;
    }

    private static ApiException notAPdf() {
        return new ApiException(HttpStatus.BAD_REQUEST, "not-a-pdf", "Please choose a PDF file.");
    }

    /** Page text, then every link address found in the page annotations (hidden behind words like "GitHub"). */
    private static String extract(byte[] pdf) {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            StringBuilder out = new StringBuilder(new PDFTextStripper().getText(doc));
            Set<String> urls = new LinkedHashSet<>();
            for (PDPage page : doc.getPages()) {
                for (PDAnnotation a : page.getAnnotations()) {
                    if (a instanceof PDAnnotationLink l && l.getAction() instanceof PDActionURI u && u.getURI() != null) {
                        urls.add(u.getURI());
                    }
                }
            }
            if (!urls.isEmpty()) out.append("\nLinks in the PDF:\n").append(String.join("\n", urls));
            return out.length() > MAX_CHARS ? out.substring(0, MAX_CHARS) : out.toString();
        } catch (IOException e) {
            throw notAPdf(); // damaged or password-protected
        }
    }

    // Rebuilds the reply field by field so anything extra or mistyped from the model is dropped (api-contracts 2.1).
    static Map<String, Object> shape(JsonNode r) {
        JsonNode p = r.path("profile");
        List<Map<String, Object>> links = new ArrayList<>();
        for (JsonNode l : p.path("links")) {
            String url = text(l.path("url"));
            if (url != null) links.add(map("label", text(l.path("label")), "url", url));
        }
        Map<String, Object> profile = map("name", text(p.path("name")), "email", null, "phone", null,
                "location", text(p.path("location")), "links", links, "summary", text(p.path("summary")));
        return map("profile", profile,
                "education", each(r.path("education"), e -> map("school", text(e.path("school")), "degree", text(e.path("degree")),
                        "date", text(e.path("date")), "details", strings(e.path("details")))),
                "experience", each(r.path("experience"), e -> map("company", text(e.path("company")), "title", text(e.path("title")),
                        "date", text(e.path("date")), "bullets", strings(e.path("bullets")))),
                "projects", each(r.path("projects"), e -> map("name", text(e.path("name")), "date", text(e.path("date")),
                        "bullets", strings(e.path("bullets")))),
                "skills", strings(r.path("skills")));
    }

    private static String text(JsonNode n) {
        if (!n.isString()) return null;
        String s = n.asString().strip();
        return s.isEmpty() ? null : s;
    }

    private static List<String> strings(JsonNode n) {
        List<String> out = new ArrayList<>();
        for (JsonNode s : n) { String t = text(s); if (t != null) out.add(t); }
        return out;
    }

    private static List<Map<String, Object>> each(JsonNode n, java.util.function.Function<JsonNode, Map<String, Object>> f) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (JsonNode e : n) {
            if (!e.isObject()) continue;
            Map<String, Object> m = f.apply(e);
            // the model sometimes returns the all-null example entry from the prompt
            if (m.values().stream().anyMatch(v -> v instanceof String || v instanceof List<?> l && !l.isEmpty())) out.add(m);
        }
        return out;
    }

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }
}
