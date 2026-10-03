package tech.abhyas.trainer.story;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

/** Contract tests for /stories (ticket 25). The queries run on MySQL in StoryDbTest. */
class StoryApiTest {
    StoryService stories = mock(StoryService.class);
    TokenService tokens = new TokenService("test-secret-test-secret-test-secret-123", Clock.systemUTC());
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new StoryController(stories))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder r) { return r.cookie(new Cookie("rdt_session", tokens.issue(7L))); }

    @Test
    void listFiltersByTag() throws Exception {
        when(stories.list(7L, "teamwork")).thenReturn(Map.of("stories", List.of(Map.of("id", 9)), "tags", List.of("teamwork")));
        mvc.perform(as(get("/api/stories?tag=teamwork"))).andExpect(status().isOk())
                .andExpect(jsonPath("$.stories[0].id").value(9)).andExpect(jsonPath("$.tags[0]").value("teamwork"));
    }

    @Test
    void createReturns201AndUpdateAndGetReturnTheStory() throws Exception {
        when(stories.create(anyLong(), any())).thenReturn(Map.of("id", 9, "title", "T"));
        when(stories.update(anyLong(), anyLong(), any())).thenReturn(Map.of("id", 9, "title", "T2"));
        when(stories.get(7L, 9L)).thenReturn(Map.of("id", 9, "title", "T"));
        mvc.perform(as(post("/api/stories")).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"T\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.story.id").value(9));
        mvc.perform(as(put("/api/stories/9")).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"T2\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.story.title").value("T2"));
        mvc.perform(as(get("/api/stories/9"))).andExpect(status().isOk()).andExpect(jsonPath("$.story.title").value("T"));
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(as(delete("/api/stories/9"))).andExpect(status().isNoContent());
    }

    @Test
    void anotherUsersStoryAndBadBodiesMapToProblemDetails() throws Exception {
        when(stories.get(7L, 5L)).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        doThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x")).when(stories).delete(7L, 5L);
        when(stories.update(eq(7L), eq(5L), any())).thenThrow(new ApiException(HttpStatus.NOT_FOUND, "not-found", "x"));
        mvc.perform(as(put("/api/stories/5")).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"t\"}")).andExpect(status().isNotFound());
        when(stories.create(anyLong(), any())).thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "validation", "x", Map.of("errors", List.of())));
        mvc.perform(as(get("/api/stories/5"))).andExpect(status().isNotFound()).andExpect(jsonPath("$.type").value("not-found"));
        mvc.perform(as(delete("/api/stories/5"))).andExpect(status().isNotFound());
        mvc.perform(as(post("/api/stories")).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    void needLogin() throws Exception {
        mvc.perform(get("/api/stories")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/stories").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/stories/1")).andExpect(status().isUnauthorized());
    }
}
