package tech.abhyas.trainer.session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.resume.KeyFactService;

/** Contract tests for /sessions (ticket 13). The queries run on MySQL in SessionDbTest. */
class SessionApiTest {
    SessionService sessions = mock(SessionService.class);
    KeyFactService keyFacts = mock(KeyFactService.class);
    ExportService exports = mock(ExportService.class);
    TokenService tokens = new TokenService("test-secret-test-secret-test-secret-123", Clock.systemUTC());
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new SessionController(sessions, keyFacts, exports))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder r) { return r.cookie(new Cookie("rdt_session", tokens.issue(7L))); }

    @Test
    void startReturns201AndRetriesPendingKeyFacts() throws Exception {
        when(sessions.start(7L, 3L)).thenReturn(Map.of("id", 41L, "status", "in_progress"));
        mvc.perform(as(post("/api/sessions")).contentType(MediaType.APPLICATION_JSON).content("{\"resumeId\":3}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.session.id").value(41));
        verify(keyFacts).retryInBackground(7L);
    }

    @Test
    void startNeedsAResumeAndReportsAnOpenSession() throws Exception {
        mvc.perform(as(post("/api/sessions")).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        when(sessions.start(7L, 3L)).thenThrow(new ApiException(HttpStatus.CONFLICT, "session-open", "x", Map.of("sessionId", 5L)));
        mvc.perform(as(post("/api/sessions")).contentType(MediaType.APPLICATION_JSON).content("{\"resumeId\":3}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("session-open")).andExpect(jsonPath("$.sessionId").value(5));
    }

    @Test
    void listGetAnswerAndAbandon() throws Exception {
        when(sessions.list(7L, "in_progress", 1)).thenReturn(List.of(Map.of("id", 41L)));
        when(sessions.detail(7L, 41L)).thenReturn(Map.of("id", 41L, "attempts", List.of()));
        when(sessions.abandon(7L, 41L)).thenReturn(Map.of("id", 41L, "status", "abandoned"));
        when(sessions.saveAnswer(eq(7L), eq(41L), eq(2), any())).thenReturn(Map.of("position", 2));
        mvc.perform(as(get("/api/sessions?status=in_progress&limit=1"))).andExpect(status().isOk()).andExpect(jsonPath("$.sessions[0].id").value(41));
        mvc.perform(as(get("/api/sessions/41"))).andExpect(status().isOk()).andExpect(jsonPath("$.session.id").value(41));
        mvc.perform(as(post("/api/sessions/41/abandon"))).andExpect(status().isOk()).andExpect(jsonPath("$.session.status").value("abandoned"));
        mvc.perform(as(put("/api/sessions/41/attempts/2")).contentType(MediaType.APPLICATION_JSON).content("{\"answer\":{\"selectedOptions\":[\"A\"]}}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.attempt.position").value(2));
    }

    @Test
    void closedSessionAndAnotherUsersSession() throws Exception {
        when(sessions.saveAnswer(eq(7L), eq(41L), anyInt(), any())).thenThrow(new ApiException(HttpStatus.CONFLICT, "session-closed", "x"));
        when(sessions.detail(7L, 9L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(put("/api/sessions/41/attempts/1")).contentType(MediaType.APPLICATION_JSON).content("{\"answer\":null}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("session-closed"));
        mvc.perform(as(get("/api/sessions/9"))).andExpect(status().isNotFound());
        when(sessions.abandon(7L, 9L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(post("/api/sessions/9/abandon"))).andExpect(status().isNotFound());
        when(sessions.saveAnswer(eq(7L), eq(9L), anyInt(), any())).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(put("/api/sessions/9/attempts/1")).contentType(MediaType.APPLICATION_JSON).content("{\"answer\":null}")).andExpect(status().isNotFound());
    }

    @Test
    void exportIsADownloadAndRefusesUnfinishedOrForeignSessions() throws Exception {
        when(exports.export(7L, 41L)).thenReturn(Map.of("description", "d", "metadata", Map.of("sessionDate", "2026-10-03")));
        mvc.perform(as(get("/api/sessions/41/export"))).andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"practice-session-41-2026-10-03.json\""))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("d"));
        when(exports.export(7L, 42L)).thenThrow(new ApiException(HttpStatus.CONFLICT, "session-not-completed", "x"));
        mvc.perform(as(get("/api/sessions/42/export"))).andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("session-not-completed"));
        when(exports.export(7L, 9L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(get("/api/sessions/9/export"))).andExpect(status().isNotFound());
        mvc.perform(get("/api/sessions/41/export")).andExpect(status().isUnauthorized());
    }

    @Test
    void needLogin() throws Exception {
        mvc.perform(get("/api/sessions")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/sessions/1/abandon")).andExpect(status().isUnauthorized());
    }

    @Test
    void transcribeTakesAnAudioFileAndMapsItsErrors() throws Exception {
        var audio = new org.springframework.mock.web.MockMultipartFile("audio", "answer.webm", "audio/webm", new byte[] {1, 2, 3});
        when(sessions.transcribe(7L, 41L, 1, new byte[] {1, 2, 3}, "audio/webm")).thenReturn(Map.of("transcript", "hello", "stats", Map.of("wordCount", 1)));
        mvc.perform(multipart("/api/sessions/41/attempts/1/transcribe").file(audio).cookie(new Cookie("rdt_session", tokens.issue(7L)))).andExpect(status().isOk())
                .andExpect(jsonPath("$.transcript").value("hello")).andExpect(jsonPath("$.stats.wordCount").value(1));
        when(sessions.transcribe(eq(7L), eq(41L), eq(2), any(), any())).thenThrow(new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ai-unavailable", "x"));
        mvc.perform(multipart("/api/sessions/41/attempts/2/transcribe").file(audio).cookie(new Cookie("rdt_session", tokens.issue(7L)))).andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("ai-unavailable"));
        mvc.perform(multipart("/api/sessions/41/attempts/1/transcribe").file(audio)).andExpect(status().isUnauthorized());
    }

    @Test
    void submitReturns202WhenItStartsGradingAnd200WhenRepeated() throws Exception {
        when(sessions.submit(7L, 41L)).thenReturn(new SessionService.Submitted(true, Map.of("id", 41L, "status", "completed", "gradingComplete", false)));
        mvc.perform(as(post("/api/sessions/41/submit"))).andExpect(status().isAccepted())
                .andExpect(jsonPath("$.session.status").value("completed")).andExpect(jsonPath("$.session.gradingComplete").value(false));
        when(sessions.submit(7L, 42L)).thenReturn(new SessionService.Submitted(false, Map.of("id", 42L, "status", "completed")));
        mvc.perform(as(post("/api/sessions/42/submit"))).andExpect(status().isOk()).andExpect(jsonPath("$.session.id").value(42));
        when(sessions.submit(7L, 43L)).thenThrow(new ApiException(HttpStatus.CONFLICT, "session-closed", "x"));
        mvc.perform(as(post("/api/sessions/43/submit"))).andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("session-closed"));
        when(sessions.submit(7L, 9L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(post("/api/sessions/9/submit"))).andExpect(status().isNotFound());
        mvc.perform(post("/api/sessions/41/submit")).andExpect(status().isUnauthorized());
    }
}
