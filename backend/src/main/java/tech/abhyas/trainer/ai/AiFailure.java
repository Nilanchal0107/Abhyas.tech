package tech.abhyas.trainer.ai;

/** A failed provider call. Carries only the kind, never the request or reply, so it is safe to log. */
public class AiFailure extends RuntimeException {
    public enum Kind { SERVER_ERROR, TIMEOUT, RATE_LIMIT, DAILY_LIMIT, NOT_CONFIGURED, OTHER }

    private final Kind kind;
    private final int retryAfterSeconds;

    public AiFailure(Kind kind) { this(kind, 0); }

    /** {@code retryAfterSeconds} is the provider's Retry-After header on a rate limit, or 0 when it sent none. */
    public AiFailure(Kind kind, int retryAfterSeconds) {
        super(kind.name(), null, false, false);
        this.kind = kind;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Kind kind() { return kind; }

    public int retryAfterSeconds() { return retryAfterSeconds; }
}
