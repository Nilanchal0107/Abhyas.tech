package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;

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
import tech.abhyas.trainer.role.RoleSearch;

/** V2 counts and the search query on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class RoleSearchDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired JdbcTemplate jdbc;
    @Autowired RoleSearch search;

    private long count(String sql) { return jdbc.queryForObject(sql, Long.class); }

    @Test
    void v2LoadsTheStarterData() {
        assertThat(count("SELECT COUNT(*) FROM role WHERE owner_user_id IS NULL")).isEqualTo(42);
        assertThat(count("SELECT COUNT(*) FROM role_alias")).isEqualTo(143);
        assertThat(count("SELECT COUNT(*) FROM skill")).isEqualTo(228);
        assertThat(count("SELECT COUNT(*) FROM role_skill")).isEqualTo(466);
        assertThat(count("SELECT COUNT(DISTINCT LOWER(name)) FROM skill")).isEqualTo(228);
    }

    @Test
    void searchMatchesAliasAndNameCaseInsensitively() {
        List<Map<String, Object>> sde = search.search(1, "sde");
        assertThat(sde).extracting(m -> m.get("name")).contains("Software Engineer", "Test Automation Engineer");
        Map<String, Object> se = sde.stream().filter(m -> m.get("name").equals("Software Engineer")).findFirst().orElseThrow();
        assertThat(se).containsEntry("matchedOn", "alias").containsEntry("matchedAlias", "SDE").containsEntry("isOwn", false);

        Map<String, Object> byName = search.search(1, "BACKEND").get(0);
        assertThat(byName).containsEntry("name", "Backend Developer").containsEntry("matchedOn", "name");
    }

    @Test
    void capsAtTwentyAndTreatsWildcardsAsText() {
        assertThat(search.search(1, "e")).hasSize(20);
        assertThat(search.search(1, "%")).isEmpty();
        assertThat(search.search(1, "zzzz-no-role")).isEmpty();
    }

    @Test
    void ownRolesAreMarkedAndPrivateToTheirOwner() {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES ('alice', 'x'), ('bob', 'x')");
        long alice = count("SELECT id FROM app_user WHERE login_id = 'alice'");
        long bob = count("SELECT id FROM app_user WHERE login_id = 'bob'");
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('Prompt engineer', ?)", alice);
        jdbc.update("INSERT INTO role_alias (role_id, alias) SELECT id, 'PE-secret' FROM role WHERE name = 'Prompt engineer'");

        assertThat(search.search(alice, "prompt")).hasSize(1).first().satisfies(m -> assertThat(m).containsEntry("isOwn", true));
        assertThat(search.search(alice, "PE-secret")).hasSize(1);
        assertThat(search.search(bob, "prompt")).isEmpty();
        assertThat(search.search(bob, "PE-secret")).isEmpty();
    }
}
