package tech.abhyas.trainer.ai;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import tools.jackson.databind.json.JsonMapper;

/** Real calls, one per provider. Runs only when the keys are in the environment; costs a few tokens. Not part of CI. */
class AiLiveSmokeTest {
    private static final String GROQ = System.getenv("GROQ_API_KEY");
    private static final String GOOGLE = System.getenv("GOOGLE_AI_KEY");

    private AiClient client() {
        var json = JsonMapper.builder().build();
        var transport = new HttpAiTransport(json, GROQ == null ? "" : GROQ, GOOGLE == null ? "" : GOOGLE,
                "llama-3.3-70b-versatile", "whisper-large-v3-turbo", "gemma-3-27b-it"); // keep in step with application.yml
        return new AiClient(transport, new AiGate(Clock.systemUTC()), json, 20, 40);
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GROQ_API_KEY", matches = ".+")
    void groqChat() {
        assertTrue(client().json(Provider.GROQ_CHAT, "Return {\"ok\": true}", n -> n.path("ok").asBoolean()).isPresent());
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "GOOGLE_AI_KEY", matches = ".+")
    void gemma() {
        assertTrue(client().json(Provider.GEMMA, "Return {\"ok\": true}", n -> n.path("ok").asBoolean()).isPresent());
    }

    /** Set WHISPER_TEST_FILE to a short .webm recording. */
    @Test
    @EnabledIfEnvironmentVariable(named = "WHISPER_TEST_FILE", matches = ".+")
    void whisper() throws Exception {
        byte[] audio = Files.readAllBytes(Path.of(System.getenv("WHISPER_TEST_FILE")));
        assertTrue(client().transcribe(audio, n -> n.has("text")).isPresent());
    }
}
