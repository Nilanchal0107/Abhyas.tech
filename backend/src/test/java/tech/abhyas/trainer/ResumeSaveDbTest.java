package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
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

/** Save a resume on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class ResumeSaveDbTest {
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
    static final JsonMapper JSON = JsonMapper.builder().build();

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    private long starterRole() { return jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class); }

    private static JsonNode parsed(String skills, String bullets) {
        return JSON.readTree("""
                {"profile":{"name":"Asha","email":"a@x.com","phone":"123"},"education":[],
                 "experience":[{"company":"Acme","bullets":[%s]}],
                 "projects":[{"name":"Order Service","bullets":["P1"]}],"skills":[%s]}""".formatted(bullets, skills));
    }

    @Test
    void savesResumeAndPointsInOrderWithSourcePathAndMatchedSkills() {
        long me = user("res_owner");
        long skillsBefore = jdbc.queryForObject("SELECT COUNT(*) FROM skill", Long.class);
        Map<String, Object> out = service.save(me, "R.pdf", starterRole(), "2026-11-20", "openresume",
                parsed("\"python\",\"Zzz Unknown Skill\"", "\"E1\",\"E2\""));

        assertThat(out).containsEntry("pointCount", 5).containsEntry("interviewDate", "2026-11-20");
        long id = (Long) out.get("id");
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT kind, source_ref, text, skill_id FROM resume_point WHERE resume_id = ? ORDER BY position", id);
        assertThat(rows).extracting(r -> r.get("kind")).containsExactly(
                "experience_bullet", "experience_bullet", "project_bullet", "skill", "skill");
        assertThat(rows).extracting(r -> r.get("source_ref")).containsExactly(
                "experience[0].bullets[0]", "experience[0].bullets[1]", "projects[0].bullets[0]", "skills[0]", "skills[1]");
        assertThat(rows.get(3).get("skill_id")).isNotNull(); // "python" matched the starter skill
        assertThat(rows.get(4).get("skill_id")).isNull();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM skill", Long.class)).isEqualTo(skillsBefore);
    }

    @Test
    void anotherUsersPrivateRoleIsNotFound() {
        long owner = user("res_role_owner"), other = user("res_role_other");
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('Mine', ?)", owner);
        long roleId = jdbc.queryForObject("SELECT id FROM role WHERE owner_user_id = ?", Long.class, owner);
        service.save(owner, "R.pdf", roleId, null, "openresume", parsed("", ""));
        assertThatThrownBy(() -> service.save(other, "R.pdf", roleId, null, "openresume", parsed("", "")))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("not-found"));
        assertThatThrownBy(() -> service.save(other, "R.pdf", -1L, null, "openresume", parsed("", "")))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("not-found"));
    }

    @Test
    void limitsAreValidationErrorsAndNothingIsSaved() {
        long me = user("res_limits");
        long before = jdbc.queryForObject("SELECT COUNT(*) FROM resume", Long.class);
        String bullets51 = String.join(",", Collections.nCopies(51, "\"b\""));
        String long501 = "\"" + "x".repeat(501) + "\"";
        for (JsonNode bad : List.of(parsed("", bullets51), parsed("", long501))) {
            assertThatThrownBy(() -> service.save(me, "R.pdf", starterRole(), null, "openresume", bad))
                    .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
        // 4 entries of 50 bullets = 200, plus one project bullet = 201 points
        String fifty = String.join(",", Collections.nCopies(50, "\"b\""));
        JsonNode tooMany = JSON.readTree("""
                {"experience":[{"bullets":[%1$s]},{"bullets":[%1$s]},{"bullets":[%1$s]},{"bullets":[%1$s]}],
                 "projects":[{"name":"P","bullets":["x"]}],"skills":[]}""".formatted(fifty));
        assertThatThrownBy(() -> service.save(me, "R.pdf", starterRole(), null, "openresume", tooMany))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        assertThatThrownBy(() -> service.save(me, "R.pdf", starterRole(), "not-a-date", "openresume", parsed("", "")))
                .isInstanceOf(ApiException.class);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM resume", Long.class)).isEqualTo(before);
    }
}
