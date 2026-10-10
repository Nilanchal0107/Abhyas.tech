package tech.abhyas.trainer.session;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Practice sessions (FR-9, FR-10, trd 7.2, 7.3). So far a session holds the multiple-correct, fill-in-the-blank and brief parts: ticket 16
 * add the other parts, 17 the practice bank. While in progress nothing that gives the answers away leaves this class.
 */
@Service
public class SessionService {
    static final int MC_SLOTS = 5, MC_MARKS = 2, FB_SLOTS = 5, FB_MARKS = 2, BRIEF_MARKS = 5, MAX_BRIEF = 5000, VOICE_MARKS = 10, TYPED_VOICE_MARKS = 5, MAX_TRANSCRIPT = 10000, MAX_AUDIO = 5 * 1024 * 1024, MAX_SECONDS = 180, MAX_BLANK = 200;
    private static final JsonMapper JSON = JsonMapper.builder().build();
    static final String BLANK = "________";
    private static final List<String> KEYS = List.of("A", "B", "C", "D");

    private final NamedParameterJdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final AiClient ai;
    private final Clock clock;
    private final PracticeBank bank;
    private final GradingService grading;
    private final Random random = new Random();

    public SessionService(NamedParameterJdbcTemplate jdbc, TransactionTemplate tx, AiClient ai, Clock clock, PracticeBank bank, GradingService grading) {
        this.jdbc = jdbc;
        this.tx = tx;
        this.ai = ai;
        this.clock = clock;
        this.bank = bank;
        this.grading = grading;
    }

    // ---- slot selection (pure) ----

    /** A stored question as seen by slot selection. {@code lastScore} and {@code lastAsked} can be null. */
    record Candidate(long id, boolean mastered, int timesAsked, Double lastScore, LocalDateTime lastAsked, boolean behavioral, boolean project) {
        Candidate(long id, boolean mastered, int timesAsked, Double lastScore, LocalDateTime lastAsked, boolean behavioral) { this(id, mastered, timesAsked, lastScore, lastAsked, behavioral, false); }
    }

    /**
     * Unmastered asked questions first (lowest score, then oldest asked; no score counts as 0), then the rest of the
     * slots at random from new questions and mastered ones that rested for a month (prd Section 6 rules 2 to 4).
     */
    static List<Long> pick(List<Candidate> all, int slots, LocalDateTime now, Random random) {
        List<Long> out = new ArrayList<>();
        all.stream().filter(c -> !c.mastered() && c.timesAsked() > 0)
                .sorted(Comparator.comparingDouble((Candidate c) -> c.lastScore() == null ? 0 : c.lastScore())
                        .thenComparing(c -> c.lastAsked() == null ? LocalDateTime.MIN : c.lastAsked()))
                .limit(slots).forEach(c -> out.add(c.id()));
        List<Long> fresh = new ArrayList<>(all.stream()
                .filter(c -> c.timesAsked() == 0 || (c.mastered() && c.lastAsked() != null && !c.lastAsked().isAfter(now.minusMonths(1))))
                .map(Candidate::id).filter(id -> !out.contains(id)).toList());
        Collections.shuffle(fresh, random);
        for (int i = 0; out.size() < slots && i < fresh.size(); i++) out.add(fresh.get(i));
        return out;
    }

    /** The typed text of a brief answer, or null when it is empty. Up to 5,000 characters (api-contracts 1.2). */
    static String briefText(JsonNode answer) {
        if (answer == null || answer.isNull()) return null;
        JsonNode t = answer.path("text");
        if (!answer.isObject() || !t.isString()) throw invalid("answer", "Type your answer.");
        if (t.asString().length() > MAX_BRIEF) throw invalid("answer", "Use at most 5,000 characters.");
        return t.asString().isBlank() ? null : t.asString();
    }

    /** A saved voice answer: recorded (transcript and stats) or typed instead (text). */
    record VoiceAnswer(String via, String text, String transcript, String stats) {}

