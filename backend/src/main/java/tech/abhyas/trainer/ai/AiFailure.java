package tech.abhyas.trainer.ai;

/** A failed provider call. Carries only the kind, never the request or reply, so it is safe to log. */
public class AiFailure extends RuntimeException {
    public enum Kind { SERVER_ERROR, TIMEOUT, RATE_LIMIT, DAILY_LIMIT, NOT_CONFIGURED, OTHER }

    private final Kind kind;

    public AiFailure(Kind kind) {
        super(kind.name(), null, false, false);
        this.kind = kind;
    }

    public Kind kind() { return kind; }
}
