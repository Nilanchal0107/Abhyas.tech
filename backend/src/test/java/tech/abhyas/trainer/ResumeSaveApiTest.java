package tech.abhyas.trainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.Clock;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.resume.ResumeController;
import tech.abhyas.trainer.resume.ResumeService;

/** Contract tests for POST /resumes (ticket 09). The transaction itself runs on MySQL in ResumeSaveDbTest. */
class ResumeSaveApiTest {
    ResumeService resumes = mock(ResumeService.class);
    TokenService tokens = new TokenService(AuthApiTest.SECRET, Clock.systemUTC());
    MockMvc mvc;

    static final String BODY = """
            {"fileName":"Paras_Resume.pdf","targetRoleId":12,"interviewDate":"2026-11-20","parseSource":"openresume",
             "parsed":{"profile":{"name":"Asha"},"skills":["Java"]},"extra":1}""";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ResumeController(null, resumes))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder save(String body) {
        return post("/api/resumes").cookie(new Cookie("rdt_session", tokens.issue(7L))).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void returns201WithTheSummaryForTheSignedInUser() throws Exception {
        when(resumes.save(eq(7L), eq("Paras_Resume.pdf"), eq(12L), eq("2026-11-20"), eq("openresume"), any()))
                .thenReturn(Map.of("id", 7L, "pointCount", 38, "parseSource", "openresume"));
        mvc.perform(save(BODY)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.resume.id").value(7)).andExpect(jsonPath("$.resume.pointCount").value(38));
    }

    @Test
    void notFoundUsesTheContractCode() throws Exception {
        when(resumes.save(any(Long.class), any(), any(), any(), any(), any()))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that role."));
        mvc.perform(save(BODY)).andExpect(status().isNotFound()).andExpect(jsonPath("$.type").value("not-found"));
    }

    @Test
    void needsLogin() throws Exception {
        mvc.perform(post("/api/resumes").contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
    }
}
