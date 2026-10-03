package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.resume.ResumeService;
import tools.jackson.databind.json.JsonMapper;

/** The voice part with Whisper available (AiClient is faked), on a real MySQL 8. Skipped when Docker is not installed. */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "app.jwt.secret=test-secret-test-secret-test-secret-123")
class SessionVoiceDbTest {
    @Container
    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.0").withUrlParam("serverTimezone", "UTC");

    @DynamicPropertySource
    static void db(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", MYSQL::getJdbcUrl);
        r.add("spring.datasource.username", MYSQL::getUsername);
        r.add("spring.datasource.password", MYSQL::getPassword);
    }

    @MockitoBean AiClient ai;
    @Autowired JdbcTemplate jdbc;
    @Autowired ResumeService resumes;
    @Autowired SessionService service;
    static final JsonMapper JSON = JsonMapper.builder().build();

    private static Object q(Map<String, Object> a, String key) { return ((Map<?, ?>) a.get("question")).get(key); }

    @Test
    @SuppressWarnings("unchecked")
    void voicePartHasOneTechnicalAndTwoBehavioralSlotsAndATypedAnswerIsWorthFive() {
        when(ai.available(Provider.GROQ_WHISPER)).thenReturn(true);
        when(ai.json(any(), any(), any())).thenReturn(Optional.empty());
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES ('voice_me', 'x')");
        long me = jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = 'voice_me'", Long.class);
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        long resume = (Long) resumes.save(me, "r.pdf", role, null, "openresume", JSON.readTree("{\"experience\":[],\"projects\":[],\"skills\":[]}")).get("id");
        jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'voice', 'Tech?', 'ai', ?)", resume, SessionService.hash("tech" + resume));
        for (int i = 0; i < 2; i++) {
            jdbc.update("INSERT INTO question (resume_id, type, theme, text, source, text_hash) VALUES (?, 'voice', 'conflict', ?, 'ai', ?)", resume, "Behavioral " + i + "?", SessionService.hash("beh" + i + resume));
        }
        Map<String, Object> s = service.start(me, resume);
        List<Map<String, Object>> attempts = (List<Map<String, Object>>) s.get("attempts");
        assertThat(s).containsEntry("voiceSkipped", false).containsEntry("maxMarks", 30); // only the voice part had questions
        assertThat(attempts).hasSize(3).extracting(a -> q(a, "isBehavioral")).containsExactly(false, true, true);
        assertThat(attempts).extracting(a -> a.get("maxMarks")).containsOnly(10);

        long id = ((Number) s.get("id")).longValue();
        Map<?, ?> typed = service.saveAnswer(me, id, 1, JSON.readTree("{\"answer\":{\"text\":\"I typed it.\",\"answeredVia\":\"typed\"}}"));
        assertThat(typed.get("maxMarks")).isEqualTo(5);
        assertThat(((Map<?, ?>) typed.get("answer")).get("voiceStats")).isNull();
        Map<?, ?> recorded = service.saveAnswer(me, id, 1, JSON.readTree(
                "{\"answer\":{\"text\":null,\"transcript\":\"At Acme\",\"voiceStats\":{\"wordsPerMinute\":10,\"fillerCount\":0,\"wordCount\":2,\"durationSeconds\":12},\"answeredVia\":\"voice\"}}"));
        assertThat(recorded.get("maxMarks")).isEqualTo(10); // recording again takes the 5-mark cut back
        assertThat(((Map<String, Object>) recorded.get("answer"))).containsEntry("transcript", "At Acme").containsEntry("answeredVia", "voice");
        assertThat(service.saveAnswer(me, id, 1, JSON.readTree("{\"answer\":null}")).get("answer")).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void transcribeReturnsTranscriptAndStatsAndStoresNothing() {
        when(ai.json(any(), any(), any())).thenReturn(Optional.empty());
        jdbc.update("INSERT INTO app_user (login_id, password_hash) VALUES ('tr_me', 'x')");
        long me = jdbc.queryForObject("SELECT id FROM app_user WHERE login_id = 'tr_me'", Long.class);
        long role = jdbc.queryForObject("SELECT MIN(id) FROM role WHERE owner_user_id IS NULL", Long.class);
        long resume = (Long) resumes.save(me, "r.pdf", role, null, "openresume", JSON.readTree("{\"experience\":[],\"projects\":[],\"skills\":[]}")).get("id");
        jdbc.update("INSERT INTO question (resume_id, type, text, source, text_hash) VALUES (?, 'voice', 'Tech?', 'ai', ?)", resume, SessionService.hash("t" + resume));
        when(ai.available(Provider.GROQ_WHISPER)).thenReturn(true);
        long id = ((Number) service.start(me, resume).get("id")).longValue();
        when(ai.transcribe(any(), any())).thenReturn(Optional.of(JSON.readTree("{\"text\":\"Um we shipped it\",\"duration\":6.0}")));

        Map<String, Object> r = service.transcribe(me, id, 1, new byte[] {1, 2, 3}, "audio/webm;codecs=opus");
        assertThat(r).containsEntry("transcript", "Um we shipped it");
        assertThat(((Map<String, Object>) r.get("stats"))).containsEntry("wordCount", 4).containsEntry("fillerCount", 1).containsEntry("wordsPerMinute", 40);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM attempt WHERE session_id = ? AND (transcript IS NOT NULL OR voice_stats IS NOT NULL)", Long.class, id)).isZero();

        when(ai.transcribe(any(), any())).thenReturn(Optional.empty());
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.transcribe(me, id, 1, new byte[] {1}, "audio/webm"))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("ai-unavailable"));
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.transcribe(me, id, 1, new byte[] {1}, "audio/mp3"))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.transcribe(me, id, 1, new byte[5 * 1024 * 1024 + 1], "audio/webm"))
                .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("file-too-large"));
    }
}
