package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.resume.ResumeService;
import tools.jackson.databind.json.JsonMapper;

/**
 * Submit, grading, the practice bank fallback and the shared bank on a real MySQL 8 (tickets 17 to 22). Skipped when Docker is not installed.
 * No AI keys are set, so Groq and Gemma count as unavailable: every brief answer is graded by the keyword fallback and its text is a template.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"app.jwt.secret=test-secret-test-secret-test-secret-123", "app.ai.groq-key=", "app.ai.google-key=", "app.grading.gemma-gap-millis=0"})
class GradingDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    static final JsonMapper JSON = JsonMapper.builder().build();
    static final String RESUME = "{\"profile\":{\"name\":\"Asha Rao\"},\"experience\":[{\"company\":\"Acme Corp\",\"bullets\":[\"Built REST APIs with Spring Boot for order processing\",\"Tuned MySQL queries to cut page load time by 40%\"]}],"
            + "\"projects\":[{\"name\":\"Order Service\",\"bullets\":[\"Cached product lookups in Redis\"]}],\"skills\":[]}";

    static final String EMPTY = "{\"experience\":[],\"projects\":[],\"skills\":[]}"; // nothing for the fallback to build from, so only the stored questions are used

    @Autowired JdbcTemplate jdbc;
    @Autowired ResumeService resumes;
    @Autowired SessionService service;
    @Autowired GradingService grading;
    @Autowired PracticeBank bank;

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    private long resume(long me, String parsed) {
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        for (String s : List.of("Spring Boot", "MySQL", "Redis")) jdbc.update("INSERT IGNORE INTO skill (name) VALUES (?)", s);
        return (Long) resumes.save(me, "r.pdf", role, null, "openresume", JSON.readTree(parsed)).get("id");
    }

    private void mc(long resume, int n) {
        for (int i = 0; i < n; i++) {
            jdbc.update("""
                    INSERT INTO question (resume_id, type, text, options, correct_options, source, text_hash)
                    VALUES (?, 'multiple_correct', ?, '[{"key":"A","text":"a"},{"key":"B","text":"b"},{"key":"C","text":"c"},{"key":"D","text":"d"}]', '["A","C"]', 'ai', ?)""",
                    resume, "MC " + i + "?", SessionService.hash("mc" + i + resume));
        }
    }

    private void brief(long resume, int technical, int behavioral) {
        for (int i = 0; i < technical; i++) jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'brief', ?, 'ai', ?)", resume, "Technical " + i + "?", SessionService.hash("t" + i + resume));
        for (int i = 0; i < behavioral; i++) jdbc.update("INSERT INTO question (resume_id, type, theme, text, source, text_hash) VALUES (?, 'brief', 'teamwork', ?, 'ai', ?)", resume, "Behavioral " + i + "?", SessionService.hash("b" + i + resume));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> awaitGraded(long me, long id) throws InterruptedException {
        for (int i = 0; i < 200; i++) {
            Map<String, Object> s = service.detail(me, id);
            if (Boolean.TRUE.equals(s.get("gradingComplete"))) return s;
            Thread.sleep(100);
        }
        throw new AssertionError("grading did not finish: " + service.detail(me, id).get("report"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void submitMarksEveryPartWithoutAnyHintThatAiWasOff() throws Exception {
        long me = user("grade_me");
        long resume = resume(me, EMPTY);
        mc(resume, 5);
        brief(resume, 4, 2);
        long id = ((Number) service.start(me, resume).get("id")).longValue();
        for (int p = 1; p <= 5; p++) service.saveAnswer(me, id, p, JSON.readTree("{\"answer\":{\"selectedOptions\":" + (p == 1 ? "[\"A\"]" : "[\"A\",\"C\"]") + "}}")); // 1 mark, then four 2-mark answers
        service.saveAnswer(me, id, 6, JSON.readTree("{\"answer\":{\"text\":\"I added an index because the queries were slow. The result was 40% faster pages.\"}}"));

        SessionService.Submitted first = service.submit(me, id);
        assertThat(first.started()).isTrue();
        assertThat(first.session()).containsEntry("status", "completed");
        assertThat(service.submit(me, id).started()).isFalse(); // repeating does not grade again

        Map<String, Object> s = awaitGraded(me, id);
        assertThat(s.get("totalMarks")).isNotNull();
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(attempts.get(0).get("score")).isEqualTo(1);
        assertThat(attempts.get(1)).containsEntry("score", 2).containsEntry("mastered", true).containsEntry("correctOptions", JSON.readTree("[\"A\",\"C\"]"));
        assertThat(attempts.get(6).get("score")).isEqualTo(0); // a skipped brief question is zero
        Map<String, Object> answered = attempts.get(5);
        assertThat(((List<?>) answered.get("checklist"))).hasSize(5);
        assertThat(((Map<?, ?>) answered.get("feedback")).get("text")).isNotNull();
        assertThat(((Map<String, Object>) s.get("report")).get("headline")).isNotNull();
        assertThat(((List<?>) s.get("marksByPart"))).hasSize(2); // multiple-correct and brief: the voice part was skipped
        assertThat(s.toString()).doesNotContain("gradedBy").doesNotContain("graded_by");

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM attempt WHERE session_id = ? AND graded_by = 'java'", Long.class, id)).isEqualTo(10L); // 5 multiple-correct and the 5 skipped brief questions
        assertThat(jdbc.queryForObject("SELECT graded_by FROM attempt WHERE session_id = ? AND position = 6", String.class, id)).isEqualTo("fallback");
        // the fallback mark does not touch mastery, a Java mark does
        assertThat(jdbc.queryForObject("SELECT last_score IS NULL AND mastered = 0 FROM question q JOIN attempt a ON a.question_id = q.id WHERE a.session_id = ? AND a.position = 6", Boolean.class, id)).isTrue();
        assertThat(jdbc.queryForObject("SELECT mastered FROM question q JOIN attempt a ON a.question_id = q.id WHERE a.session_id = ? AND a.position = 2", Boolean.class, id)).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void aSessionStartsFromTheBankAndGenericTemplatesWhenAiIsOffAndTheBankIsShared() {
        long me = user("bank_me");
        long resume = resume(me, RESUME);
        Map<String, Object> s = service.start(me, resume);
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(attempts).isNotEmpty();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM question WHERE resume_id = ? AND source = 'bank'", Long.class, resume)).isPositive();
        assertThat(attempts).allSatisfy(a -> assertThat(((Map<?, ?>) a.get("question")).get("text").toString()).isNotBlank());

        // the shared bank takes only technical, skill-based questions without personal text, and a repeat is dropped
        PracticeBank.Ctx ctx = bank.context(resume);
        String text = "Which Redis command sets a key with an expiry time?", opts = "[{\"key\":\"A\",\"text\":\"SETEX\"},{\"key\":\"B\",\"text\":\"GET\"},{\"key\":\"C\",\"text\":\"DEL\"},{\"key\":\"D\",\"text\":\"TTL\"}]";
        assertThat(bank.learn(ctx, "multiple_correct", null, text, opts, "[\"A\"]", null, "groq")).isTrue();
        assertThat(bank.learn(ctx, "multiple_correct", null, text, opts, "[\"A\"]", null, "groq")).isFalse();
        assertThat(bank.learn(ctx, "brief", "teamwork", "Tell me about a Redis conflict.", null, null, null, "groq")).isFalse(); // behavioral
        assertThat(bank.learn(ctx, "brief", null, "Why did Order Service use Redis?", null, null, null, "groq")).isFalse(); // project name
        assertThat(bank.learn(ctx, "brief", null, "Why do teams like to cache things?", null, null, null, "groq")).isFalse(); // no skill in the text
        Map<String, Object> row = jdbc.queryForMap("SELECT source, provider, created_at FROM bank_question WHERE text_template = ?", text);
        assertThat(row).containsEntry("source", "ai").containsEntry("provider", "groq");
        assertThat(row.get("created_at")).isNotNull();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM bank_question WHERE text_template LIKE '%Order Service%' OR text_template LIKE '%Acme%'", Long.class)).isZero();
    }

    @Test
    void twoSavesOfTheSameQuestionAtOnceLeaveOneBankRow() throws Exception {
        long me = user("race_me");
        PracticeBank.Ctx ctx = bank.context(resume(me, RESUME));
        String text = "Which MySQL storage engine supports transactions and foreign keys?";
        ExecutorService pool = Executors.newFixedThreadPool(8);
        List<Future<Boolean>> saves = new ArrayList<>();
        for (int i = 0; i < 8; i++) saves.add(pool.submit(() -> bank.learn(ctx, "brief", null, text, null, null, null, "groq")));
        int added = 0;
        for (Future<Boolean> f : saves) if (f.get()) added++;
        pool.shutdown();
        assertThat(added).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM bank_question WHERE text_template = ?", Long.class, text)).isEqualTo(1L);
    }

    @Test
    void aCompletedSessionLeftHalfGradedIsFinishedOnStartup() throws Exception {
        long me = user("restart_me");
        long resume = resume(me, RESUME);
        mc(resume, 1);
        long q = jdbc.queryForObject("SELECT id FROM question WHERE resume_id = ?", Long.class, resume);
        jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks, completed_at) VALUES (?, ?, 'completed', 2, UTC_TIMESTAMP(3))", me, resume);
        long id = jdbc.queryForObject("SELECT id FROM practice_session WHERE user_id = ?", Long.class, me);
        jdbc.update("INSERT INTO attempt (session_id, question_id, position, max_marks, answered_via, selected_options) VALUES (?, ?, 1, 2, 'click', '[\"A\",\"C\"]')", id, q);

        grading.resumeUnfinished();
        Map<String, Object> s = awaitGraded(me, id);
        assertThat(s).containsEntry("totalMarks", 2.0).containsEntry("masteredCount", 1);
    }
}