    /** Null clears the answer. Shapes follow api-contracts 5, PUT attempts. */
    static VoiceAnswer voiceAnswer(JsonNode answer) {
        if (answer == null || answer.isNull()) return null;
        String via = answer.path("answeredVia").asString("");
        if (!answer.isObject() || !(via.equals("voice") || via.equals("typed"))) throw invalid("answer", "Record your answer or type it.");
        if (via.equals("typed")) {
            String text = briefText(answer);
            return text == null ? null : new VoiceAnswer("typed", text, null, null);
        }
        JsonNode t = answer.path("transcript"), st = answer.path("voiceStats");
        if (!t.isString() || t.asString().isBlank()) throw invalid("answer", "The recording has no text. Record again or type your answer.");
        if (t.asString().length() > MAX_TRANSCRIPT) throw invalid("answer", "The recording is too long.");
        if (st.isObject() && st.size() == 1 && st.path("durationSeconds").isIntegralNumber() && st.path("durationSeconds").asLong() >= 0 && st.path("durationSeconds").asLong() <= MAX_SECONDS + 1) {
            // a Web Speech answer has no timing, only the length the browser measured: Java counts the words
            return new VoiceAnswer("voice", null, t.asString().strip(), JSON.writeValueAsString(VoiceStats.of(t.asString(), st.path("durationSeconds").asLong()).toMap()));
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        for (String k : List.of("wordsPerMinute", "fillerCount", "wordCount", "durationSeconds")) {
            if (!st.path(k).isIntegralNumber() || st.path(k).asLong() < 0 || st.path(k).asLong() > 100000) throw invalid("answer", "The recording numbers are not valid.");
            stats.put(k, st.path(k).asInt());
        }
        return new VoiceAnswer("voice", null, t.asString().strip(), JSON.writeValueAsString(stats));
    }

    // ---- answers (pure validation) ----

    /** The typed words of an answer to a fill-in-the-blank question (one entry per blank), or null when nothing was typed. */
    static List<String> blankAnswers(JsonNode answer, int blankCount) {
        if (answer == null || answer.isNull()) return null;
        JsonNode list = answer.path("blankAnswers");
        if (!answer.isObject() || !list.isArray() || list.size() != blankCount) throw invalid("answer", "Give one answer for each blank.");
        List<String> out = new ArrayList<>();
        for (JsonNode n : list) {
            if (!n.isString()) throw invalid("answer", "Give one answer for each blank.");
            if (n.asString().length() > MAX_BLANK) throw invalid("answer", "Use at most 200 characters for each blank.");
            out.add(n.asString().strip());
        }
        return out.stream().allMatch(String::isEmpty) ? null : out;
    }


    /** The option keys of an answer to a multiple-correct question, or null to clear it. Throws validation for a wrong shape. */
    static List<String> selectedOptions(JsonNode answer, int optionCount) {
        if (answer == null || answer.isNull()) return null;
        JsonNode sel = answer.path("selectedOptions");
        if (!answer.isObject() || !sel.isArray()) throw invalid("answer", "Choose the options you think are correct.");
        Set<String> keys = new LinkedHashSet<>();
        for (JsonNode n : sel) {
            String k = n.isString() ? n.asString() : "";
            if (!KEYS.subList(0, optionCount).contains(k)) throw invalid("answer", "Use only the options shown.");
            keys.add(k);
        }
        return keys.isEmpty() ? null : List.copyOf(keys);
    }

    // ---- start ----

    /** Slots of one kind inside a part: technical from the bank (no theme), behavioral (themed), or a project question the AI wrote for this resume. */
    record Quota(boolean behavioral, boolean project, int slots) {
        Quota(boolean behavioral, int slots) { this(behavioral, false, slots); }
    }

    /** The parts, in session order (trd 7.2, ticket 17). Brief is 3 technical, 2 behavioral and 1 project; voice is 1 behavioral and 2 project. */
    record Part(String type, int marks, List<Quota> quotas) {}
    static final List<Part> PARTS = List.of(
            new Part("multiple_correct", MC_MARKS, List.of(new Quota(false, MC_SLOTS))),
            new Part("fill_blank", FB_MARKS, List.of(new Quota(false, FB_SLOTS))),
            new Part("brief", BRIEF_MARKS, List.of(new Quota(false, 3), new Quota(true, 2), new Quota(false, true, 1))),
            new Part("voice", VOICE_MARKS, List.of(new Quota(true, 1), new Quota(false, true, 2))));

    /** Interview mode (ticket 13): 6 spoken questions, 4 technical (the user's projects first) and 2 behavioral, 10 marks each (5 when typed). */
    static final List<Part> INTERVIEW = List.of(new Part("voice", VOICE_MARKS, List.of(new Quota(false, true, 4), new Quota(true, 2))));

    static final List<String> THEMES = List.of("teamwork", "conflict", "failure", "leadership", "deadline_pressure");

    /** Picks each quota of a part on its own (technical and behavioral questions never fill each other's slots). */
    static List<List<Long>> pickQuotas(List<Candidate> all, List<Quota> quotas, LocalDateTime now, Random random) {
        List<List<Long>> out = new ArrayList<>();
        for (Quota q : quotas) out.add(pick(all.stream().filter(c -> c.behavioral() == q.behavioral() && c.project() == q.project()).toList(), q.slots(), now, random));
        return out;
    }

    /** Builds the slots, generating missing questions first (can take several seconds). */
    public Map<String, Object> start(long userId, long resumeId) { return start(userId, resumeId, "quiz"); }

    public Map<String, Object> start(long userId, long resumeId, String mode) {
        if (!mode.equals("quiz") && !mode.equals("interview")) throw invalid("mode", "Use quiz or interview.");
        jdbc.queryForList("SELECT id FROM resume WHERE id = :r AND user_id = :me", Map.of("r", resumeId, "me", userId))
                .stream().findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that resume."));
        long open = openSession(userId);
        if (open > 0) throw sessionOpen(open);

        LocalDateTime now = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));
        List<Long> slots = new ArrayList<>(); // question ids in session order
        List<Integer> marks = new ArrayList<>();
        // always 19 questions, 80 marks (ticket 19): with Whisper down the browser's Web Speech API gives the transcript of a voice answer
        for (Part part : mode.equals("interview") ? INTERVIEW : PARTS) {
            List<List<Long>> chosen = pickQuotas(candidates(userId, resumeId, part.type()), part.quotas(), now, random);
            boolean short_ = missing(part, chosen, false) + missing(part, chosen, true) > 0;
            if (short_) {
                // no AI call here: project questions were written once after the save (ProjectQuestionService), the rest comes from the reviewed bank (skill-bank D3)
                int stillTechnical = missing(part, chosen, false), stillBehavioral = missing(part, chosen, true);
                if (stillTechnical + stillBehavioral > 0) { // then the practice bank, then generic templates, with no message to the user (FR-20)
                    bank.fill(resumeId, part.type(), stillTechnical, stillBehavioral);
                    List<Candidate> all = candidates(userId, resumeId, part.type());
                    chosen = pickQuotas(all, part.quotas(), now, random);
                    topUpProject(part, chosen, all, now);
                    relax(all, part.quotas(), chosen, random);
                }
            }
            for (List<Long> ids : chosen) for (Long q : ids) { slots.add(q); marks.add(part.marks()); }
        }
        // only a resume (and a bank) with nothing at all to ask ends up here
        if (slots.isEmpty()) throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ai-unavailable", "We could not prepare your questions right now. Please try again later.");

