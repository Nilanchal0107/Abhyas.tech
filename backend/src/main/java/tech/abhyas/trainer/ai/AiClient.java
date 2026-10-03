package tech.abhyas.trainer.ai;

import java.time.Duration;
import java.util.Optional;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The only code that calls an AI provider (trd 7.5). Scrubs the prompt, asks for JSON only, applies the timeout,
 * validates the reply, retries once, and obeys the {@link AiGate}. An empty result means "unavailable": the caller
 * switches to its fallback. Logs only provider, duration and error kind (trd 6.5).
 */
@Component
public class AiClient {
    private static final Logger log = LoggerFactory.getLogger(AiClient.class);
    private static final String JSON_ONLY = "\n\nReply with a single JSON object only. No prose, no code fences.";
    private static final int ATTEMPTS = 2; // first try plus one retry
    static final int MAX_WAIT_SECONDS = 60;

    private final AiTransport transport;
    private final AiGate gate;
    private final JsonMapper json;
    private final Duration timeout;
    private final Duration whisperTimeout;

    public AiClient(AiTransport transport, AiGate gate, JsonMapper json,
                    @Value("${app.ai.timeout-seconds:20}") long timeoutSeconds,
                    @Value("${app.ai.whisper-timeout-seconds:40}") long whisperTimeoutSeconds) {
        this.transport = transport;
        this.gate = gate;
        this.json = json;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        this.whisperTimeout = Duration.ofSeconds(whisperTimeoutSeconds);
    }

    /** Sends the scrubbed prompt, returns the reply once it parses and passes {@code valid}. */
    public Optional<JsonNode> json(Provider provider, String prompt, Predicate<JsonNode> valid) {
        String body = Scrubber.scrub(prompt) + JSON_ONLY;
        return run(provider, () -> transport.send(provider, body, timeout), valid, null);
    }

    /**
     * Like {@link #json}, but a rate-limit reply that carries Retry-After is waited out once (at most {@link #MAX_WAIT_SECONDS}
     * seconds, through {@code sleepMillis}) and the call is repeated. A second limit reply puts the provider on cooldown.
     */
    public Optional<JsonNode> jsonPatient(Provider provider, String prompt, Predicate<JsonNode> valid, LongConsumer sleepMillis) {
        String body = Scrubber.scrub(prompt) + JSON_ONLY;
        return run(provider, () -> transport.send(provider, body, timeout), valid, sleepMillis);
    }

    /** Whether a call to this provider could work now: it has a key and is not on cooldown. */
    public boolean available(Provider provider) { return transport.configured(provider) && gate.available(provider); }

    /** Sends audio to Groq Whisper (never stored). */
    public Optional<JsonNode> transcribe(byte[] audio, Predicate<JsonNode> valid) {
        return run(Provider.GROQ_WHISPER, () -> transport.transcribe(audio, whisperTimeout), valid, null);
    }

    private Optional<JsonNode> run(Provider provider, Supplier<String> send, Predicate<JsonNode> valid, LongConsumer sleepMillis) {
        boolean waited = false;
        for (int i = 0; i < ATTEMPTS; i++) {
            if (!gate.available(provider)) {
                log.info("ai provider={} unavailable (cooldown)", provider.id());
                return Optional.empty();
            }
            long start = System.nanoTime();
            try {
                JsonNode reply = parse(send.get());
                long ms = (System.nanoTime() - start) / 1_000_000;
                if (reply != null && valid.test(reply)) {
                    log.info("ai provider={} ok ms={}", provider.id(), ms);
                    return Optional.of(reply);
                }
                log.warn("ai provider={} invalid-json ms={}", provider.id(), ms);
            } catch (AiFailure f) {
                log.warn("ai provider={} failure={}", provider.id(), f.kind());
                switch (f.kind()) {
                    case RATE_LIMIT -> {
                        if (sleepMillis != null && !waited && f.retryAfterSeconds() > 0) { // honor Retry-After once, without using up an attempt
                            waited = true;
                            sleepMillis.accept(Math.min(f.retryAfterSeconds(), MAX_WAIT_SECONDS) * 1000L);
                            i--;
                            continue;
                        }
                        gate.rateLimited(provider);
                        return Optional.empty();
                    }
                    case DAILY_LIMIT -> { gate.dailyLimited(provider); return Optional.empty(); }
                    case NOT_CONFIGURED, OTHER -> { return Optional.empty(); }
                    case SERVER_ERROR, TIMEOUT -> { /* retry */ }
                }
            }
        }
        return Optional.empty();
    }

    private JsonNode parse(String text) {
        try {
            // models sometimes wrap JSON in a code fence despite the instruction
            return json.readTree(text.strip().replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", ""));
        } catch (JacksonException e) {
            return null; // the exception message can quote the reply, so it is dropped
        }
    }
}
