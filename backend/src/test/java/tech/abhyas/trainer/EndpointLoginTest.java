package tech.abhyas.trainer;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AuthController;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.MeController;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.resume.ResumeController;
import tech.abhyas.trainer.role.RoleController;
import tech.abhyas.trainer.session.SessionController;
import tech.abhyas.trainer.story.StoryController;

/**
 * Ticket 28: every endpoint in api-contracts.md Section 9 answers 401 without a cookie, using the real controllers' mappings.
 * Add a line here when an endpoint is added. Login, signup and logout are public by design.
 */
class EndpointLoginTest {
    record Call(HttpMethod method, String path) {}

    static final List<Call> PROTECTED = Stream.of(
            "GET /api/me", "DELETE /api/me",
            "GET /api/roles", "POST /api/roles/draft-skills", "POST /api/roles",
            "POST /api/resumes/parse-fallback", "POST /api/resumes", "GET /api/resumes", "GET /api/resumes/1", "PATCH /api/resumes/1", "DELETE /api/resumes/1",
            "GET /api/resumes/1/role-skills-check",
            "POST /api/sessions", "GET /api/sessions", "GET /api/sessions/1", "PUT /api/sessions/1/attempts/1", "POST /api/sessions/1/attempts/1/transcribe",
            "POST /api/sessions/1/submit", "POST /api/sessions/1/abandon", "GET /api/sessions/1/export",
            "GET /api/stories", "GET /api/stories/1", "POST /api/stories", "PUT /api/stories/1", "DELETE /api/stories/1")
            .map(s -> new Call(HttpMethod.valueOf(s.split(" ")[0]), s.split(" ")[1])).toList();

    @Test
    void everyProtectedEndpointNeedsTheLoginCookie() throws Exception {
        TokenService tokens = new TokenService("test-secret-test-secret-test-secret-123", Clock.systemUTC());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(mock(AuthController.class), mock(MeController.class), mock(RoleController.class),
                        mock(ResumeController.class), mock(SessionController.class), mock(StoryController.class))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
        for (Call c : PROTECTED) {
            mvc.perform(request(c.method(), c.path()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.type").value("unauthorized"));
        }
    }
}
