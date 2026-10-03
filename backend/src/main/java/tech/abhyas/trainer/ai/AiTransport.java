package tech.abhyas.trainer.ai;

import java.time.Duration;

/** The wire to one provider. {@link HttpAiTransport} is real; tests use a fake. Throws {@link AiFailure}. */
public interface AiTransport {
    /** Sends a prompt, returns the reply text. */
    String send(Provider provider, String prompt, Duration timeout);

    /** Sends audio to Groq Whisper, returns the reply JSON text. */
    String transcribe(byte[] audio, Duration timeout);
}
