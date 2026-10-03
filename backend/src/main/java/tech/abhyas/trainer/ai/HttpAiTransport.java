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

/** Real HTTP calls to Groq chat, Groq Whisper and Gemma. Keys come from GROQ_API_KEY and GOOGLE_AI_KEY only. */
@Component
class HttpAiTransport implements AiTransport {
    private static final String GROQ = "https://api.groq.com/openai/v1/";
    private static final String GOOGLE = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final HttpClient http = HttpClient.newHttpClient();
    private final JsonMapper json;
    private final String groqKey, googleKey, chatModel, whisperModel, gemmaModel;

    HttpAiTransport(JsonMapper json,
                    @Value("${GROQ_API_KEY:}") String groqKey,
                    @Value("${GOOGLE_AI_KEY:}") String googleKey,
                    @Value("${app.ai.models.groq-chat}") String chatModel,
                    @Value("${app.ai.models.groq-whisper}") String whisperModel,
                    @Value("${app.ai.models.gemma}") String gemmaModel) {
        this.json = json;
        this.groqKey = groqKey;
        this.googleKey = googleKey;
        this.chatModel = chatModel;
        this.whisperModel = whisperModel;
        this.gemmaModel = gemmaModel;
    }

    @Override
    public String send(Provider provider, String prompt, Duration timeout) {
        if (provider == Provider.GEMMA) {
            JsonNode r = json.readTree(call(googleKey, "x-goog-api-key", URI.create(GOOGLE + gemmaModel + ":generateContent"),
                    "application/json", gemmaBody(json, prompt), timeout));
            return r.path("candidates").path(0).path("content").path("parts").path(0).path("text").asString("");
        }
        JsonNode r = json.readTree(call(groqKey, "Authorization", URI.create(GROQ + "chat/completions"),
                "application/json", groqBody(json, chatModel, prompt), timeout));
        return r.path("choices").path(0).path("message").path("content").asString("");
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

    static byte[] gemmaBody(JsonMapper json, String prompt) {
        return json.writeValueAsBytes(Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("temperature", 0)));
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
                throw new AiFailure(b.contains("per day") || b.contains("daily")
                        ? AiFailure.Kind.DAILY_LIMIT : AiFailure.Kind.RATE_LIMIT);
            }
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

    private static void field(ByteArrayOutputStream out, String boundary, String name, String value) {
        write(out, "--" + boundary + "\r\nContent-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" + value + "\r\n");
    }

    private static void write(ByteArrayOutputStream out, String s) { out.writeBytes(s.getBytes(StandardCharsets.UTF_8)); }
}
