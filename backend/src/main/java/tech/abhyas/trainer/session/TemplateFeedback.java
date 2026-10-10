package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tech.abhyas.trainer.session.Grading.Tick;

/** Feedback and report text written by Java alone, used when Groq wrote no text (trd 7.5). Same shape as the AI text, same tone (UI-UX Section 9). */
final class TemplateFeedback {
    private TemplateFeedback() {}

    /** {@code stats} is the voice stats map of a recorded answer, or null. */
    static Map<String, Object> feedback(List<Tick> ticks, List<String> facts, Map<String, Object> stats) {
        List<String> got = new ArrayList<>(), missed = new ArrayList<>();
        for (Tick t : ticks) (t.passed() ? got : missed).add(t.label());
        boolean missedFact = ticks.stream().anyMatch(t -> !t.passed() && t.item().startsWith("key_fact_"));
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("text", got.size() + " of " + ticks.size() + " items covered." + (got.isEmpty() ? "" : " Covered: " + String.join("; ", got.stream().limit(4).toList()) + "."));
        out.put("whatToFix", missed.isEmpty() ? "Nothing to fix here. Keep this level of detail."
                : "Add: " + String.join("; ", missed.stream().limit(3).toList()) + "."
                        + (missedFact && !facts.isEmpty() ? " Key facts to mention: " + String.join("; ", facts.stream().limit(3).toList()) + "." : ""));
        out.put("deliveryComment", stats == null ? null : delivery(stats));
        return out;
    }

    private static String delivery(Map<String, Object> s) {
        int wpm = ((Number) s.get("wordsPerMinute")).intValue(), fillers = ((Number) s.get("fillerCount")).intValue();
        String pace = wpm > 170 ? "faster than the usual 120 to 160" : wpm < 100 ? "slower than the usual 120 to 160" : "within the usual 120 to 160";
        return "Pace: " + wpm + " words per minute, " + pace + ". Filler words: " + fillers + ".";
    }

    static Map<String, Object> report(double total, double max, int mastered, int questions, List<String> weakest) {
        int pct = max == 0 ? 0 : (int) Math.round(100 * total / max);
        String headline = pct >= 80 ? "Strong session. Keep this up."
                : pct >= 50 ? "Good progress. A few points still need work."
                : "A start to build on. Practicing again will strengthen these points.";
        String summary = "You scored " + fmt(total) + " of " + fmt(max) + " (" + pct + "%). " + mastered + " of " + questions + " questions are mastered."
                + (weakest.isEmpty() ? "" : " Points to practice next: " + String.join("; ", weakest) + ".");
        return Map.of("headline", headline, "summary", summary);
    }

    static String fmt(double d) { return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d); }
}
