package tech.abhyas.trainer;

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
import tech.abhyas.trainer.resume.ResumeService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** List, open, change the date of and delete resumes on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class ResumeListDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired ResumeService service;
    static final JsonNode PARSED = JsonMapper.builder().build().readTree(
            "{\"experience\":[{\"bullets\":[\"E1\"]}],\"projects\":[],\"skills\":[\"Java\"]}");

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    private long save(long me, String file) {
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        return (Long) service.save(me, file, role, null, "openresume", PARSED).get("id");
    }

    private static void assertNotFound(Runnable call) {
        assertThatThrownBy(call::run).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("not-found"));
    }

    @Test
    void listIsMostRecentlyUsedThenNewestAndOnlyMine() {
        long me = user("list_me"), other = user("list_other");
        long old = save(me, "old.pdf"), used = save(me, "used.pdf"), newest = save(me, "newest.pdf");
        save(other, "theirs.pdf");
        jdbc.update("UPDATE resume SET created_at = '2026-01-01 00:00:00.000' WHERE id = ?", old);
        jdbc.update("UPDATE resume SET last_used_at = '2026-02-01 00:00:00.000' WHERE id = ?", used);

        List<Map<String, Object>> list = service.list(me);
        assertThat(list).extracting(r -> r.get("id")).containsExactly(used, newest, old);
        assertThat(list.get(0)).containsEntry("pointCount", 2).containsEntry("masteredCount", 0)
                .containsEntry("lastUsedAt", "2026-02-01T00:00:00.000Z");
        assertThat(list.get(1).get("lastUsedAt")).isNull();
    }

    @Test
    void openShowsPointsAndAnotherUsersResumeIsNotFound() {
        long me = user("open_me"), other = user("open_other");
        long id = save(me, "r.pdf");
        Map<String, Object> r = service.get(me, id);
        assertThat((List<?>) r.get("points")).hasSize(2);
        assertThat(((Map<?, ?>) ((List<?>) r.get("points")).get(0)).get("kind")).isEqualTo("experience_bullet");
        assertThat(((JsonNode) r.get("parsed")).path("skills").get(0).asString()).isEqualTo("Java");

        assertNotFound(() -> service.get(other, id));
        assertNotFound(() -> service.setInterviewDate(other, id, Map.of("interviewDate", "2026-12-01")));
        assertNotFound(() -> service.delete(other, id));
        assertThat(service.get(me, id)).isNotNull(); // untouched
    }

    @Test
    void patchChangesOnlyTheInterviewDate() {
        long me = user("patch_me");
        long id = save(me, "r.pdf");
        assertThat(service.setInterviewDate(me, id, Map.of("interviewDate", "2026-12-01"))).containsEntry("interviewDate", "2026-12-01");
        java.util.HashMap<String, Object> clear = new java.util.HashMap<>();
        clear.put("interviewDate", null);
        assertThat(service.setInterviewDate(me, id, clear).get("interviewDate")).isNull();
        for (Map<String, Object> bad : List.of(Map.<String, Object>of("fileName", "x"), Map.<String, Object>of("interviewDate", "nope"),
                Map.<String, Object>of("interviewDate", "2026-12-01", "fileName", "x"))) {
            assertThatThrownBy(() -> service.setInterviewDate(me, id, bad))
                    .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
        assertThat(service.get(me, id)).containsEntry("fileName", "r.pdf");
    }

    @Test
    @SuppressWarnings("unchecked")
    void roleSkillsCheckSplitsRoleSkillsByEvidenceAndReplacesTheSnapshot() {
        long me = user("check_me"), other = user("check_other");
        // Java is on the resume (a skill point); Docker and Redis are role skills with no evidence
        jdbc.update("INSERT IGNORE INTO skill (name) VALUES ('Java'), ('Docker'), ('Redis')");
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('Check role', ?)", me);
        long role = jdbc.queryForObject("SELECT id FROM role WHERE name = 'Check role'", Long.class);
        jdbc.update("INSERT INTO role_skill (role_id, skill_id) SELECT ?, id FROM skill WHERE name IN ('Java', 'Docker', 'Redis')", role);
        long id = (Long) service.save(me, "r.pdf", role, null, "openresume", PARSED).get("id");

        Map<String, Object> r = service.roleSkillsCheck(me, id);
        List<Map<String, Object>> with = (List<Map<String, Object>>) r.get("withEvidence");
        assertThat(with).hasSize(1);
        assertThat(with.get(0)).containsEntry("skill", "Java");
        assertThat((List<?>) with.get(0).get("resumePoints")).hasSize(1); // the E1 bullet has no skill_id, so it is not evidence
        assertThat(((List<Map<String, Object>>) r.get("withoutEvidence"))).extracting(s -> s.get("skill")).containsExactly("Docker", "Redis");
        assertThat(r).containsEntry("resumeId", id).containsKey("computedAt");
        assertThat(jdbc.queryForObject("SELECT JSON_LENGTH(result, '$.withEvidence') FROM gap_result WHERE resume_id = ?", Long.class, id)).isEqualTo(1);

        jdbc.update("UPDATE gap_result SET result = JSON_OBJECT('withEvidence', JSON_ARRAY(), 'withoutEvidence', JSON_ARRAY()) WHERE resume_id = ?", id);
        service.roleSkillsCheck(me, id); // recomputed: the stale snapshot is replaced, still one row
        assertThat(jdbc.queryForObject("SELECT JSON_LENGTH(result, '$.withEvidence') FROM gap_result WHERE resume_id = ?", Long.class, id)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM gap_result WHERE resume_id = ?", Long.class, id)).isEqualTo(1);

        assertNotFound(() -> service.roleSkillsCheck(other, id));
    }

    @Test
    void deleteRemovesTheResumeWithItsRowsUnlessASessionIsOpen() {
        long me = user("del_me");
        long id = save(me, "r.pdf");
        long point = jdbc.queryForObject("SELECT MIN(id) FROM resume_point WHERE resume_id = ?", Long.class, id);
        jdbc.update("INSERT INTO key_fact (resume_point_id, position, fact_text) VALUES (?, 1, 'fact')", point);
        jdbc.update("INSERT INTO question (resume_id, resume_point_id, type, text, source, text_hash) VALUES (?, ?, 'brief', 'Q?', 'ai', REPEAT('a', 64))", id, point);
        jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks) VALUES (?, ?, 'in_progress', 19)", me, id);

        assertThatThrownBy(() -> service.delete(me, id)).isInstanceOfSatisfying(ApiException.class, e -> {
            assertThat(e.type()).isEqualTo("session-open");
            assertThat(e.extras()).containsKey("sessionId");
        });

        jdbc.update("UPDATE practice_session SET status = 'abandoned' WHERE resume_id = ?", id);
        service.delete(me, id);
        for (String t : List.of("resume", "practice_session")) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + t + (t.equals("resume") ? " WHERE id = ?" : " WHERE resume_id = ?"), Long.class, id)).isZero();
        }
        for (String t : List.of("resume_point", "question")) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM " + t + " WHERE resume_id = ?", Long.class, id)).isZero();
        }
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM key_fact WHERE resume_point_id = ?", Long.class, point)).isZero();
    }
}
