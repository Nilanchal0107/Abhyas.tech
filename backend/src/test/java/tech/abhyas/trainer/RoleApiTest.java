package tech.abhyas.trainer;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.role.RoleController;
import tech.abhyas.trainer.role.RoleSearch;

/** Contract tests for GET /roles (ticket 05). The query itself runs against MySQL in RoleSearchDbTest. */
class RoleApiTest {
    RoleSearch search = mock(RoleSearch.class);
    TokenService tokens = new TokenService(AuthApiTest.SECRET, Clock.systemUTC());
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RoleController(search))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private Cookie cookie() { return new Cookie("rdt_session", tokens.issue(7L)); }

    @Test
    void returnsTheContractShape() throws Exception {
        when(search.search(eq(7L), eq("sde"))).thenReturn(List.of(Map.of("id", 4L, "name", "Software Engineer",
                "category", "Software Development", "matchedOn", "alias", "matchedAlias", "SDE", "isOwn", false)));
        mvc.perform(get("/api/roles").param("q", " sde ").cookie(cookie()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0].id").value(4))
                .andExpect(jsonPath("$.roles[0].matchedOn").value("alias"))
                .andExpect(jsonPath("$.roles[0].matchedAlias").value("SDE"))
                .andExpect(jsonPath("$.roles[0].isOwn").value(false));
    }

    @Test
    void searchesAsTheSignedInUser() throws Exception {
        when(search.search(anyLong(), anyString())).thenReturn(List.of());
        mvc.perform(get("/api/roles").param("q", "x").cookie(cookie()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.roles").isEmpty());
        verify(search).search(7L, "x");
    }

    @Test
    void missingBlankOrTooLongQueryIsValidation() throws Exception {
        for (String q : new String[] {null, "  ", "x".repeat(101)}) {
            var req = get("/api/roles").cookie(cookie());
            if (q != null) req.param("q", q);
            mvc.perform(req).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.type").value("validation"))
                    .andExpect(jsonPath("$.errors[0].field").value("q"));
        }
    }

    @Test
    void needsLogin() throws Exception {
        mvc.perform(get("/api/roles").param("q", "sde")).andExpect(status().isUnauthorized());
    }

    @Test
    void addingARoleIsGone() throws Exception { // removed 4 October 2026: the 11 starter roles are fixed
        mvc.perform(post("/api/roles").cookie(cookie()).contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Prompt engineer\"}"))
                .andExpect(status().isMethodNotAllowed());
    }
}
