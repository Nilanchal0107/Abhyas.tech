package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.resume.ResumeService;
import tools.jackson.databind.json.JsonMapper;

/** Starting, answering and leaving a session on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"app.jwt.secret=test-secret-test-secret-test-secret-123", "app.ai.groq-key=", "app.ai.google-key="}) // no keys: no real AI call, and Whisper counts as unavailable
class SessionDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ResumeService resumes;
    @Autowired SessionService service;

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    /** A saved resume with n stored multiple-correct questions, so no AI call is needed. */
    private long resume(long me, int questions) {
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        long id = (Long) resumes.save(me, "r.pdf", role, null, "openresume",
                JsonMapper.builder().build().readTree("{\"experience\":[],\"projects\":[],\"skills\":[]}")).get("id");
        for (int i = 0; i < questions; i++) {
            jdbc.update("""
                    INSERT INTO question (resume_id, type, text, options, correct_options, source, text_hash)
                    VALUES (?, 'multiple_correct', ?, '[{"key":"A","text":"a"},{"key":"B","text":"b"},{"key":"C","text":"c"},{"key":"D","text":"d"}]', '["A","C"]', 'ai', ?)""",
                    id, "Question " + i + "?", SessionService.hash("Question " + i + "?" + id));
        }
        return id;
    }

    private void blankQuestions(long resume, int n) {
        for (int i = 0; i < n; i++) {
            String text = "Blank " + i + ": ________" + (i % 2 == 1 ? " and ________" : "");
            jdbc.update("INSERT INTO question (resume_id, type, text, accepted_answers, source, text_hash) VALUES (?, 'fill_blank', ?, ?, 'ai', ?)",
                    resume, text, i % 2 == 1 ? "[[\"x\"],[\"y\"]]" : "[[\"x\",\"X\"]]", SessionService.hash(text + resume));
        }
    }

    private static void assertCode(Runnable call, String type) {
        assertThatThrownBy(call::run).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo(type));
    }

    private static Object q(Map<String, Object> a, String key) { return ((Map<?, ?>) a.get("question")).get(key); }

    @Test
    @SuppressWarnings("unchecked")
    void startBuildsFiveSlotsHidesTheKeyAndAllowsOnlyOneOpenSession() {
        long me = user("sess_me");
        long resume = resume(me, 6);
        Map<String, Object> s = service.start(me, resume);
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(s).containsEntry("status", "in_progress").containsEntry("maxMarks", 10).containsEntry("gradingComplete", false);
        assertThat(attempts).hasSize(5).extracting(a -> a.get("position")).containsExactly(1, 2, 3, 4, 5);
        assertThat(s.toString()).doesNotContain("correct").doesNotContain("keyFacts"); // nothing that gives the answer away
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM question WHERE resume_id = ? AND times_asked = 1 AND last_asked_at IS NOT NULL", Long.class, resume)).isEqualTo(5);
        assertThat(jdbc.queryForObject("SELECT last_used_at IS NOT NULL FROM resume WHERE id = ?", Boolean.class, resume)).isTrue();

        assertThatThrownBy(() -> service.start(me, resume)).isInstanceOfSatisfying(ApiException.class, e -> {
            assertThat(e.type()).isEqualTo("session-open");
            assertThat(e.extras()).containsEntry("sessionId", s.get("id"));
        });
        // the unique index itself, behind the check
        assertThatThrownBy(() -> jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks) VALUES (?, ?, 'in_progress', 10)", me, resume))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void answersAreSavedRepeatablyClearedAndRefusedOnceTheSessionIsOver() {
        long me = user("ans_me"), other = user("ans_other");
        long id = ((Number) service.start(me, resume(me, 5)).get("id")).longValue();
        var body = JsonMapper.builder().build().readTree("{\"answer\":{\"selectedOptions\":[\"A\",\"C\"]}}");
        for (int i = 0; i < 2; i++) { // saving twice gives the same result
            Map<String, Object> a = service.saveAnswer(me, id, 2, body);
            assertThat(((Map<?, ?>) a.get("answer")).get("selectedOptions").toString()).isEqualTo("[\"A\",\"C\"]");
        }
        assertThat(service.list(me, "in_progress", 1).get(0)).containsEntry("answeredCount", 1);
        assertThat(service.saveAnswer(me, id, 2, JsonMapper.builder().build().readTree("{\"answer\":null}")).get("answer")).isNull();
        assertCode(() -> service.saveAnswer(me, id, 2, JsonMapper.builder().build().readTree("{\"answer\":{\"selectedOptions\":[\"E\"]}}")), "validation");
        assertCode(() -> service.saveAnswer(me, id, 9, body), "not-found");
        assertCode(() -> service.saveAnswer(other, id, 1, body), "not-found");
        assertCode(() -> service.detail(other, id), "not-found");
        assertCode(() -> service.abandon(other, id), "not-found");

        assertThat(service.abandon(me, id)).containsEntry("status", "abandoned");
        assertThat(service.abandon(me, id)).containsEntry("status", "abandoned"); // repeating gives the same
        assertCode(() -> service.saveAnswer(me, id, 1, body), "session-closed");
        assertThat(service.list(me, "abandoned", 1)).hasSize(1);
        assertThat(service.list(me, "in_progress", null)).isEmpty();
        service.start(me, jdbc.queryForObject("SELECT resume_id FROM practice_session WHERE id = ?", Long.class, id)); // a new one can start
    }

    @Test
    void aResumeWithNoQuestionsAndNoAiCannotStartASession() {
        long me = user("noai_me");
        assertCode(() -> service.start(me, resume(me, 0)), "ai-unavailable");
        assertCode(() -> service.start(me, 999_999L), "not-found");
    }

    @Test
    @SuppressWarnings("unchecked")
    void sessionHasBothPartsAndBlankAnswersAreCheckedAgainstTheBlankCount() {
        long me = user("fb_me");
        long resume = resume(me, 5);
        blankQuestions(resume, 6);
        Map<String, Object> s = service.start(me, resume);
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(attempts).hasSize(10).extracting(a -> q(a, "type"))
                .containsExactly("multiple_correct", "multiple_correct", "multiple_correct", "multiple_correct", "multiple_correct",
                        "fill_blank", "fill_blank", "fill_blank", "fill_blank", "fill_blank");
        assertThat(s).containsEntry("maxMarks", 20);
        assertThat(s.toString()).doesNotContain("accepted").doesNotContain("\"X\""); // the accepted wordings stay on the server
        Map<String, Object> first = (Map<String, Object>) attempts.get(5).get("question");
        int blanks = (Integer) first.get("blankCount");
        assertThat(blanks).isBetween(1, 2);

        long id = ((Number) s.get("id")).longValue();
        var json = JsonMapper.builder();
        String good = blanks == 1 ? "[\"RestController\"]" : "[\"a\",\"b\"]", bad = blanks == 1 ? "[\"a\",\"b\"]" : "[\"a\"]";
        for (int i = 0; i < 2; i++) { // saving twice gives the same result
            Map<String, Object> a = service.saveAnswer(me, id, 6, json.build().readTree("{\"answer\":{\"blankAnswers\":" + good + "}}"));
            assertThat(((Map<?, ?>) a.get("answer")).get("answeredVia")).isEqualTo("typed");
        }
        assertCode(() -> service.saveAnswer(me, id, 6, json.build().readTree("{\"answer\":{\"blankAnswers\":" + bad + "}}")), "validation");
        assertCode(() -> service.saveAnswer(me, id, 6, json.build().readTree("{\"answer\":{\"selectedOptions\":[\"A\"]}}")), "validation");
        assertThat(service.saveAnswer(me, id, 6, json.build().readTree("{\"answer\":null}")).get("answer")).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void briefPartHasFourTechnicalAndTwoBehavioralSlotsAndSavesTypedText() {
        long me = user("brief_me");
        long resume = resume(me, 0);
        for (int i = 0; i < 5; i++) {
            jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'brief', ?, 'ai', ?)", resume, "Technical " + i + "?", SessionService.hash("t" + i + resume));
        }
        for (int i = 0; i < 3; i++) {
            jdbc.update("INSERT INTO question (resume_id, type, theme, text, source, text_hash) VALUES (?, 'brief', 'teamwork', ?, 'ai', ?)", resume, "Behavioral " + i + "?", SessionService.hash("b" + i + resume));
        }
        Map<String, Object> s = service.start(me, resume);
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(attempts).hasSize(6).extracting(a -> a.get("maxMarks")).containsOnly(5);
        assertThat(attempts).extracting(a -> q(a, "isBehavioral")).containsExactly(false, false, false, false, true, true);
        assertThat(attempts.get(4).get("question")).extracting(q -> ((Map<?, ?>) q).get("theme")).isEqualTo("teamwork");

        long id = ((Number) s.get("id")).longValue();
        var json = JsonMapper.builder().build();
        for (int i = 0; i < 2; i++) { // saving twice gives the same result
            Map<?, ?> a = (Map<?, ?>) service.saveAnswer(me, id, 1, json.readTree("{\"answer\":{\"text\":\"I added an index.\"}}")).get("answer");
            assertThat(a.get("text")).isEqualTo("I added an index.");
            assertThat(a.get("answeredVia")).isEqualTo("typed");
        }
        assertCode(() -> service.saveAnswer(me, id, 1, json.readTree("{\"answer\":{\"text\":\"" + "x".repeat(5001) + "\"}}")), "validation");
        assertThat(service.saveAnswer(me, id, 1, json.readTree("{\"answer\":{\"text\":\"  \"}}")).get("answer")).isNull();
    }

    @Test
    void withoutWhisperTheSessionLeavesOutTheVoicePart() {
        long me = user("novoice_me");
        long resume = resume(me, 5);
        for (int i = 0; i < 3; i++) {
            jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'voice', ?, 'ai', ?)", resume, "Voice " + i + "?", SessionService.hash("v" + i + resume));
        }
        Map<String, Object> s = service.start(me, resume);
        assertThat(s).containsEntry("voiceSkipped", true);
        assertThat(((List<?>) s.get("attempts"))).extracting(a -> q((Map<String, Object>) a, "type")).doesNotContain("voice");
    }
}
