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
        // a resume with a resume point for each user (ticket 10)
        long starter = jdbc.queryForObject("SELECT id FROM role WHERE name = 'Starter role'", Long.class);
        for (String login : new String[] {"gone", "stays"}) {
            jdbc.update("INSERT INTO resume (user_id, file_name, target_role_id, parsed_json, parse_source) "
                    + "SELECT id, 'r.pdf', ?, '{}', 'openresume' FROM app_user WHERE login_id = ?", starter, login);
        }
        jdbc.update("INSERT INTO resume_point (resume_id, kind, source_ref, text, position) "
                + "SELECT id, 'skill', 'skills[0]', 'Java', 1 FROM resume");

        // a story with a tag and a linked resume point for each user (ticket 25)
        jdbc.update("INSERT INTO story (user_id, title) SELECT id, 'A story' FROM app_user WHERE login_id IN ('gone', 'stays')");
        jdbc.update("INSERT INTO story_tag (story_id, tag) SELECT id, 'teamwork' FROM story");
        jdbc.update("INSERT INTO story_resume_point (story_id, resume_point_id) "
                + "SELECT s.id, p.id FROM story s JOIN resume r ON r.user_id = s.user_id JOIN resume_point p ON p.resume_id = r.id");

        users.deleteById(gone);

        assertThat(count("resume WHERE user_id = " + gone)).isZero();
        assertThat(count("resume")).isEqualTo(1);
        assertThat(count("resume_point")).isEqualTo(1);

        assertThat(count("story")).isEqualTo(1);
        assertThat(count("story_tag")).isEqualTo(1);
        assertThat(count("story_resume_point")).isEqualTo(1);

        assertThat(count("app_user WHERE login_id = 'gone'")).isZero();
        assertThat(count("role WHERE name = 'My own role'")).isZero();
        assertThat(count("app_user WHERE login_id = 'stays'")).isEqualTo(1);
        assertThat(count("role WHERE owner_user_id IS NULL")).isEqualTo(sharedRoles);
        assertThat(count("skill")).isEqualTo(skills);
    }

    private long count(String fromWhere) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM " + fromWhere, Long.class);
    }

    /** Ticket 28: one user with a row in every user table, deleted, leaves nothing; the shared tables keep their rows. */
    @Test
    void deletingAUserLeavesNoRowInAnyUserTable() {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES ('full', 'x')");
        long me = jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = 'full'", Long.class);
        jdbc.update("INSERT INTO skill (name) VALUES ('Kept skill')");
        long skill = jdbc.queryForObject("SELECT id FROM skill WHERE name = 'Kept skill'", Long.class);
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES ('Full role', ?)", me);
        long role = jdbc.queryForObject("SELECT id FROM role WHERE name = 'Full role'", Long.class);
        jdbc.update("INSERT INTO role_alias (role_id, alias) VALUES (?, 'full alias')", role);
        jdbc.update("INSERT INTO role_skill (role_id, skill_id) VALUES (?, ?)", role, skill);
        jdbc.update("INSERT INTO resume (user_id, file_name, target_role_id, parsed_json, parse_source) VALUES (?, 'r.pdf', ?, '{}', 'openresume')", me, role);
        long resume = jdbc.queryForObject("SELECT id FROM resume WHERE user_id = ?", Long.class, me);
        jdbc.update("INSERT INTO resume_point (resume_id, kind, source_ref, text, position) VALUES (?, 'skill', 'skills[0]', 'Java', 1)", resume);
        long point = jdbc.queryForObject("SELECT id FROM resume_point WHERE resume_id = ?", Long.class, resume);
        jdbc.update("INSERT INTO key_fact (resume_point_id, fact_text, position) VALUES (?, 'a fact', 1)", point);
        jdbc.update("INSERT INTO gap_result (resume_id, result, computed_at) VALUES (?, '{}', UTC_TIMESTAMP(3))", resume);
        jdbc.update("INSERT INTO bank_question (type, skill_id, text_template, source, text_hash) VALUES ('brief', ?, 'Shared?', 'seed', SHA2('shared', 256))", skill);
        jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'brief', 'Tell me.', 'ai', SHA2('q', 256))", resume);
        long question = jdbc.queryForObject("SELECT id FROM question WHERE resume_id = ?", Long.class, resume);
        jdbc.update("INSERT INTO practice_session (user_id, resume_id, status, max_marks, started_at) VALUES (?, ?, 'completed', 80, UTC_TIMESTAMP(3))", me, resume);
        long session = jdbc.queryForObject("SELECT id FROM practice_session WHERE user_id = ?", Long.class, me);
        jdbc.update("INSERT INTO attempt (session_id, question_id, position, max_marks) VALUES (?, ?, 1, 5)", session, question);
        long attempt = jdbc.queryForObject("SELECT id FROM attempt WHERE session_id = ?", Long.class, session);
        jdbc.update("INSERT INTO story (user_id, title, source_attempt_id) VALUES (?, 'S', ?)", me, attempt);
        long story = jdbc.queryForObject("SELECT id FROM story WHERE user_id = ?", Long.class, me);
        jdbc.update("INSERT INTO story_tag (story_id, tag) VALUES (?, 't')", story);
        jdbc.update("INSERT INTO story_resume_point (story_id, resume_point_id) VALUES (?, ?)", story, point);
        long banks = count("bank_question"), skills = count("skill");

        users.deleteById(me);

        for (String table : List.of("resume", "resume_point", "key_fact", "gap_result", "question", "practice_session", "attempt", "story", "story_tag", "story_resume_point", "role_alias", "role_skill")) {
            assertThat(count(table + " WHERE 1 = 1 AND " + owner(table, me, role, resume, point, session, story))).as(table).isZero();
        }
        assertThat(count("role WHERE id = " + role)).isZero();
        assertThat(count("bank_question")).isEqualTo(banks);
        assertThat(count("skill")).isEqualTo(skills);
    }

    /** A condition that finds the rows of the deleted user in each table. */
    private static String owner(String table, long me, long role, long resume, long point, long session, long story) {
        return switch (table) {
            case "resume", "practice_session", "story" -> "user_id = " + me;
            case "resume_point", "question", "gap_result" -> "resume_id = " + resume;
            case "key_fact", "story_resume_point" -> "resume_point_id = " + point;
            case "attempt" -> "session_id = " + session;
            case "story_tag" -> "story_id = " + story;
            default -> "role_id = " + role; // role_alias, role_skill
        };
    }
}
