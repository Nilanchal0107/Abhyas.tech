package tech.abhyas.trainer.ai;

import java.time.Duration;

/** The wire to one provider. {@link HttpAiTransport} is real; tests use a fake. Throws {@link AiFailure}. */
public interface AiTransport {
    /** Sends a prompt, returns the reply text. */
    String send(Provider provider, String prompt, Duration timeout);

    /** False when the provider has no key, so callers can skip features that need it. */
    default boolean configured(Provider provider) { return true; }

    /** Sends audio to Groq Whisper, returns the reply JSON text. */
    String transcribe(byte[] audio, Duration timeout);
}
