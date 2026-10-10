package tech.abhyas.trainer.ai;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Real HTTP calls to Groq chat and Groq Whisper. The key comes from GROQ_API_KEY only. */
@Component
class HttpAiTransport implements AiTransport {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpAiTransport.class);
    private static final String GROQ = "https://api.groq.com/openai/v1/";

    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json;
    private final String groqKey, chatModel, whisperModel;

    HttpAiTransport(JsonMapper json,
                    @Value("${app.ai.groq-key:}") String groqKey,
                    @Value("${app.ai.models.groq-chat}") String chatModel,
                    @Value("${app.ai.models.groq-whisper}") String whisperModel) {
        this.json = json;
        this.groqKey = groqKey;
        this.chatModel = chatModel;
        this.whisperModel = whisperModel;
    }

    @Override
    public String send(Provider provider, String prompt, Duration timeout) {
        JsonNode r = json.readTree(call(groqKey, "Authorization", URI.create(GROQ + "chat/completions"),
                "application/json", groqBody(json, chatModel, prompt), timeout));
        return r.path("choices").path(0).path("message").path("content").asString("");
    }

    @Override
    public boolean configured(Provider provider) {
        return !groqKey.isBlank();
    }

    @Override
    public String transcribe(byte[] audio, Duration timeout) {
        String boundary = UUID.randomUUID().toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        field(out, boundary, "model", whisperModel);
        field(out, boundary, "response_format", "verbose_json");
        field(out, boundary, "temperature", "0");
        write(out, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"answer.webm\"\r\n"
                + "Content-Type: audio/webm\r\n\r\n");
        out.writeBytes(audio);
        write(out, "\r\n--" + boundary + "--\r\n");
        return call(groqKey, "Authorization", URI.create(GROQ + "audio/transcriptions"),
                "multipart/form-data; boundary=" + boundary, out.toByteArray(), timeout);
    }

    static byte[] groqBody(JsonMapper json, String model, String prompt) {
        return json.writeValueAsBytes(Map.of("model", model, "temperature", 0,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(Map.of("role", "user", "content", prompt))));
    }

    private String call(String key, String keyHeader, URI uri, String contentType, byte[] body, Duration timeout) {
        if (key.isBlank()) throw new AiFailure(AiFailure.Kind.NOT_CONFIGURED);
        HttpRequest req = HttpRequest.newBuilder(uri).timeout(timeout)
                .header(keyHeader, keyHeader.equals("Authorization") ? "Bearer " + key : key)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();
        try {
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
            int s = res.statusCode();
            if (s / 100 == 2) return res.body();
            if (s == 429) {
                String b = res.body().toLowerCase();
                boolean daily = b.contains("per day") || b.contains("daily");
                throw new AiFailure(daily ? AiFailure.Kind.DAILY_LIMIT : AiFailure.Kind.RATE_LIMIT, daily ? 0 : retryAfter(res));
            }
            log.warn("ai http status={} host={}", s, uri.getHost()); // status only, never the body (trd 6.5)
            throw new AiFailure(s >= 500 ? AiFailure.Kind.SERVER_ERROR : AiFailure.Kind.OTHER);
        } catch (HttpTimeoutException e) {
            throw new AiFailure(AiFailure.Kind.TIMEOUT);
        } catch (IOException e) {
            throw new AiFailure(AiFailure.Kind.SERVER_ERROR);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiFailure(AiFailure.Kind.OTHER);
        }
    }

    private static int retryAfter(HttpResponse<String> res) {
        try { return Math.max(0, Integer.parseInt(res.headers().firstValue("retry-after").orElse("0").strip())); }
        catch (NumberFormatException e) { return 0; } // an HTTP date is not worth parsing: the caller then does not wait
    }

    private static void field(ByteArrayOutputStream out, String boundary, String name, String value) {
        write(out, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + value + "\r\n");
    }

    private static void write(ByteArrayOutputStream out, String s) { out.writeBytes(s.getBytes(StandardCharsets.UTF_8)); }
}
