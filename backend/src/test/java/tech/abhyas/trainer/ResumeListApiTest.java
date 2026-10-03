package tech.abhyas.trainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import tech.abhyas.trainer.resume.ResumeController;
import tech.abhyas.trainer.resume.ResumeService;

/** Contract tests for GET, PATCH and DELETE /resumes (ticket 10). The queries run on MySQL in ResumeListDbTest. */
class ResumeListApiTest {
    ResumeService resumes = mock(ResumeService.class);
    TokenService tokens = new TokenService(AuthApiTest.SECRET, Clock.systemUTC());
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ResumeController(null, resumes))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder r) { return r.cookie(new Cookie("rdt_session", tokens.issue(7L))); }

    private static ApiException notFound() { return new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that resume."); }

    @Test
    void listReturnsTheUsersResumes() throws Exception {
        when(resumes.list(7L)).thenReturn(List.of(Map.of("id", 3L, "fileName", "A.pdf")));
        mvc.perform(as(get("/api/resumes"))).andExpect(status().isOk()).andExpect(jsonPath("$.resumes[0].id").value(3));
    }

    @Test
    void getReturnsOneResume() throws Exception {
        when(resumes.get(7L, 3L)).thenReturn(Map.of("id", 3L, "points", List.of()));
        mvc.perform(as(get("/api/resumes/3"))).andExpect(status().isOk()).andExpect(jsonPath("$.resume.id").value(3));
    }

    @Test
    void patchPassesTheBodyAndReturnsTheSummary() throws Exception {
        when(resumes.setInterviewDate(7L, 3L, Map.of("interviewDate", "2026-12-01"))).thenReturn(Map.of("id", 3L, "interviewDate", "2026-12-01"));
        mvc.perform(as(patch("/api/resumes/3")).contentType(MediaType.APPLICATION_JSON).content("{\"interviewDate\":\"2026-12-01\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.resume.interviewDate").value("2026-12-01"));
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(as(delete("/api/resumes/3"))).andExpect(status().isNoContent());
        verify(resumes).delete(7L, 3L);
    }

    @Test
    void notFoundForEveryEndpoint() throws Exception {
        when(resumes.get(7L, 9L)).thenThrow(notFound());
        when(resumes.setInterviewDate(7L, 9L, Map.of("interviewDate", "2026-12-01"))).thenThrow(notFound());
        org.mockito.Mockito.doThrow(notFound()).when(resumes).delete(7L, 9L);
        mvc.perform(as(get("/api/resumes/9"))).andExpect(status().isNotFound()).andExpect(jsonPath("$.type").value("not-found"));
        mvc.perform(as(patch("/api/resumes/9")).contentType(MediaType.APPLICATION_JSON).content("{\"interviewDate\":\"2026-12-01\"}"))
                .andExpect(status().isNotFound());
        mvc.perform(as(delete("/api/resumes/9"))).andExpect(status().isNotFound());
    }

    @Test
    void openSessionBlocksDeleteWithItsId() throws Exception {
        org.mockito.Mockito.doThrow(new ApiException(HttpStatus.CONFLICT, "session-open", "x", Map.of("sessionId", 5L)))
                .when(resumes).delete(any(Long.class), any(Long.class));
        mvc.perform(as(delete("/api/resumes/3"))).andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("session-open")).andExpect(jsonPath("$.sessionId").value(5));
    }

    @Test
    void needLogin() throws Exception {
        mvc.perform(get("/api/resumes")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/resumes/3")).andExpect(status().isUnauthorized());
    }
}
