package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.session.Grading.Tick;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Grades a submitted session in the background (trd 7.4). One single-thread worker, so Gemma's calls are sent one at a time.
 * Stage A marks every answer and sets the total; stage B writes feedback and the report, filling rows in gradually. Both stages
 * pick up only what is still open, so a restart finishes the work where it stopped. Logs only ids and error classes (trd 6.5).
 */
@Service
public class GradingService {
    private static final Logger log = LoggerFactory.getLogger(GradingService.class);
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final NamedParameterJdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final AiClient ai;
    private final long gapMillis;
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "grading");
        t.setDaemon(true);
        return t;
    });
    private final Set<Long> queued = java.util.concurrent.ConcurrentHashMap.newKeySet(); // sessions waiting or running, so a double submit queues once

    public GradingService(NamedParameterJdbcTemplate jdbc, TransactionTemplate tx, AiClient ai, @Value("${app.grading.gemma-gap-millis:7000}") long gapMillis) {
        this.jdbc = jdbc;
        this.tx = tx;
        this.ai = ai;
        this.gapMillis = gapMillis;
    }

    /** Hands a completed session to the worker. */
    public void start(long sessionId) {
        if (queued.add(sessionId)) worker.execute(() -> {
            try { run(sessionId); }
            catch (RuntimeException e) { log.warn("grading session={} stopped: {}", sessionId, e.getClass().getSimpleName()); } // never the message: it can quote the data
            finally { queued.remove(sessionId); }
        });
    }

    /** On startup: every completed session that still has something pending is finished (trd 7.4 recovery). */
    @EventListener(ApplicationReadyEvent.class)
    void resumeUnfinished() {
        for (long id : jdbc.queryForList("""
                SELECT s.id FROM practice_session s WHERE s.status = 'completed' AND (s.total_marks IS NULL OR s.report_status = 'pending'
                  OR EXISTS (SELECT 1 FROM attempt a WHERE a.session_id = s.id AND a.feedback_status = 'pending'))""", Map.of(), Long.class)) start(id);
    }

    void run(long sessionId) {
        markAnswers(sessionId);
        writeFeedback(sessionId);
    }

    // ---- loading ----

    private record Att(long id, int position, double maxMarks, String via, String selected, String blanks, String text, String transcript, String stats, Double score,
                       String checklist, String feedbackStatus, long questionId, String type, String theme, String questionText, String correct, String accepted,
                       String keyFacts, String keywords, Long pointId, String pointText) {
        boolean answered() { return via != null; }
        boolean behavioral() { return theme != null; }
        boolean voiceRecorded() { return type.equals("voice") && "voice".equals(via); }
        String answerText() { return voiceRecorded() ? transcript : text; }
    }

    private List<Att> load(long sessionId) {
        return jdbc.query("""
                SELECT a.id, a.position, a.max_marks, a.answered_via, a.selected_options, a.blank_answers, a.answer_text, a.transcript, a.voice_stats, a.score, a.checklist, a.feedback_status,
                       q.id AS qid, q.type, q.theme, q.text, q.correct_options, q.accepted_answers, q.key_facts, q.keywords, q.resume_point_id, p.text AS point_text
                FROM attempt a JOIN question q ON q.id = a.question_id LEFT JOIN resume_point p ON p.id = q.resume_point_id
                WHERE a.session_id = :s ORDER BY a.position""", Map.of("s", sessionId), (rs, i) -> new Att(rs.getLong("id"), rs.getInt("position"), rs.getDouble("max_marks"),
                rs.getString("answered_via"), rs.getString("selected_options"), rs.getString("blank_answers"), rs.getString("answer_text"), rs.getString("transcript"),
                rs.getString("voice_stats"), rs.getObject("score") == null ? null : rs.getDouble("score"), rs.getString("checklist"), rs.getString("feedback_status"),
                rs.getLong("qid"), rs.getString("type"), rs.getString("theme"), rs.getString("text"), rs.getString("correct_options"), rs.getString("accepted_answers"),
                rs.getString("key_facts"), rs.getString("keywords"), rs.getObject("resume_point_id") == null ? null : rs.getLong("resume_point_id"), rs.getString("point_text")));
    }

    private static List<String> strings(String json) {
        List<String> out = new ArrayList<>();
        if (json != null) for (JsonNode n : JSON.readTree(json)) if (n.isString()) out.add(n.asString());
        return out;
    }

    /** The answer key of a technical brief or voice question: its own facts (bank rows), else those drafted for its resume point. Behavioral ones have none. */
    private Map<Long, List<String>> factsFor(List<Att> atts) {
        Set<Long> points = new LinkedHashSet<>();
        for (Att a : atts) if (a.pointId() != null && !a.behavioral()) points.add(a.pointId());
        return KeyFacts.load(jdbc, points);
    }

    private static List<String> facts(Att a, Map<Long, List<String>> byPoint) {
        if (a.behavioral()) return List.of();
        List<String> own = strings(a.keyFacts());
        return !own.isEmpty() ? own : a.pointId() == null ? List.of() : byPoint.getOrDefault(a.pointId(), List.of());
    }

    // ---- stage A: marks ----

    /** Marks every open answer, then sets the total once nothing is left open. */
    void markAnswers(long sessionId) {
        List<Att> all = load(sessionId), todo = all.stream().filter(a -> a.score() == null).toList();
        Map<Long, List<String>> factsByPoint = factsFor(todo);

        List<Att> blanks = new ArrayList<>();
        for (Att a : todo) {
            if (!a.answered()) save(a, 0, null, null, "java", true); // a skipped question is zero
            else switch (a.type()) {
                case "multiple_correct" -> {
                    Set<String> picked = new LinkedHashSet<>(strings(a.selected())), correct = new LinkedHashSet<>(strings(a.correct()));
                    save(a, Grading.multipleCorrectMarks(picked, correct), null, null, "java", true);
                }
                case "fill_blank" -> blanks.add(a);
                default -> markChecklist(a, facts(a, factsByPoint));
            }
        }
        markBlanks(blanks);

        jdbc.update("""
                UPDATE practice_session SET total_marks = (SELECT COALESCE(SUM(score), 0) FROM attempt WHERE session_id = :s)
                WHERE id = :s AND NOT EXISTS (SELECT 1 FROM attempt WHERE session_id = :s AND score IS NULL)""", Map.of("s", sessionId));
    }

    /** Java matches every blank; all blanks it marked wrong go to Groq in one call, and stay wrong if Groq is unavailable. */
    private void markBlanks(List<Att> blanks) {
        if (blanks.isEmpty()) return;
        Map<Long, List<List<String>>> accepted = new LinkedHashMap<>();
        Map<Long, List<String>> typed = new LinkedHashMap<>();
        Map<Long, boolean[]> ok = new LinkedHashMap<>();
        record Ref(Att att, int blank) {}
        List<Ref> wrong = new ArrayList<>();
        for (Att a : blanks) {
            List<List<String>> acc = new ArrayList<>();
            for (JsonNode one : JSON.readTree(a.accepted())) acc.add(strings(one.toString()));
            List<String> given = strings(a.blanks());
            boolean[] right = new boolean[acc.size()];
            for (int i = 0; i < acc.size(); i++) {
                String answer = i < given.size() ? given.get(i) : "";
                right[i] = Grading.blankMatches(answer, acc.get(i));
                if (!right[i] && !answer.isBlank()) wrong.add(new Ref(a, i));
            }
            accepted.put(a.id(), acc);
            typed.put(a.id(), given);
            ok.put(a.id(), right);
        }
        if (!wrong.isEmpty() && ai.available(Provider.GROQ_CHAT)) {
            StringBuilder sb = new StringBuilder("A candidate filled blanks (________) in fill-in-the-blank interview questions. Java compared each answer with the accepted wordings and found no match. "
                    + "For each numbered item decide whether the candidate's answer is still correct in meaning (a synonym, an equivalent term or a small spelling slip counts; a different thing does not).\n");
            for (int n = 0; n < wrong.size(); n++) {
                Ref r = wrong.get(n);
                sb.append(n).append(". Question: ").append(r.att().questionText()).append(" | Blank ").append(r.blank() + 1).append(" accepted: ")
                        .append(String.join(", ", accepted.get(r.att().id()).get(r.blank()))).append(" | Candidate answer: ").append(typed.get(r.att().id()).get(r.blank())).append('\n');
            }
            sb.append("Reply as {\"results\":[{\"id\":0,\"correct\":true}]} with one entry per item.");
            Optional<JsonNode> reply = ai.json(Provider.GROQ_CHAT, sb.toString(), r -> r.path("results").isArray());
            if (reply.isPresent()) {
                for (JsonNode res : reply.get().path("results")) {
                    int n = res.path("id").asInt(-1);
                    if (n >= 0 && n < wrong.size() && res.path("correct").asBoolean(false)) ok.get(wrong.get(n).att().id())[wrong.get(n).blank()] = true;
                }
            }
        }
        for (Att a : blanks) {
            boolean[] right = ok.get(a.id());
            double each = Grading.marksPerBlank(right.length), score = 0;
            List<Map<String, Object>> results = new ArrayList<>();
            for (boolean b : right) { if (b) score += each; results.add(Map.of("correct", b)); }
            save(a, score, null, JSON.writeValueAsString(results), "java", true);
        }
    }

    /** Groq ticks the checklist and Java enforces the evidence rule; when Groq cannot, keyword matching does, and that mark does not count toward mastery. */
    private void markChecklist(Att a, List<String> facts) {
        List<String> keys = Grading.itemKeys(a.voiceRecorded(), a.behavioral());
        String answer = a.answerText() == null ? "" : a.answerText();
        List<Tick> ticks = null;
        if (ai.available(Provider.GROQ_CHAT)) {
            Optional<JsonNode> reply = ai.json(Provider.GROQ_CHAT, checklistPrompt(a, keys, facts, answer), r -> Grading.complete(r, keys));
            if (reply.isPresent()) ticks = Grading.applyEvidence(reply.get(), keys, facts, answer);
        }
        boolean byAi = ticks != null;
        if (!byAi) ticks = KeywordGrader.grade(answer, keys, facts, strings(a.keywords()), a.pointText() == null ? a.questionText() : a.pointText(), a.questionText());
        save(a, Grading.score(ticks), JSON.writeValueAsString(ticks.stream().map(GradingService::tickMap).toList()), null, byAi ? "ai" : "fallback", byAi);
    }

    private static Map<String, Object> tickMap(Tick t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("item", t.item());
        m.put("label", t.label());
        m.put("passed", t.passed());
        m.put("quote", t.quote());
        return m;
    }

    private static String checklistPrompt(Att a, List<String> keys, List<String> facts, String answer) {
        StringBuilder sb = new StringBuilder("You grade one interview answer with a yes/no checklist. An item is passed only if the answer clearly shows it, and a passed item needs \"quote\": "
                + "a sentence copied word for word from the candidate answer that proves it. If you cannot quote the answer, passed must be false.\n")
                .append("Question (").append(a.behavioral() ? "behavioral" : "technical").append("): ").append(a.questionText()).append('\n');
        if (a.pointText() != null) sb.append("Resume point the question is about: ").append(a.pointText()).append('\n');
        sb.append("Candidate answer: ").append(answer).append("\nChecklist items:\n");
        for (String k : keys) sb.append("- ").append(k).append(": ").append(Grading.label(k, facts)).append('\n');
        return sb.append("Reply as {\"items\":[{\"item\":\"").append(keys.get(0)).append("\",\"passed\":true,\"quote\":\"...\"}]} and list every item.").toString();
    }

    /** Saves a mark. A counting mark also sets the question's last score and mastered flag (database-schema 3.13). */
    private void save(Att a, double score, String checklist, String blankResults, String gradedBy, boolean counts) {
        tx.executeWithoutResult(t -> {
            jdbc.update("UPDATE attempt SET score = :sc, checklist = :ch, blank_results = :br, graded_by = :g WHERE id = :id",
                    new MapSqlParameterSource("sc", score).addValue("ch", checklist).addValue("br", blankResults).addValue("g", gradedBy).addValue("id", a.id()));
            if (counts) {
                jdbc.update("UPDATE question SET last_score = :sc, mastered = :m WHERE id = :q",
                        new MapSqlParameterSource("sc", score).addValue("m", Grading.mastered(a.type(), a.maxMarks(), score)).addValue("q", a.questionId()));
            }
        });
    }

    // ---- stage B: feedback and report ----

    /** Gemma writes one answer at a time, then the report. Anything it cannot write gets template text. */
    void writeFeedback(long sessionId) {
        GemmaFeedback gemma = new GemmaFeedback(ai, GradingService::sleep, gapMillis);
        List<Att> all = load(sessionId);
        Map<Long, List<String>> factsByPoint = factsFor(all);
        for (Att a : all) {
            if (!"pending".equals(a.feedbackStatus()) || a.score() == null) continue;
            if (a.checklist() == null) { // nothing to write about (an objective or skipped answer)
                jdbc.update("UPDATE attempt SET feedback_status = 'none' WHERE id = :id", Map.of("id", a.id()));
                continue;
            }
            List<Tick> ticks = ticks(a.checklist());
            List<String> facts = facts(a, factsByPoint);
            Map<String, Object> stats = a.voiceRecorded() && a.stats() != null ? statsMap(a.stats()) : null;
            Optional<Map<String, Object>> text = ticks.isEmpty() ? Optional.empty()
                    : gemma.feedback(GemmaFeedback.feedbackPrompt(a.questionText(), a.answerText(), ticks, facts, stats), stats != null);
            boolean ai = text.isPresent();
            Map<String, Object> out = text.orElseGet(() -> TemplateFeedback.feedback(ticks, facts, stats));
            jdbc.update("UPDATE attempt SET feedback_text = :t, feedback_status = :st WHERE id = :id AND feedback_status = 'pending'",
                    Map.of("t", JSON.writeValueAsString(out), "st", ai ? "ready" : "none", "id", a.id()));
        }

        String status = jdbc.queryForObject("SELECT report_status FROM practice_session WHERE id = :s", Map.of("s", sessionId), String.class);
        if (!"pending".equals(status)) return;
        Map<String, Object> row = jdbc.queryForMap("SELECT max_marks, total_marks FROM practice_session WHERE id = :s", Map.of("s", sessionId));
        if (row.get("total_marks") == null) return;
        double total = ((Number) row.get("total_marks")).doubleValue(), max = ((Number) row.get("max_marks")).doubleValue();
        Grading.Summary sum = Grading.summarize(Grading.scored(jdbc, sessionId));
        List<String> weakest = sum.weakest().stream().map(Grading.Scored::pointText).toList();
        Optional<Map<String, Object>> text = gemma.report(GemmaFeedback.reportPrompt(total, max, sum.mastered(), all.size(), sum.byPart(), weakest));
        Map<String, Object> out = text.orElseGet(() -> TemplateFeedback.report(total, max, sum.mastered(), all.size(), weakest));
        jdbc.update("UPDATE practice_session SET report_text = :t, report_status = :st WHERE id = :s",
                Map.of("t", JSON.writeValueAsString(out), "st", text.isPresent() ? "ready" : "none", "s", sessionId));
    }

    private static List<Tick> ticks(String json) {
        List<Tick> out = new ArrayList<>();
        if (json != null) for (JsonNode n : JSON.readTree(json)) {
            out.add(new Tick(n.path("item").asString(), n.path("label").asString(), n.path("passed").asBoolean(), n.path("quote").isString() ? n.path("quote").asString() : null));
        }
        return out;
    }

    private static Map<String, Object> statsMap(String json) {
        JsonNode n = JSON.readTree(json);
        Map<String, Object> m = new LinkedHashMap<>();
        for (String k : List.of("wordsPerMinute", "fillerCount", "wordCount", "durationSeconds")) m.put(k, n.path(k).asInt());
        return m;
    }

    private static void sleep(long millis) {
        if (millis <= 0) return;
        try { Thread.sleep(millis); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
