package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.auth.UserRepository;

/**
 * Deleting an app_user removes everything of theirs and nothing shared (database-schema decision 9).
 * Extend the insert block as later tickets add tables. Skipped when Docker is not installed.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class AccountCascadeTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired UserRepository users;

    @Test
    void deletingAUserCascadesToTheirRowsAndLeavesSharedTablesAlone() {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES ('gone', 'x'), ('stays', 'x')");
        long gone = jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = 'gone'", Long.class);
        // shared starter data (owner_user_id is NULL) and the user's own role
        jdbc.update("INSERT INTO skill (name) VALUES ('Java')");
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('Starter role', NULL)");
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('My own role', ?)", gone);
        long sharedRoles = count("role WHERE owner_user_id IS NULL");
        long skills = count("skill");

        users.deleteById(gone);

        assertThat(count("app_user WHERE login_id = 'gone'")).isZero();
        assertThat(count("role WHERE name = 'My own role'")).isZero();
        assertThat(count("app_user WHERE login_id = 'stays'")).isEqualTo(1);
        assertThat(count("role WHERE owner_user_id IS NULL")).isEqualTo(sharedRoles);
        assertThat(count("skill")).isEqualTo(skills);
    }

    private long count(String fromWhere) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + fromWhere, Long.class);
    }
}
