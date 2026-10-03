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
import tech.abhyas.trainer.role.RoleSearch;
import tech.abhyas.trainer.role.RoleService;

/** Add a missing role on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class RoleAddDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired RoleService service;
    @Autowired RoleSearch search;

    private long user(String login) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        return jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
    }

    @Test
    void savesAPrivateRoleAndReusesSkillsByCaseInsensitiveName() {
        long me = user("role_owner"), other = user("role_other");
        long skillsBefore = jdbc.queryForObject("SELECT COUNT(*) FROM skill", Long.class);
        Map<String, Object> role = service.add(me, " Prompt engineer ", List.of("PE"),
                List.of("python", "Prompt Evaluation", "PYTHON"));

        assertThat(role).containsEntry("name", "Prompt engineer").containsEntry("category", null).containsEntry("isOwn", true);
        assertThat((List<?>) role.get("skills")).hasSize(2);
        // "python" matched the starter skill, only "Prompt Evaluation" is new
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM skill", Long.class)).isEqualTo(skillsBefore + 1);
        assertThat(search.search(me, "Prompt eng")).extracting(m -> m.get("isOwn")).containsExactly(true);
        assertThat(search.search(other, "Prompt eng")).isEmpty();
    }

    @Test
    void nearDuplicateNameOrAliasIsRoleExists() {
        long me = user("role_dup");
        for (List<String> args : List.of(List.of("software-engineer", "x1"), List.of("Brand New Role", "sde"))) {
            assertThatThrownBy(() -> service.add(me, args.get(0), List.of(args.get(1)), List.of()))
                    .isInstanceOfSatisfying(ApiException.class, e -> {
                        assertThat(e.type()).isEqualTo("role-exists");
                        assertThat(e.extras()).containsEntry("existingRoleName", "Software Engineer");
                    });
        }
    }
}
