package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import tools.jackson.databind.JsonNode;

/** The grading rules (prd Section 9, trd 7.4): marks, matching, checklists, the evidence rule, mastery and the report numbers. Pure, except {@link #scored}. */
final class Grading {
    private Grading() {}

    // ---- objective parts ----

    /** {@code 2 x (correct picked - wrong picked) / correct options}, never below 0, rounded to the nearest 0.5. */
    static double multipleCorrectMarks(Set<String> picked, Set<String> correct) {
        if (correct.isEmpty()) return 0;
        long right = picked.stream().filter(correct::contains).count(), wrong = picked.size() - right;
        double raw = Math.max(0, 2.0 * (right - wrong) / correct.size());
        return Math.round(raw * 2) / 2.0;
    }

    /** Trims and ignores case against the accepted wordings of one blank. */
    static boolean blankMatches(String typed, List<String> accepted) {
        String t = typed == null ? "" : typed.strip();
        return !t.isEmpty() && accepted.stream().anyMatch(a -> a.strip().equalsIgnoreCase(t));
    }

    /** Marks per blank: one blank is worth 2 marks, two blanks 1 mark each. */
    static double marksPerBlank(int blankCount) { return 2.0 / blankCount; }

    // ---- brief and voice checklists ----

    /** One yes/no item. {@code quote} is the proof taken from the answer, or null. */
    record Tick(String item, String label, boolean passed, String quote) {}

