package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.resume.KeyFactService;
import tech.abhyas.trainer.resume.ResumeService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Key facts drafting and retry (ticket 11) on a real MySQL 8, with a fake AI. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class KeyFactDbTest {
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
    @Autowired NamedParameterJdbcTemplate named;
    @Autowired TransactionTemplate tx;
    @Autowired ResumeService resumes;

    AiClient ai = mock(AiClient.class);
    AtomicReference<Instant> now = new AtomicReference<>(Instant.now());
    Clock clock = new Clock() {
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId z) { return this; }
        @Override public Instant instant() { return now.get(); }
    };
    KeyFactService service = new KeyFactService(ai, named, tx, clock);

    /** Plays the AI: the reply goes through the same validity check AiClient applies (invalid means empty). */
    private void aiReplies(String json) {
        when(ai.json(eq(Provider.GROQ_CHAT), any(), any())).thenAnswer(inv -> {
            Predicate<JsonNode> valid = inv.getArgument(2);
            return Optional.ofNullable(json).map(JSON::readTree).filter(valid);
        });
    }

    private long resumeWith(String login, int bullets) {
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES (?, 'x')", login);
        long me = jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = ?", Long.class, login);
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        String b = String.join(",", java.util.Collections.nCopies(bullets, "\"Built an API\""));
        return (Long) resumes.save(me, "R.pdf", role, null, "openresume",
                JSON.readTree("{\"profile\":{\"name\":\"A\"},\"experience\":[{\"company\":\"Acme\",\"bullets\":[" + b + "]}],\"projects\":[],\"skills\":[]}")).get("id");
    }

    private Map<String, Object> point(long resume, int position) {
        return jdbc.queryForMap("SELECT id, key_facts_status AS status, key_facts_tries AS tries, last_try_at FROM resume_point WHERE resume_id = ? AND position = ?", resume, position);
    }

    @Test
    void successStoresFactsInOrderAndMarksReady() {
        long r = resumeWith("kf_ok", 2);
        aiReplies("{\"keyFacts\":[\" First \",\"\",\"Second\"]}");
        assertThat(service.draftPending(r)).isEqualTo(2);
        Map<String, Object> p = point(r, 1);
        assertThat(p).containsEntry("status", "ready");
        assertThat(jdbc.queryForList("SELECT fact_text FROM key_fact WHERE resume_point_id = ? ORDER BY position", String.class, p.get("id")))
                .containsExactly("First", "Second");
        assertThat(service.draftPending(r)).isZero(); // ready points are not drafted again
    }

    @Test
    void invalidJsonOrUnavailableAiCountsATryAndStopsThePass() {
        long r = resumeWith("kf_bad", 3);
        for (String reply : new String[] {"{\"keyFacts\":[]}", "{\"other\":1}", null}) {
            now.set(now.get().plus(Duration.ofMinutes(11)));
            aiReplies(reply);
            service.draftPending(r);
        }
        Map<String, Object> first = point(r, 1);
        assertThat(first).containsEntry("status", "pending");
        assertThat(((Number) first.get("tries")).intValue()).isEqualTo(3);
        assertThat(first.get("last_try_at")).isNotNull();
        assertThat(((Number) point(r, 2).get("tries")).intValue()).isZero(); // the pass stopped at the first failure
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM key_fact", Long.class)).isZero();
    }

    @Test
    void aFailedPointWaitsTenMinutesAndIsNeverCalledInATightLoop() {
        long r = resumeWith("kf_delay", 1);
        aiReplies(null);
        service.draftPending(r);
        service.draftPending(r);
        now.set(now.get().plus(Duration.ofMinutes(9)));
        service.draftPending(r);
        verify(ai, times(1)).json(any(), any(), any()); // still inside the 10 minutes

        now.set(now.get().plus(Duration.ofMinutes(2)));
        aiReplies("{\"keyFacts\":[\"Fact\"]}");
        assertThat(service.draftPending(r)).isEqualTo(1);
        assertThat(point(r, 1)).containsEntry("status", "ready");
    }
}
