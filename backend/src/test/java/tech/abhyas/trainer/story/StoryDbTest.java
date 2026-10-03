package tech.abhyas.trainer.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.session.ExportService;
import tools.jackson.databind.json.JsonMapper;

/** Stories, tags, links, ownership and the export refusal on a real MySQL 8 (tickets 25 to 27). Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = {"app.jwt.secret=test-secret-test-secret-test-secret-123", "app.ai.groq-key=", "app.ai.google-key="})
class StoryDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    static final JsonMapper JSON = JsonMapper.builder().build();

    @Autowired JdbcTemplate jdbc;
    @Autowired StoryService stories;
    @Autowired ExportService exports;

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    /** A resume with two resume points. Returns the ids of the resume and of the first point. */
    private long[] resume(long me) {
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        jdbc.update("INSERT INTO resume (user_id, file_name, target_role_id, parsed_json, parse_source) VALUES (?, 'r.pdf', ?, '{}', 'openresume')", me, role);
        long resume = jdbc.queryForObject("SELECT MAX(id) FROM resume WHERE user_id = ?", Long.class, me);
        for (int i = 1; i <= 2; i++) jdbc.update("INSERT INTO resume_point (resume_id, kind, source_ref, text, position) VALUES (?, 'skill', ?, ?, ?)", resume, "skills[" + i + "]", "Point " + i, i);
        return new long[] {resume, jdbc.queryForObject("SELECT MIN(id) FROM resume_point WHERE resume_id = ?", Long.class, resume)};
    }

    private tools.jackson.databind.JsonNode body(String json) { return JSON.readTree(json); }

    @Test
    void tagFilterListsOnlyMatchingStoriesAndAllTheUsersTags() {
        long me = user("tags_me"), other = user("tags_other");
        stories.create(me, body("{\"title\":\"A\",\"tags\":[\"teamwork\",\"conflict\"]}"));
        stories.create(me, body("{\"title\":\"B\",\"tags\":[\"conflict\"]}"));
        stories.create(me, body("{\"title\":\"C\"}"));
        stories.create(other, body("{\"title\":\"D\",\"tags\":[\"secret\"]}"));
        Map<String, Object> all = stories.list(me, null);
        assertThat((List<?>) all.get("stories")).hasSize(3);
        assertThat(all.get("tags")).isEqualTo(List.of("conflict", "teamwork")); // distinct, the other user's tag is not here
        Map<String, Object> teamwork = stories.list(me, "teamwork");
        assertThat((List<Map<String, Object>>) teamwork.get("stories")).extracting(s -> s.get("title")).containsExactly("A");
        assertThat((List<?>) stories.list(me, "secret").get("stories")).isEmpty();
    }

    @Test
    void anotherUsersStoryIsNotFoundForReadEditAndDelete() {
        long me = user("own_me"), other = user("own_other");
        long id = ((Number) stories.create(me, body("{\"title\":\"Mine\"}")).get("id")).longValue();
        assertThatThrownBy(() -> stories.get(other, id)).isInstanceOf(ApiException.class).extracting("type").isEqualTo("not-found");
        assertThatThrownBy(() -> stories.update(other, id, body("{\"title\":\"x\"}"))).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> stories.delete(other, id)).isInstanceOf(ApiException.class);
        assertThat(stories.get(me, id).get("title")).isEqualTo("Mine");
    }

    @Test
    void linkedResumePointsMustBelongToTheUserAndGoWhenTheResumeIsDeleted() {
        long me = user("link_me"), other = user("link_other");
        long[] mine = resume(me), theirs = resume(other);
        assertThatThrownBy(() -> stories.create(me, body("{\"title\":\"t\",\"resumePointIds\":[" + theirs[1] + "]}"))).isInstanceOf(ApiException.class);
        long id = ((Number) stories.create(me, body("{\"title\":\"t\",\"resumePointIds\":[" + mine[1] + "]}")).get("id")).longValue();
        assertThat((List<?>) stories.get(me, id).get("resumePoints")).hasSize(1);
        jdbc.update("DELETE FROM resume WHERE id = ?", mine[0]);
        assertThat((List<?>) stories.get(me, id).get("resumePoints")).isEmpty(); // links are gone, the story stays
    }

    @Test
    void updateReplacesTagsAndLinks() {
        long me = user("upd_me");
        long[] r = resume(me);
        long id = ((Number) stories.create(me, body("{\"title\":\"t\",\"tags\":[\"a\"],\"resumePointIds\":[" + r[1] + "]}")).get("id")).longValue();
        Map<String, Object> s = stories.update(me, id, body("{\"title\":\"t2\",\"tags\":[\"b\"],\"resumePointIds\":[]}"));
        assertThat(s.get("title")).isEqualTo("t2");
        assertThat(s.get("tags")).isEqualTo(List.of("b"));
        assertThat((List<?>) s.get("resumePoints")).isEmpty();
    }

    @Test
    void sourceAttemptMustBelongToTheUserAndIsClearedWhenTheAnswerIsDeleted() {
        long me = user("src_me"), other = user("src_other");
        long[] mine = resume(me), theirs = resume(other);
        long attempt = attempt(me, mine[0]), foreign = attempt(other, theirs[0]);
        assertThatThrownBy(() -> stories.create(me, body("{\"title\":\"t\",\"sourceAttemptId\":" + foreign + "}"))).isInstanceOf(ApiException.class);
        long id = ((Number) stories.create(me, body("{\"title\":\"t\",\"action\":\"I did it\",\"sourceAttemptId\":" + attempt + "}")).get("id")).longValue();
        assertThat(((Number) stories.get(me, id).get("sourceAttemptId")).longValue()).isEqualTo(attempt);
        jdbc.update("DELETE FROM attempt WHERE id = ?", attempt);
        assertThat(stories.get(me, id).get("sourceAttemptId")).isNull();
    }

    @Test
    void anUnfinishedSessionCannotBeExported() {
        long me = user("exp_me"), other = user("exp_other");
        long[] r = resume(me);
        jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks, started_at) VALUES (?, ?, 'in_progress', 80, UTC_TIMESTAMP(3))", me, r[0]);
        long session = jdbc.queryForObject("SELECT MAX(id) FROM practice_session WHERE user_id = ?", Long.class, me);
        assertThatThrownBy(() -> exports.export(me, session)).isInstanceOf(ApiException.class).extracting("type").isEqualTo("session-not-completed");
        assertThatThrownBy(() -> exports.export(other, session)).isInstanceOf(ApiException.class).extracting("type").isEqualTo("not-found");
        jdbc.update("UPDATE practice_session SET status = 'abandoned' WHERE id = ?", session);
        assertThatThrownBy(() -> exports.export(me, session)).isInstanceOf(ApiException.class).extracting("type").isEqualTo("session-not-completed");
    }

    /** One question and one attempt of a fresh session. Returns the attempt id. */
    private long attempt(long me, long resume) {
        jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks, started_at, completed_at) VALUES (?, ?, 'completed', 80, UTC_TIMESTAMP(3), UTC_TIMESTAMP(3))", me, resume);
        long session = jdbc.queryForObject("SELECT MAX(id) FROM practice_session WHERE user_id = ?", Long.class, me);
        jdbc.update("INSERT INTO question (resume_id, type, theme, text, source, text_hash) VALUES (?, 'brief', 'teamwork', 'Tell me.', 'ai', SHA2(CONCAT('q', ?), 256))", resume, session);
        long question = jdbc.queryForObject("SELECT MAX(id) FROM question WHERE resume_id = ?", Long.class, resume);
        jdbc.update("INSERT INTO attempt (session_id, question_id, position, max_marks, answered_via, answer_text) VALUES (?, ?, 1, 5, 'typed', 'I did it')", session, question);
        return jdbc.queryForObject("SELECT MAX(id) FROM attempt WHERE session_id = ?", Long.class, session);
    }
}
