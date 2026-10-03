package tech.abhyas.trainer.ai;

/** The three AI providers the gate tracks separately (trd 7.5). */
public enum Provider {
    GROQ_CHAT("groq-chat"), GROQ_WHISPER("groq-whisper"), GEMMA("gemma");

    private final String id;

    Provider(String id) { this.id = id; }

    public String id() { return id; }
}
