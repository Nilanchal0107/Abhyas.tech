package tech.abhyas.trainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.Clock;
import org.mockito.Mockito;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.MeController;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.auth.UserRepository;
import tech.abhyas.trainer.common.ProblemHandler;

/** No database needed: only the controller and the Problem Details handler. */
class ProblemDetailsTest {
    @RestController
    static class Boom {
        @GetMapping("/api/boom")
        void boom() {
            throw new IllegalStateException("secret resume text");
        }
    }

    private final MockMvc mvc = MockMvcBuilders
            .standaloneSetup(new MeController(Mockito.mock(UserRepository.class)), new Boom())
            .addMappedInterceptors(new String[] {"/api/me"}, new AuthInterceptor(new TokenService("x".repeat(32), Clock.systemUTC())))
            .setControllerAdvice(new ProblemHandler()).build();

    @Test
    void notSignedInIsUnauthorizedProblem() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("unauthorized"))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void unknownRouteIsNotFoundProblem() throws Exception {
        mvc.perform(get("/api/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not-found"));
    }

    @Test
    void wrongMethodKeepsItsClientErrorStatus() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/me"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void unexpectedErrorLogsNoMessageContent() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(ProblemHandler.class);
        ListAppender<ILoggingEvent> logs = new ListAppender<>();
        logs.start();
        logger.addAppender(logs);
        try {
            mvc.perform(get("/api/boom"));
        } finally {
            logger.detachAppender(logs);
        }
        org.assertj.core.api.Assertions.assertThat(logs.list).isNotEmpty()
                .noneMatch(e -> e.getFormattedMessage().contains("secret") || e.getThrowableProxy() != null);
    }

    @Test
    void unexpectedErrorIsServerErrorWithoutLeakingTheMessage() throws Exception {
        mvc.perform(get("/api/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("server-error"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("secret"))));
    }
}