    static final String GENERIC_FACT = "Makes a correct point about the topic";

    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("explains_why", "Explains why"), Map.entry("consistent_with_resume", "Consistent with my resume"),
            Map.entry("concrete_example", "Gives a concrete example from my project"), Map.entry("own_role", "Shows my own role (\"I\", not \"we\")"),
            Map.entry("situation_task", "Describes the situation and task"), Map.entry("situation", "Describes the situation"), Map.entry("task", "Describes the task"),
            Map.entry("action", "Describes the action I took"), Map.entry("result", "States the result"), Map.entry("lesson_learned", "Says what I learned"),
            Map.entry("concrete_detail", "Gives a measurable or concrete detail"), Map.entry("answers_question", "Answers the question that was asked"),
            Map.entry("clear_and_focused", "Is clear and focused"));

    /** Item keys by question: the number equals the marks available (5, or 10 for a recorded voice answer). Missing key facts are padded with generic checks. */
    static List<String> itemKeys(boolean voiceRecorded, boolean behavioral) {
        if (voiceRecorded && behavioral) return List.of("situation", "task", "action", "result", "own_role", "concrete_detail", "lesson_learned", "consistent_with_resume", "answers_question", "clear_and_focused");
        if (voiceRecorded) return List.of("key_fact_1", "key_fact_2", "key_fact_3", "key_fact_4", "key_fact_5", "key_fact_6", "explains_why", "concrete_example", "own_role", "consistent_with_resume");
        if (behavioral) return List.of("situation_task", "action", "result", "own_role", "lesson_learned");
        return List.of("key_fact_1", "key_fact_2", "key_fact_3", "explains_why", "consistent_with_resume");
    }

    /** The label shown for an item. A key-fact item shows the fact itself (the answer key), or a generic line when the point has fewer facts. */
    static String label(String item, List<String> facts) {
        if (item.startsWith("key_fact_")) {
            int i = Integer.parseInt(item.substring("key_fact_".length())) - 1;
            return i < facts.size() ? "Mentions: " + facts.get(i) : GENERIC_FACT;
        }
        return LABELS.getOrDefault(item, item);
    }

    /** Lowercase, trimmed, whitespace collapsed: the form in which a quote is looked up in the answer. */
    static String norm(String s) { return s == null ? "" : s.strip().toLowerCase(Locale.ROOT).replaceAll("\\s+", " "); }

    /**
     * Turns the AI reply into ticks. The evidence rule: a "yes" whose quote is not found in the answer (after trimming and
     * lowercasing) becomes "no". An item the AI left out is "no". The AI's own score, if any, is ignored.
     */
    static List<Tick> applyEvidence(JsonNode reply, List<String> keys, List<String> facts, String answer) {
        Map<String, JsonNode> byItem = new LinkedHashMap<>();
        JsonNode items = reply == null ? null : reply.path("items");
        if (items != null && items.isArray()) for (JsonNode n : items) byItem.putIfAbsent(n.path("item").asString(""), n);
        String haystack = norm(answer);
        List<Tick> out = new ArrayList<>();
        for (String key : keys) {
            JsonNode n = byItem.get(key);
            String quote = n == null || !n.path("quote").isString() ? "" : n.path("quote").asString().strip();
            boolean passed = n != null && n.path("passed").asBoolean(false) && !quote.isEmpty() && haystack.contains(norm(quote));
            out.add(new Tick(key, label(key, facts), passed, passed ? quote : null));
        }
        return out;
    }

    /** Marks are the number of ticks, always summed here. */
    static int score(List<Tick> ticks) { return (int) ticks.stream().filter(Tick::passed).count(); }

    /** A reply is worth keeping when it lists every wanted item (a retry is cheaper than a half-graded answer). */
    static boolean complete(JsonNode reply, List<String> keys) {
        JsonNode items = reply.path("items");
        if (!items.isArray()) return false;
        Set<String> have = new java.util.HashSet<>();
        for (JsonNode n : items) have.add(n.path("item").asString(""));
        return have.containsAll(keys);
    }

    // ---- mastery ----

    /** The mark that counts as mastered: 2 of 2, 2 of 2, 4 of 5, 8 of 10, and 4 of 5 for a typed voice answer. */
    static double masteryMark(String type, double maxMarks) {
        return switch (type) {
            case "multiple_correct", "fill_blank" -> maxMarks;
            default -> maxMarks * 0.8;
        };
    }

    static boolean mastered(String type, double maxMarks, double score) { return score >= masteryMark(type, maxMarks); }

    /** Nominal marks of one question of a part (a typed voice answer is lower, see SessionService). */
    static int nominalMarks(String type) {
        return switch (type) {
            case "multiple_correct", "fill_blank" -> SessionService.MC_MARKS;
            case "brief" -> SessionService.BRIEF_MARKS;
            default -> SessionService.VOICE_MARKS;
        };
    }

    // ---- report numbers ----

    /** One graded attempt as the report sees it. */
    record Scored(String type, double maxMarks, double score, Long pointId, String pointText) {
        boolean mastered() { return Grading.mastered(type, maxMarks, score); }
    }

    /** {@code byPart} is part to [marks, nominal maximum] in session order; {@code weakest} is up to 3 resume points behind the lowest-scoring unmastered questions. */
    record Summary(Map<String, double[]> byPart, int mastered, List<Scored> weakest) {}

    static Summary summarize(List<Scored> all) {
        Map<String, double[]> byPart = new LinkedHashMap<>();
        for (Scored s : all) {
            double[] m = byPart.computeIfAbsent(s.type(), k -> new double[2]);
            m[0] += s.score();
            m[1] += nominalMarks(s.type());
        }
        Set<Long> seen = new java.util.HashSet<>();
        List<Scored> weakest = all.stream().filter(s -> !s.mastered() && s.pointId() != null)
                .sorted(java.util.Comparator.comparingDouble(s -> s.score() / s.maxMarks()))
                .filter(s -> seen.add(s.pointId())).limit(3).toList();
        return new Summary(byPart, (int) all.stream().filter(Scored::mastered).count(), weakest);
    }

    /** The graded attempts of a session (all must be marked), in session order. */
    static List<Scored> scored(NamedParameterJdbcTemplate jdbc, long sessionId) {
        return jdbc.query("""
                SELECT q.type, a.max_marks, a.score, q.resume_point_id, p.text FROM attempt a JOIN question q ON q.id = a.question_id
                LEFT JOIN resume_point p ON p.id = q.resume_point_id WHERE a.session_id = :s AND a.score IS NOT NULL ORDER BY a.position""", Map.of("s", sessionId),
                (rs, i) -> new Scored(rs.getString("type"), rs.getDouble("max_marks"), rs.getDouble("score"),
                        rs.getObject("resume_point_id") == null ? null : rs.getLong("resume_point_id"), rs.getString("text")));
    }
}