        int max = marks.stream().mapToInt(Integer::intValue).sum();
        long sessionId;
        try {
            sessionId = tx.execute(s -> {
                var keys = new org.springframework.jdbc.support.GeneratedKeyHolder();
                jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, mode, voice_skipped, max_marks) VALUES (:me, :r, 'in_progress', :mode, :skip, :max)",
                        new MapSqlParameterSource("me", userId).addValue("r", resumeId).addValue("mode", mode).addValue("skip", false).addValue("max", max), keys);
                long id = keys.getKey().longValue();
                for (int i = 0; i < slots.size(); i++) {
                    jdbc.update("INSERT INTO attempt (session_id, question_id, position, max_marks) VALUES (:s, :q, :pos, :m)",
                            Map.of("s", id, "q", slots.get(i), "pos", i + 1, "m", marks.get(i)));
                    jdbc.update("UPDATE question SET times_asked = times_asked + 1, last_asked_at = UTC_TIMESTAMP(3) WHERE id = :q", Map.of("q", slots.get(i)));
                    jdbc.update("""
                            INSERT INTO bank_performance (user_id, bank_question_id, times_asked, last_asked_at)
                            SELECT :me, bank_question_id, 1, UTC_TIMESTAMP(3) FROM question WHERE id = :q AND bank_question_id IS NOT NULL
                            ON DUPLICATE KEY UPDATE bank_performance.times_asked = bank_performance.times_asked + 1, bank_performance.last_asked_at = UTC_TIMESTAMP(3)""", Map.of("me", userId, "q", slots.get(i)));
                }
                jdbc.update("UPDATE resume SET last_used_at = UTC_TIMESTAMP(3) WHERE id = :r", Map.of("r", resumeId));
                return id;
            });
        } catch (DuplicateKeyException e) { // two starts at once: the unique open-session index decides
            throw sessionOpen(openSession(userId));
        }
        return detail(userId, sessionId);
    }

    /**
     * Last resort for a quota the bank and the templates could not fill: any stored question of the right kind that is not already in
     * the part, even one that is still resting, so the session keeps its size (ticket 19). Nothing repeats inside a session.
     */
    static void relax(List<Candidate> all, List<Quota> quotas, List<List<Long>> chosen, Random random) {
        Set<Long> used = new LinkedHashSet<>();
        chosen.forEach(used::addAll);
        for (int i = 0; i < quotas.size(); i++) {
            int lack = quotas.get(i).slots() - chosen.get(i).size();
            if (lack <= 0) continue;
            boolean behavioral = quotas.get(i).behavioral();
            List<Long> spare = new ArrayList<>(all.stream().filter(c -> c.behavioral() == behavioral && !used.contains(c.id())).map(Candidate::id).toList());
            Collections.shuffle(spare, random);
            for (Long id : spare.subList(0, Math.min(lack, spare.size()))) { chosen.get(i).add(id); used.add(id); }
        }
    }

    /** A project quota the AI has not filled (no project questions yet) takes extra bank questions instead, so the part keeps its size. */
    private void topUpProject(Part part, List<List<Long>> chosen, List<Candidate> all, LocalDateTime now) {
        for (int i = 0; i < chosen.size(); i++) {
            int lack = part.quotas().get(i).slots() - chosen.get(i).size();
            if (!part.quotas().get(i).project() || lack <= 0) continue;
            Set<Long> used = new LinkedHashSet<>();
            chosen.forEach(used::addAll);
            chosen.get(i).addAll(pick(all.stream().filter(c -> !c.behavioral() && !c.project() && !used.contains(c.id())).toList(), lack, now, random));
        }
    }

    /** Slots still missing in the technical (or behavioral) quotas of a part. */
    private static int missing(Part part, List<List<Long>> chosen, boolean behavioral) {
        int n = 0;
        for (int i = 0; i < chosen.size(); i++) if (part.quotas().get(i).behavioral() == behavioral) n += part.quotas().get(i).slots() - chosen.get(i).size();
        return n;
    }

    /** A bank question's performance is the user's own row in bank_performance (none yet means new); a project question keeps its own columns. */
    private List<Candidate> candidates(long userId, long resumeId, String type) {
        return jdbc.query("""
                SELECT q.id, IF(q.bank_question_id IS NULL, q.mastered, COALESCE(bp.mastered, FALSE)) AS mastered,
                       IF(q.bank_question_id IS NULL, q.times_asked, COALESCE(bp.times_asked, 0)) AS times_asked,
                       IF(q.bank_question_id IS NULL, q.last_score, bp.last_score) AS last_score,
                       IF(q.bank_question_id IS NULL, q.last_asked_at, bp.last_asked_at) AS last_asked_at, q.theme IS NOT NULL AS behavioral, (q.source = 'ai' AND q.theme IS NULL) AS project
                FROM question q LEFT JOIN bank_performance bp ON bp.user_id = :me AND bp.bank_question_id = q.bank_question_id
                WHERE q.resume_id = :r AND q.type = :t""",
                Map.of("me", userId, "r", resumeId, "t", type), (rs, i) -> new Candidate(rs.getLong("id"), rs.getBoolean("mastered"), rs.getInt("times_asked"),
                        rs.getObject("last_score") == null ? null : rs.getDouble("last_score"),
                        rs.getTimestamp("last_asked_at") == null ? null : rs.getTimestamp("last_asked_at").toLocalDateTime(), rs.getBoolean("behavioral"), rs.getBoolean("project")));
    }

    public static String hash(String text) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.strip().toLowerCase().getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    // ---- read ----

    public List<Map<String, Object>> list(long userId, String status, Integer limit) {
        if (status != null && !Set.of("in_progress", "completed", "abandoned").contains(status)) throw invalid("status", "Use in_progress, completed or abandoned.");
        if (limit != null && (limit < 1 || limit > 50)) throw invalid("limit", "Use a number from 1 to 50.");
        return summaries(userId, null, status, limit);
    }

    public Map<String, Object> detail(long userId, long id) {
        Map<String, Object> out = summary(userId, id);
        out.remove("answeredCount");
        out.remove("questionCount");
        boolean completed = "completed".equals(out.get("status"));
        if (!completed) out.put("totalMarks", null); // marks, answers and feedback exist only once a session is submitted
        out.put("gradingComplete", false);
        out.put("report", null);
        out.put("marksByPart", null);
        out.put("masteredCount", null);
        out.put("weakestPoints", null);
        List<Map<String, Object>> attempts = attempts(id, null, completed);
        out.put("attempts", attempts);
        if (completed) gradedFields(id, out, attempts);
        return out;
    }

    /** The report, marks by part, mastered count and weakest points of a submitted session, as far as grading has got (api-contracts 2.7). */
    @SuppressWarnings("unchecked")
    private void gradedFields(long id, Map<String, Object> out, List<Map<String, Object>> attempts) {
        Map<String, Object> row = jdbc.queryForMap("SELECT report_status, report_text FROM practice_session WHERE id = :s", Map.of("s", id));
        String status = (String) row.get("report_status");
        JsonNode text = row.get("report_text") == null ? null : JSON.readTree((String) row.get("report_text"));
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("status", status);
        report.put("headline", text == null ? null : text.path("headline").asString(null));
        report.put("summary", text == null ? null : text.path("summary").asString(null));
        out.put("report", report);
        boolean feedbackPending = attempts.stream().anyMatch(a -> a.get("feedback") != null && "pending".equals(((Map<String, Object>) a.get("feedback")).get("status")));
        boolean total = out.get("totalMarks") != null;
        out.put("gradingComplete", total && !"pending".equals(status) && !feedbackPending);
        if (!total) return;
        Grading.Summary sum = Grading.summarize(Grading.scored(jdbc, id));
        List<Map<String, Object>> parts = new ArrayList<>();
        sum.byPart().forEach((type, m) -> parts.add(Map.of("type", type, "marks", num(m[0]), "maxMarks", num(m[1]))));
        out.put("marksByPart", parts);
        out.put("masteredCount", sum.mastered());
        out.put("weakestPoints", sum.weakest().stream().map(w -> Map.<String, Object>of("id", w.pointId(), "text", w.pointText())).toList());
    }

    private static Object num(double m) { return m == Math.rint(m) ? (Object) (int) m : m; }

    /**
     * Attempts of a session (one position, or all). Without {@code graded} only the in-progress fields are present: never correct or
     * accepted answers. With it, a marked attempt also carries its score, checklist, key facts and feedback (api-contracts 2.5),
     * and {@code gradedWithoutAi} when keyword matching gave the mark, so the report can offer a regrade (skill-bank D9).
     */
    private List<Map<String, Object>> attempts(long sessionId, Integer position, boolean graded) {
        List<Map<String, Object>> out = jdbc.query("""
                SELECT a.id AS aid, a.position, a.max_marks, a.answered_via, a.selected_options, a.blank_answers, a.answer_text, a.transcript, a.voice_stats, a.follow_up_text, a.follow_up_answer,
                       a.score, a.checklist, a.blank_results, a.feedback_text, a.feedback_status, a.graded_by,
                       q.id AS qid, q.type, q.theme, q.text, q.options, q.correct_options, q.accepted_answers, q.key_facts,
                       JSON_LENGTH(q.accepted_answers) AS blank_count,
                       (b.skill_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM resume_point p WHERE p.resume_id = q.resume_id AND p.skill_id = b.skill_id)) AS gap_skill
                FROM attempt a JOIN question q ON q.id = a.question_id LEFT JOIN bank_question b ON b.id = q.bank_question_id WHERE a.session_id = :s AND (:p IS NULL OR a.position = :p) ORDER BY a.position""",
                new MapSqlParameterSource("s", sessionId).addValue("p", position), (rs, i) -> {
            Map<String, Object> question = new LinkedHashMap<>();
            question.put("id", rs.getLong("qid"));
            question.put("type", rs.getString("type"));
            question.put("theme", rs.getString("theme"));
            question.put("isBehavioral", rs.getString("theme") != null);
            question.put("isGapSkill", rs.getBoolean("gap_skill")); // a role skill the resume does not show (skill-bank D4)
            question.put("text", rs.getString("text"));
            question.put("options", rs.getString("options") == null ? null : JSON.readTree(rs.getString("options")));
            question.put("blankCount", rs.getObject("blank_count") == null ? null : rs.getInt("blank_count"));
            Map<String, Object> a = new LinkedHashMap<>();
            a.put("id", rs.getLong("aid")); // "Save as story" sends it as sourceAttemptId (api-contracts 2.5)
            a.put("position", rs.getInt("position"));
            double m = rs.getDouble("max_marks");
            a.put("maxMarks", num(m));
            a.put("question", question);
            a.put("followUp", rs.getString("follow_up_text") == null ? null : Map.of("text", rs.getString("follow_up_text"), "answered", rs.getString("follow_up_answer") != null,
                    "answer", rs.getString("follow_up_answer") == null ? "" : rs.getString("follow_up_answer")));
            String via = rs.getString("answered_via");
            if (via == null) a.put("answer", null);
            else {
                Map<String, Object> ans = new LinkedHashMap<>();
                ans.put("selectedOptions", rs.getString("selected_options") == null ? null : JSON.readTree(rs.getString("selected_options")));
                ans.put("blankAnswers", rs.getString("blank_answers") == null ? null : JSON.readTree(rs.getString("blank_answers")));
                ans.put("text", rs.getString("answer_text"));
                ans.put("transcript", rs.getString("transcript"));
                ans.put("voiceStats", rs.getString("voice_stats") == null ? null : JSON.readTree(rs.getString("voice_stats")));
                ans.put("answeredVia", via);
                a.put("answer", ans);
            }
            if (graded) {
                String type = rs.getString("type");
                boolean marked = rs.getObject("score") != null, brief = type.equals("brief") || type.equals("voice");
                double score = rs.getDouble("score");
                a.put("score", marked ? num(score) : null);
                a.put("mastered", marked ? Grading.mastered(type, m, score) : null);
                a.put("gradedWithoutAi", marked && "fallback".equals(rs.getString("graded_by")));
                a.put("correctOptions", marked && type.equals("multiple_correct") ? JSON.readTree(rs.getString("correct_options")) : null);
                a.put("acceptedAnswers", marked && type.equals("fill_blank") ? JSON.readTree(rs.getString("accepted_answers")) : null);
                a.put("blankResults", marked && rs.getString("blank_results") != null ? JSON.readTree(rs.getString("blank_results")) : null);
                a.put("checklist", marked && rs.getString("checklist") != null ? JSON.readTree(rs.getString("checklist")) : null);
                a.put("keyFacts", null);
                if (marked && brief && rs.getString("theme") == null) {
                    if (rs.getString("key_facts") != null) a.put("keyFacts", JSON.readTree(rs.getString("key_facts")));
                } else if (marked && brief) a.put("keyFacts", List.of()); // a behavioral question has no answer key
                Map<String, Object> fb = null;
                if (marked) {
                    fb = new LinkedHashMap<>();
                    JsonNode t = rs.getString("feedback_text") == null ? null : JSON.readTree(rs.getString("feedback_text"));
                    fb.put("status", rs.getString("feedback_status"));
                    fb.put("text", t == null ? null : t.path("text").asString(null));
                    fb.put("whatToFix", t == null ? null : t.path("whatToFix").asString(null));
                    fb.put("deliveryComment", t == null ? null : t.path("deliveryComment").asString(null));
                }
                a.put("feedback", fb);
            }
            return a;
        });
        return out;
    }

    // ---- answer, abandon ----

    /** Saves (or clears) one answer. Repeating the same call gives the same result. */
    public Map<String, Object> saveAnswer(long userId, long id, int position, JsonNode body) {
        Map<String, Object> s = summary(userId, id);
        if (!"in_progress".equals(s.get("status"))) throw closed();
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT q.type, q.options, JSON_LENGTH(q.accepted_answers) AS blank_count FROM attempt a JOIN question q ON q.id = a.question_id
                WHERE a.session_id = :s AND a.position = :p""", Map.of("s", id, "p", position));
        if (rows.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that question.");
        Map<String, Object> q = rows.get(0);
        JsonNode answer = body == null ? null : body.get("answer");
        String sel = null, blanks = null, text = null, transcript = null, stats = null, via = null;
        Integer marks = null; // a voice answer is worth 10, or 5 when typed instead
        switch ((String) q.get("type")) {
            case "multiple_correct" -> {
                List<String> selected = selectedOptions(answer, JSON.readTree((String) q.get("options")).size());
                if (selected != null) { sel = JSON.writeValueAsString(selected); via = "click"; }
            }
            case "fill_blank" -> {
                List<String> typed = blankAnswers(answer, ((Number) q.get("blank_count")).intValue());
                if (typed != null) { blanks = JSON.writeValueAsString(typed); via = "typed"; }
            }
            case "brief" -> {
                text = briefText(answer);
                if (text != null) via = "typed";
            }
            case "voice" -> {
                VoiceAnswer v = voiceAnswer(answer);
                marks = v != null && v.via().equals("typed") ? TYPED_VOICE_MARKS : VOICE_MARKS;
                if (v != null) { via = v.via(); text = v.text(); transcript = v.transcript(); stats = v.stats(); }
            }
            default -> throw invalid("answer", "This question type is not available yet.");
        }
        jdbc.update("UPDATE attempt SET selected_options = :sel, blank_answers = :blanks, answer_text = :text, transcript = :transcript, voice_stats = :stats, max_marks = COALESCE(:marks, max_marks), answered_via = :via, answered_at = :at WHERE session_id = :s AND position = :p",
                new MapSqlParameterSource("sel", sel).addValue("blanks", blanks).addValue("text", text).addValue("transcript", transcript).addValue("stats", stats).addValue("marks", marks).addValue("via", via)
                        .addValue("at", via == null ? null : LocalDateTime.now(clock.withZone(ZoneOffset.UTC))).addValue("s", id).addValue("p", position));
        return attempts(id, position, false).get(0);
    }

    // ---- interview follow-up (ticket 13) ----

    static final String FALLBACK_FOLLOW_UP = "Can you give one specific example of that, and tell me how it turned out?";

    /** The prompt for the one follow-up. It sees the question, the answer key and what the candidate said, and must not add details of its own. */
    static String followUpPrompt(String question, boolean behavioral, List<String> facts, String answer) {
        StringBuilder sb = new StringBuilder("You are a job interviewer. Ask ONE short follow-up question about what the candidate just said, to probe the weakest or vaguest part of the answer. "
                + "Use only what the candidate said and the question asked; never assume a project, tool, number or detail they did not state. Do not reveal the answer key or give hints.\n")
                .append("Question (").append(behavioral ? "behavioral" : "technical").append("): ").append(question).append('\n');
        if (!facts.isEmpty()) sb.append("Answer key (private, for you only): ").append(String.join("; ", facts)).append('\n');
        return sb.append("Candidate answer: ").append(answer).append("\nReply as {\"followUp\":\"...\"}.").toString();
    }

    /** A usable follow-up from the AI reply: non-empty and at most 300 characters, else null. */
    static String followUpText(JsonNode reply) {
        JsonNode t = reply.path("followUp");
        if (!t.isString()) return null;
        String s = t.asString().strip();
        return s.isEmpty() || s.length() > 300 ? null : s;
    }

    /**
     * The follow-up to one answered interview question: written by Groq once and then kept, so repeating the call (a reload) returns the same
     * text. Without AI it is a generic follow-up, so the interview never stops for it. Returns the attempt.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> followUp(long userId, long id, int position) {
        interviewSession(userId, id);
        Map<String, Object> a = attempts(id, position, false).stream().findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that question."));
        if (a.get("answer") == null) throw invalid("answer", "Answer the question first.");
        if (a.get("followUp") != null) return a;
        Map<String, Object> answer = (Map<String, Object>) a.get("answer"), question = (Map<String, Object>) a.get("question");
        boolean behavioral = Boolean.TRUE.equals(question.get("isBehavioral"));
        String said = String.valueOf(answer.get("transcript") != null ? answer.get("transcript") : answer.get("text"));
        List<String> facts = new ArrayList<>();
        if (!behavioral) {
            for (String f : jdbc.queryForList("SELECT key_facts FROM question WHERE id = :q AND key_facts IS NOT NULL", Map.of("q", question.get("id")), String.class))
                for (JsonNode n : JSON.readTree(f)) if (n.isString()) facts.add(n.asString());
        }
        String text = !ai.available(Provider.GROQ_CHAT) ? null
                : ai.json(Provider.GROQ_CHAT, followUpPrompt((String) question.get("text"), behavioral, facts, said), r -> followUpText(r) != null).map(SessionService::followUpText).orElse(null);
        jdbc.update("UPDATE attempt SET follow_up_text = :t WHERE session_id = :s AND position = :p AND follow_up_text IS NULL",
                Map.of("t", text == null ? FALLBACK_FOLLOW_UP : text, "s", id, "p", position));
        return attempts(id, position, false).get(0);
    }

    /** Saves (or clears) the answer to the follow-up, typed or transcribed. Shapes follow the voice answer (api-contracts 5). */
    public Map<String, Object> saveFollowUpAnswer(long userId, long id, int position, JsonNode body) {
        interviewSession(userId, id);
        VoiceAnswer v = voiceAnswer(body == null ? null : body.get("answer"));
        int rows = jdbc.update("UPDATE attempt SET follow_up_answer = :a WHERE session_id = :s AND position = :p AND follow_up_text IS NOT NULL",
                new MapSqlParameterSource("a", v == null ? null : v.via().equals("typed") ? v.text() : v.transcript()).addValue("s", id).addValue("p", position));
        if (rows == 0) throw invalid("answer", "There is no follow-up question to answer yet.");
        return attempts(id, position, false).get(0);
    }

    /** An unfinished interview of this user, or the usual errors (404 for another user's, 409 when closed, 400 for a quiz). */
    private Map<String, Object> interviewSession(long userId, long id) {
        Map<String, Object> s = summary(userId, id);
        if (!"in_progress".equals(s.get("status"))) throw closed();
        if (!"interview".equals(s.get("mode"))) throw invalid("session", "Follow-up questions belong to interview sessions.");
        return s;
    }

    /** Transcribes one recorded voice answer. The audio is held only for this request; nothing is saved (trd 7.3). */
    public Map<String, Object> transcribe(long userId, long id, int position, byte[] audio, String contentType) {
        if (!"in_progress".equals(summary(userId, id).get("status"))) throw closed();
        List<String> type = jdbc.queryForList("SELECT q.type FROM attempt a JOIN question q ON q.id = a.question_id WHERE a.session_id = :s AND a.position = :p",
                Map.of("s", id, "p", position), String.class);
        if (type.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that question.");
        if (!type.get(0).equals("voice")) throw invalid("audio", "This question is not a voice question.");
        if (contentType == null || !contentType.toLowerCase().startsWith("audio/webm")) throw invalid("audio", "Use a WebM recording.");
        if (audio.length == 0) throw invalid("audio", "The recording is empty.");
        if (audio.length > MAX_AUDIO) throw new ApiException(HttpStatus.CONTENT_TOO_LARGE, "file-too-large", "This recording is over 5 MB. Please record a shorter answer.");
        Optional<JsonNode> reply = ai.transcribe(audio, r -> r.path("text").isString());
        if (reply.isEmpty()) throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ai-unavailable", "We could not turn your recording into text.");
        double seconds = reply.get().path("duration").asDouble(0);
        if (seconds > MAX_SECONDS + 1) throw invalid("audio", "Recordings can be up to 3 minutes.");
        String transcript = reply.get().path("text").asString().strip();
        return Map.of("transcript", transcript, "stats", VoiceStats.of(transcript, seconds).toMap());
    }

    /** Whether this call completed the session (and so started grading), with the session as it is now. */
    public record Submitted(boolean started, Map<String, Object> session) {}

    /** Completes the session and hands it to the grading worker. Repeating it does not grade again. A skipped slot is graded as zero. */
    public Submitted submit(long userId, long id) {
        String status = (String) summary(userId, id).get("status");
        if ("abandoned".equals(status)) throw closed();
        if ("completed".equals(status)) return new Submitted(false, detail(userId, id));
        Integer done = tx.execute(t -> {
            int rows = jdbc.update("UPDATE practice_session SET status = 'completed', completed_at = UTC_TIMESTAMP(3) WHERE id = :s AND user_id = :me AND status = 'in_progress'",
                    Map.of("s", id, "me", userId));
            if (rows == 1) { // answers with nothing to write feedback about need no Gemma text
                jdbc.update("""
                        UPDATE attempt a JOIN question q ON q.id = a.question_id SET a.feedback_status = 'none'
                        WHERE a.session_id = :s AND (q.type IN ('multiple_correct', 'fill_blank') OR a.answered_via IS NULL)""", Map.of("s", id));
            }
            return rows;
        });
        boolean started = done != null && done == 1;
        if (started) grading.start(id);
        return new Submitted(started, detail(userId, id));
    }

    /**
     * Sends one keyword-graded answer back to the grading worker once AI is available (skill-bank D9). The worker marks it again, rewrites
     * its feedback and the report, and updates the total; an AI mark then counts toward mastery. Refused while grading is still running.
     */
    public Map<String, Object> regrade(long userId, long id, int position) {
        Map<String, Object> s = detail(userId, id); // another user's session is 404
        List<String> by = jdbc.queryForList("SELECT COALESCE(graded_by, '') FROM attempt WHERE session_id = :s AND position = :p", Map.of("s", id, "p", position), String.class);
        if (by.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that question.");
        if (!by.get(0).equals("fallback")) throw regradeUnavailable("This answer is already graded.");
        if (!Boolean.TRUE.equals(s.get("gradingComplete"))) throw regradeUnavailable("Wait until marking and feedback are finished.");
        if (!ai.available(Provider.GROQ_CHAT)) throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ai-unavailable", "AI grading is not available right now. Please try again later.");
        tx.executeWithoutResult(t -> {
            jdbc.update("""
                    UPDATE attempt SET score = NULL, checklist = NULL, graded_by = NULL, feedback_text = NULL, feedback_status = 'pending'
                    WHERE session_id = :s AND position = :p AND graded_by = 'fallback'""", Map.of("s", id, "p", position));
            jdbc.update("UPDATE practice_session SET report_status = 'pending' WHERE id = :s", Map.of("s", id)); // the report quotes the total
        });
        grading.regrade(id);
        return detail(userId, id);
    }

    private static ApiException regradeUnavailable(String message) {
        return new ApiException(HttpStatus.CONFLICT, "regrade-unavailable", message);
    }

    public Map<String, Object> abandon(long userId, long id) {
        Map<String, Object> s = summary(userId, id);
        if ("completed".equals(s.get("status"))) throw closed();
        if ("in_progress".equals(s.get("status"))) {
            jdbc.update("UPDATE practice_session SET status = 'abandoned' WHERE id = :s AND user_id = :me AND status = 'in_progress'", Map.of("s", id, "me", userId));
        }
        return summary(userId, id);
    }

    // ---- summaries ----

    private Map<String, Object> summary(long userId, long id) {
        List<Map<String, Object>> found = summaries(userId, id, null, null);
        if (found.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that practice session.");
        return found.get(0);
    }

    /** SessionSummary rows (api-contracts 2.6), scoped to the user. */
    private List<Map<String, Object>> summaries(long userId, Long id, String status, Integer limit) {
        return jdbc.query("""
                SELECT s.id, s.status, s.mode, s.voice_skipped, s.max_marks, s.total_marks, r.id AS resume_id, r.file_name, ro.id AS role_id, ro.name AS role_name,
                       CONCAT(DATE_FORMAT(s.started_at, '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(s.started_at) DIV 1000, 3, '0'), 'Z') AS started_at,
                       CONCAT(DATE_FORMAT(s.completed_at, '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(s.completed_at) DIV 1000, 3, '0'), 'Z') AS completed_at,
                       (SELECT COUNT(*) FROM attempt a WHERE a.session_id = s.id AND a.answered_via IS NOT NULL) AS answered,
                       (SELECT COUNT(*) FROM attempt a WHERE a.session_id = s.id) AS total
                FROM practice_session s JOIN resume r ON r.id = s.resume_id JOIN role ro ON ro.id = r.target_role_id
                WHERE s.user_id = :me AND (:id IS NULL OR s.id = :id) AND (:status IS NULL OR s.status = :status)
                ORDER BY s.started_at DESC, s.id DESC LIMIT :lim""",
                new MapSqlParameterSource("me", userId).addValue("id", id).addValue("status", status).addValue("lim", limit == null ? 1000 : limit), (rs, i) -> {
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("id", rs.getLong("id"));
                    out.put("status", rs.getString("status"));
                    out.put("mode", rs.getString("mode"));
                    out.put("startedAt", rs.getString("started_at"));
                    out.put("completedAt", rs.getString("completed_at"));
                    out.put("resume", Map.of("id", rs.getLong("resume_id"), "fileName", rs.getString("file_name")));
                    out.put("role", Map.of("id", rs.getLong("role_id"), "name", rs.getString("role_name")));
                    out.put("voiceSkipped", rs.getBoolean("voice_skipped"));
                    out.put("maxMarks", rs.getInt("max_marks"));
                    out.put("totalMarks", rs.getObject("total_marks") == null ? null : rs.getDouble("total_marks"));
                    out.put("answeredCount", rs.getInt("answered"));
                    out.put("questionCount", rs.getInt("total"));
                    return out;
                });
    }

    private long openSession(long userId) {
        return jdbc.queryForList("SELECT id FROM practice_session WHERE user_id = :me AND status = 'in_progress'", Map.of("me", userId), Long.class)
                .stream().findFirst().orElse(0L);
    }

    private static ApiException sessionOpen(long id) {
        return new ApiException(HttpStatus.CONFLICT, "session-open", "Finish or leave your unfinished practice session first.", Map.of("sessionId", id));
    }

    private static ApiException closed() {
        return new ApiException(HttpStatus.CONFLICT, "session-closed", "This practice session is no longer open.");
    }

    private static ApiException invalid(String field, String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.", Map.of("errors", List.of(Map.of("field", field, "message", message))));
    }
}
