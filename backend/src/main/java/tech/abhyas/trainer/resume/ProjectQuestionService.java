package tech.abhyas.trainer.resume;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * One AI call per resume writes about {@value #WANTED} open questions about its projects and experience, each with its own
 * key facts stored on the question (FR-8, FR-9, trd 7.1). Run after a save and retried when the user opens the Resume list or
 * starts a session; a failure leaves the resume without them, so the bank and the templates fill the slots until then.
 * Nothing is shown to the user, and nothing about the resume or the AI reply is logged (trd 6.5).
 */
@Service
public class ProjectQuestionService {
    static final int WANTED = 8, MIN_FACTS = 3, MAX_FACTS = 6, MAX_FACT_LENGTH = 500, MAX_TEXT = 1000;
    static final Duration RETRY_DELAY = Duration.ofMinutes(10);
    private static final Logger log = LoggerFactory.getLogger(ProjectQuestionService.class);
    private static final JsonMapper JSON = JsonMapper.builder().build();

    /** A resume has its project questions once one AI question without a point or a theme exists. Skills-only resumes have nothing to ask about. */
    private static final String NEEDS_QUESTIONS = """
            FROM resume r WHERE NOT EXISTS (SELECT 1 FROM question q WHERE q.resume_id = r.id AND q.source = 'ai' AND q.theme IS NULL AND q.resume_point_id IS NULL)
              AND EXISTS (SELECT 1 FROM resume_point p WHERE p.resume_id = r.id AND p.kind <> 'skill')""";

    private final AiClient ai;
    private final NamedParameterJdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final Clock clock;
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "project-questions");
        t.setDaemon(true);
        return t;
    });
    private final Set<Long> queued = ConcurrentHashMap.newKeySet(); // resume ids waiting or running, so a double click does not queue twice
    // ponytail: the last try is kept in memory, so a restart retries at once; a column on resume if that ever matters
    private final Map<Long, Instant> lastTry = new ConcurrentHashMap<>();

    public ProjectQuestionService(AiClient ai, NamedParameterJdbcTemplate jdbc, TransactionTemplate tx, Clock clock) {
        this.ai = ai;
        this.jdbc = jdbc;
        this.tx = tx;
        this.clock = clock;
    }

    /** After a save: write this resume's questions in the background. */
    public void draftInBackground(long resumeId) {
        if (queued.add(resumeId)) worker.execute(() -> {
            try { draft(resumeId); } finally { queued.remove(resumeId); }
        });
    }

    /** On opening the Resume list or starting a session: retry the user's resumes that still have none, at most once per {@link #RETRY_DELAY} each. */
    public void retryInBackground(long userId) {
        Instant cutoff = clock.instant().minus(RETRY_DELAY);
        for (long id : jdbc.queryForList("SELECT r.id " + NEEDS_QUESTIONS + " AND r.user_id = :me", Map.of("me", userId), Long.class)) {
            if (lastTry.getOrDefault(id, Instant.EPOCH).isBefore(cutoff)) draftInBackground(id);
        }
    }

    /** The one AI call. Returns how many questions were stored (0 when AI is unavailable or the reply had none that passed). */
    public int draft(long resumeId) {
        if (!ai.available(Provider.GROQ_CHAT)) return 0; // cooling down: user requests get the quota
        lastTry.put(resumeId, clock.instant());
        try {
            List<Map<String, Object>> row = jdbc.queryForList("SELECT r.parsed_json " + NEEDS_QUESTIONS + " AND r.id = :r", Map.of("r", resumeId));
            if (row.isEmpty()) return 0; // already has them, or deleted meanwhile
            Optional<JsonNode> reply = ai.json(Provider.GROQ_CHAT, prompt(String.valueOf(row.get(0).get("parsed_json"))), r -> !questions(r).isEmpty());
            if (reply.isEmpty()) return 0;
            List<Question> qs = questions(reply.get());
            tx.executeWithoutResult(s -> store(resumeId, qs));
            return qs.size();
        } catch (DataAccessException e) {
            log.warn("project questions: database error {}", e.getClass().getSimpleName()); // never the message: it can quote the data
            return 0;
        }
    }

    // 3 brief for every 5 voice: a session takes 1 brief and 2 voice (ticket 17)
    private void store(long resumeId, List<Question> qs) {
        for (int i = 0; i < qs.size(); i++) {
            jdbc.update("""
                    INSERT IGNORE INTO question (resume_id, type, text, key_facts, source, provider, text_hash)
                    VALUES (:r, :type, :text, :facts, 'ai', 'groq', :hash)""",
                    new MapSqlParameterSource("r", resumeId).addValue("type", i % 3 == 0 ? "brief" : "voice").addValue("text", qs.get(i).text())
                            .addValue("facts", JSON.writeValueAsString(qs.get(i).keyFacts())).addValue("hash", tech.abhyas.trainer.session.SessionService.hash(qs.get(i).text())));
        }
    }

    /** Only the projects, experience and skills go to the AI: the profile (name, email, phone, links) stays out. */
    private static String prompt(String parsedJson) {
        JsonNode resume = JSON.readTree(parsedJson);
        String body = JSON.writeValueAsString(Map.of("projects", resume.path("projects"), "experience", resume.path("experience"), "skills", resume.path("skills")));
        // FR-9: test the topics behind the work, and never assume what the resume does not say
        return "You are a technical interviewer writing open practice questions from a candidate's resume. Write " + WANTED
                + " questions about the candidate's projects and experience, each about a different project, job or bullet. Each asks how the candidate did something and why, "
                + "so it can be answered aloud in about a minute, and tests the technology, concept or design choice behind the work. Never ask what the resume says or mentions. "
                + "Never assume anything the resume does not state: no invented languages, tools, teams, deadlines, roles or results. "
                + "For each question give " + MIN_FACTS + " to " + MAX_FACTS + " short key facts that a good answer should mention, each true for the resume or standard technical knowledge about what it names.\n"
                + "Reply as {\"questions\":[{\"text\":\"...\",\"keyFacts\":[\"...\",\"...\",\"...\"]}]}.\nResume JSON: " + body;
    }

    record Question(String text, List<String> keyFacts) {}

    /** Keeps the well-formed questions (text, and {@value #MIN_FACTS} to {@value #MAX_FACTS} key facts), at most {@value #WANTED}. */
    static List<Question> questions(JsonNode reply) {
        List<Question> out = new ArrayList<>();
        JsonNode list = reply.path("questions");
        if (!list.isArray()) return out;
        for (JsonNode q : list) {
            String text = q.path("text").isString() ? q.path("text").asString().strip() : "";
            if (text.isEmpty() || text.length() > MAX_TEXT || out.size() >= WANTED || !q.path("keyFacts").isArray()) continue;
            List<String> facts = new ArrayList<>();
            for (JsonNode f : q.path("keyFacts")) {
                String t = f.isString() ? f.asString().strip() : "";
                if (!t.isEmpty() && t.length() <= MAX_FACT_LENGTH && facts.size() < MAX_FACTS) facts.add(t);
            }
            if (facts.size() >= MIN_FACTS && out.stream().noneMatch(o -> o.text().equalsIgnoreCase(text))) out.add(new Question(text, facts));
        }
        return out;
    }
}
