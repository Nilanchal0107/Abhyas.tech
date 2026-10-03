package tech.abhyas.trainer;

import static org.mockito.ArgumentMatchers.anyList;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.common.ProblemHandler;
import tech.abhyas.trainer.role.RoleController;
import tech.abhyas.trainer.role.RoleSearch;
import tech.abhyas.trainer.role.RoleService;

/** Contract tests for GET /roles (ticket 05). The query itself runs against MySQL in RoleSearchDbTest. */
class RoleApiTest {
    RoleSearch search = mock(RoleSearch.class);
    RoleService roles = mock(RoleService.class);
    TokenService tokens = new TokenService(AuthApiTest.SECRET, Clock.systemUTC());
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new RoleController(search, roles))
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

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder json(String path, String body) {
        return post(path).cookie(cookie()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void draftSkillsReturnsTheListAndEmptyIsStill200() throws Exception {
        when(roles.draftSkills("Prompt engineer")).thenReturn(List.of("Python")).thenReturn(List.of());
        mvc.perform(json("/api/roles/draft-skills", "{\"name\":\" Prompt engineer \"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.skills[0]").value("Python"));
        mvc.perform(json("/api/roles/draft-skills", "{\"name\":\"Prompt engineer\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.skills").isEmpty());
    }

    @Test
    void draftSkillsNeedsAName() throws Exception {
        mvc.perform(json("/api/roles/draft-skills", "{\"name\":\"x\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void addReturns201WithTheRole() throws Exception {
        when(roles.add(7L, "Prompt engineer", List.of("PE"), List.of("Python")))
                .thenReturn(Map.of("id", 61L, "name", "Prompt engineer", "isOwn", true));
        mvc.perform(json("/api/roles", "{\"name\":\"Prompt engineer\",\"aliases\":[\"PE\"],\"skills\":[\"Python\"],\"extra\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role.id").value(61))
                .andExpect(jsonPath("$.role.isOwn").value(true));
    }

    @Test
    void roleExistsCarriesTheExistingRole() throws Exception {
        when(roles.add(anyLong(), anyString(), anyList(), anyList()))
                .thenThrow(new ApiException(HttpStatus.CONFLICT, "role-exists", "A similar role exists.",
                        Map.of("existingRoleId", 3L, "existingRoleName", "Prompt Engineer")));
        mvc.perform(json("/api/roles", "{\"name\":\"Prompt engineer\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("role-exists"))
                .andExpect(jsonPath("$.existingRoleId").value(3))
                .andExpect(jsonPath("$.existingRoleName").value("Prompt Engineer"));
    }
}
