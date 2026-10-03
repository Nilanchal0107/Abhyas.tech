package tech.abhyas.trainer.session;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.session.Grading.Tick;
import tools.jackson.databind.JsonNode;

/**
 * Gemma's feedback and report text for one session (trd 7.4, steps 7 and 8). Calls go out one at a time with a fixed gap between
 * them (Gemma's free limit is per minute). An empty result means "write the template instead". Gemma never changes a mark.
 * One instance per grading run, because it remembers whether a call was already made.
 */
final class GemmaFeedback {
    static final long GAP_MILLIS = 7_000;
    static final int MAX_TEXT = 1500;

    private final AiClient ai;
    private final LongConsumer sleep;
    private final long gapMillis;
    private boolean called;

    GemmaFeedback(AiClient ai, LongConsumer sleepMillis, long gapMillis) {
        this.ai = ai;
        this.sleep = sleepMillis;
        this.gapMillis = gapMillis;
    }

    Optional<Map<String, Object>> feedback(String prompt, boolean voice) {
        return call(prompt, r -> clean(r.path("text")) != null && clean(r.path("whatToFix")) != null).map(r -> {
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("text", clean(r.path("text")));
            out.put("whatToFix", clean(r.path("whatToFix")));
            out.put("deliveryComment", voice ? clean(r.path("deliveryComment")) : null);
            return out;
        });
    }

    Optional<Map<String, Object>> report(String prompt) {
        return call(prompt, r -> clean(r.path("headline")) != null && clean(r.path("summary")) != null)
                .map(r -> Map.<String, Object>of("headline", clean(r.path("headline")), "summary", clean(r.path("summary"))));
    }

    private Optional<JsonNode> call(String prompt, Predicate<JsonNode> valid) {
        if (!ai.available(Provider.GEMMA)) return Optional.empty(); // no call, so no wait either
        if (called) sleep.accept(gapMillis);
        called = true;
        return ai.jsonPatient(Provider.GEMMA, prompt, valid, sleep);
    }

    private static String clean(JsonNode n) {
        if (!n.isString()) return null;
        String t = n.asString().strip();
        return t.isEmpty() ? null : t.length() > MAX_TEXT ? t.substring(0, MAX_TEXT) : t;
    }

    // ---- prompts ----

    static String feedbackPrompt(String question, String answer, List<Tick> ticks, List<String> facts, Map<String, Object> stats) {
        StringBuilder sb = new StringBuilder("You are a kind, concrete interview coach. Write short feedback for a candidate's answer. The marks are already decided; do not change or mention a different mark.\n")
                .append("Question: ").append(question).append("\nCandidate answer: ").append(answer).append("\nChecklist result:\n");
        for (Tick t : ticks) sb.append("- ").append(t.passed() ? "yes" : "no").append(": ").append(t.label()).append('\n');
        if (!facts.isEmpty()) sb.append("Key facts a good answer mentions: ").append(String.join(" | ", facts)).append('\n');
        if (stats != null) sb.append("Delivery numbers: ").append(stats).append('\n');
        return sb.append("Reply as {\"text\":\"two or three sentences about what was good and what was missing\",\"whatToFix\":\"one concrete sentence\"")
                .append(stats != null ? ",\"deliveryComment\":\"one sentence about pace and filler words\"" : "").append("}. Never blame the candidate.").toString();
    }

    static String reportPrompt(double total, double max, int mastered, int questions, Map<String, double[]> byPart, List<String> weakest) {
        StringBuilder sb = new StringBuilder("You are a kind, concrete interview coach. Write a short report for a practice session. The numbers are final.\n")
                .append("Total: ").append(TemplateFeedback.fmt(total)).append(" of ").append(TemplateFeedback.fmt(max)).append(". Mastered: ").append(mastered).append(" of ").append(questions).append(".\n");
        byPart.forEach((part, m) -> sb.append(part).append(": ").append(TemplateFeedback.fmt(m[0])).append(" of ").append(TemplateFeedback.fmt(m[1])).append('\n'));
        if (!weakest.isEmpty()) sb.append("Weakest resume points: ").append(String.join(" | ", weakest)).append('\n');
        return sb.append("Reply as {\"headline\":\"one supportive sentence\",\"summary\":\"two or three neutral sentences\"}.").toString();
    }
}
