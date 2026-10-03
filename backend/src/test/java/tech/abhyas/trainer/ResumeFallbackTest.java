package tech.abhyas.trainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.AiFailure;
import tech.abhyas.trainer.ai.AiGate;
import tech.abhyas.trainer.ai.AiTransport;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.resume.ResumeController;
import tech.abhyas.trainer.resume.ResumeParser;
import tools.jackson.databind.json.JsonMapper;

/** Ticket 08: PDF fallback parse. A fake AI transport stands in for Groq. */
class ResumeFallbackTest {
    static final String REPLY = """
            {"profile":{"name":"Asha Rao","location":null,"links":[{"label":"GitHub","url":"https://github.com/asha"},{"label":"x"}],"summary":" "},
             "education":[{"school":null,"degree":null,"date":null,"details":[]}],"experience":[{"company":"Acme","title":"Dev","date":"2024","bullets":["Built APIs", 5]}],
             "projects":[],"skills":["Java"],"extra":"dropped"}""";

    final List<String> prompts = new ArrayList<>();
    String reply = REPLY;
    ResumeParser parser;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        AiTransport fake = new AiTransport() {
            @Override public String send(Provider p, String prompt, Duration t) {
                prompts.add(prompt);
                if (reply == null) throw new AiFailure(AiFailure.Kind.OTHER);
                return reply;
            }
            @Override public String transcribe(byte[] audio, Duration t) { throw new AiFailure(AiFailure.Kind.OTHER); }
        };
        parser = new ResumeParser(new AiClient(fake, new AiGate(Clock.systemUTC()), JsonMapper.builder().build(), 20, 40));
        mvc = MockMvcBuilders.standaloneSetup(new ResumeController(parser, null)).setControllerAdvice(new ProblemHandler()).build();
    }

    static byte[] pdf(String text, String linkUrl) throws IOException {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                cs.newLineAtOffset(50, 700);
                cs.showText(text);
                cs.endText();
            }
            if (linkUrl != null) {
                PDAnnotationLink link = new PDAnnotationLink();
                link.setRectangle(new PDRectangle(50, 690, 100, 20));
                PDActionURI uri = new PDActionURI();
                uri.setURI(linkUrl);
                link.setAction(uri);
                page.getAnnotations().add(link);
            }
            doc.save(out);
            return out.toByteArray();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendsScrubbedTextAndHiddenLinksAndReturnsOnlyTheContractShape() throws Exception {
        Map<String, Object> r = parser.parse(pdf("Asha Rao asha@example.com +91 98765 43210 GitHub", "https://github.com/asha"));
        String prompt = prompts.get(0);
        assertTrue(prompt.contains("Asha Rao"));
        assertTrue(prompt.contains("https://github.com/asha"), "hidden link is included");
        assertFalse(prompt.contains("asha@example.com"));
        assertFalse(prompt.contains("98765"));

        Map<String, Object> profile = (Map<String, Object>) r.get("profile");
        assertEquals("Asha Rao", profile.get("name"));
        assertNull(profile.get("summary"), "blank becomes null");
        assertEquals("asha@example.com", profile.get("email"), "read locally, never sent to the AI");
        assertEquals("+91 98765 43210", profile.get("phone"));
        assertTrue(((List<?>) r.get("education")).isEmpty(), "an all-null entry is dropped");
        assertEquals(1, ((List<?>) profile.get("links")).size(), "a link without a url is dropped");
        var exp = (Map<String, Object>) ((List<?>) r.get("experience")).get(0);
        assertEquals(List.of("Built APIs"), exp.get("bullets"), "non-string bullets are dropped");
        assertFalse(r.containsKey("extra"));
    }

    @Test
    void rejectsNonPdfAndOversizeFiles() {
        assertEquals("not-a-pdf", assertThrows(ApiException.class, () -> parser.parse("hello world".getBytes())).type());
        assertEquals("not-a-pdf", assertThrows(ApiException.class, () -> parser.parse("%PDF-1.4 broken".getBytes())).type());
        var big = assertThrows(ApiException.class, () -> parser.parse(new byte[1024 * 1024 + 1]));
        assertEquals("file-too-large", big.type());
        assertEquals(413, big.status().value());
        assertTrue(prompts.isEmpty(), "nothing reached the AI");
    }

    @Test
    void unavailableOrUnusableAiIsAiUnavailable() throws Exception {
        byte[] file = pdf("Asha Rao", null);
        for (String bad : new String[] {null, "not json", "[1]"}) {
            reply = bad;
            var e = assertThrows(ApiException.class, () -> parser.parse(file));
            assertEquals("ai-unavailable", e.type());
            assertEquals(503, e.status().value());
        }
    }

    @Test
    void endpointReturnsResumeAndProblemCodes() throws Exception {
        var ok = new MockMultipartFile("file", "r.pdf", "application/pdf", pdf("Asha Rao", null));
        mvc.perform(MockMvcRequestBuilders.multipart("/api/resumes/parse-fallback").file(ok))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resume.profile.name").value("Asha Rao"));
        var bad = new MockMultipartFile("file", "r.txt", "text/plain", "hello".getBytes());
        mvc.perform(MockMvcRequestBuilders.multipart("/api/resumes/parse-fallback").file(bad))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.type").value("not-a-pdf"));
    }
}
