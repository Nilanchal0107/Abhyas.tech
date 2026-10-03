package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.MappedInterceptor;
import tech.abhyas.trainer.auth.AppUser;
import tech.abhyas.trainer.auth.AuthController;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.auth.LoginThrottle;
import tech.abhyas.trainer.auth.MeController;
import tech.abhyas.trainer.auth.TokenService;
import tech.abhyas.trainer.auth.UserRepository;
import tech.abhyas.trainer.common.ProblemHandler;

/** Contract and security tests for ticket 02. No database: the repository is an in-memory map. */
class AuthApiTest {
    static final String SECRET = "test-secret-test-secret-test-secret-123";
    static final Instant T0 = Instant.parse("2026-10-03T10:00:00Z");

    /** A clock the tests can move. */
    static class MovableClock extends Clock {
        Instant now = T0;
        public ZoneId getZone() { return ZoneOffset.UTC; }
        public Clock withZone(ZoneId z) { return this; }
        public Instant instant() { return now; }
    }

    MovableClock clock = new MovableClock();
    Map<String, AppUser> db = new HashMap<>(); // keyed by lower-case login ID, like the case-insensitive collation
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4); // low cost keeps the tests fast
    TokenService tokens;
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByLoginId(anyString())).thenAnswer(i -> db.containsKey(key(i.getArgument(0))));
        when(repo.findByLoginId(anyString())).thenAnswer(i -> Optional.ofNullable(db.get(key(i.getArgument(0)))));
        when(repo.findById(anyLong())).thenAnswer(i -> db.values().stream().filter(u -> u.getId().equals(i.getArgument(0))).findFirst());
        when(repo.save(any(AppUser.class))).thenAnswer(i -> {
            AppUser u = i.getArgument(0);
            ReflectionTestUtils.setField(u, "id", (long) db.size() + 1);
            ReflectionTestUtils.setField(u, "createdAt", T0);
            db.put(key(u.getLoginId()), u);
            return u;
        });
        doAnswer(i -> db.values().remove(i.getArgument(0))).when(repo).delete(any(AppUser.class));
        tokens = new TokenService(SECRET, clock);
        mvc = MockMvcBuilders.standaloneSetup(authController(repo), new MeController(repo, encoder, false))
                .addInterceptors(new MappedInterceptor(new String[] {"/api/**"}, AuthInterceptor.PUBLIC, new AuthInterceptor(tokens)))
                .setControllerAdvice(new ProblemHandler()).build();
    }

    private static String key(Object loginId) { return ((String) loginId).toLowerCase(Locale.ROOT); }

    private AuthController authController(UserRepository repo) {
        return new AuthController(repo, encoder, tokens, new LoginThrottle(clock), false);
    }

    private MvcResult send(String path, String json) throws Exception {
        return mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
    }

    private int statusOf(String path, String json) throws Exception {
        return send(path, json).getResponse().getStatus();
    }

    private static String creds(String id, String pw) { return "{\"loginId\":\"" + id + "\",\"password\":\"" + pw + "\"}"; }

    private Cookie signUp(String id) throws Exception {
        MvcResult r = send("/api/auth/signup", creds(id, "password1"));
        assertThat(r.getResponse().getStatus()).isEqualTo(201);
        return r.getResponse().getCookie("rdt_session");
    }

    // ---- signup ----

    @Test
    void signupReturnsUserAndSetsHardenedCookie() throws Exception {
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(creds("paras24", "password1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.loginId").value("paras24"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(result -> {
                    String set = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
                    assertThat(set).startsWith("rdt_session=").contains("HttpOnly", "SameSite=Strict", "Max-Age=28800", "Path=/");
                });
    }

    @Test
    void signupStoresOnlyABcryptHash() throws Exception {
        send("/api/auth/signup", creds("paras24", "password1"));
        String stored = db.get("paras24").getPasswordHash();
        assertThat(stored).startsWith("$2").doesNotContain("password1");
        assertThat(encoder.matches("password1", stored)).isTrue();
    }

    @Test
    void signupRejectsBadFieldsWithInlineErrors() throws Exception {
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(creds("a b", "short")))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("validation"))
                .andExpect(jsonPath("$.errors[?(@.field=='loginId')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field=='password')]").isNotEmpty());
        assertThat(db).isEmpty();
    }

    @Test
    void signupLimits() throws Exception {
        assertThat(statusOf("/api/auth/signup", creds("abc", "12345678"))).isEqualTo(201);
        assertThat(statusOf("/api/auth/signup", creds("ab", "12345678"))).isEqualTo(400);
        assertThat(statusOf("/api/auth/signup", creds("x".repeat(50), "p".repeat(72)))).isEqualTo(201);
        assertThat(statusOf("/api/auth/signup", creds("y".repeat(51), "12345678"))).isEqualTo(400);
        assertThat(statusOf("/api/auth/signup", creds("zzz", "p".repeat(73)))).isEqualTo(400);
        // 40 characters but 80 bytes: BCrypt would silently cut it, so it is refused
        assertThat(statusOf("/api/auth/signup", creds("zzz", "é".repeat(40)))).isEqualTo(400);
    }

    @Test
    void signupMissingFieldsIsValidationNotServerError() throws Exception {
        assertThat(statusOf("/api/auth/signup", "{}")).isEqualTo(400);
    }

    @Test
    void signupTakenIgnoresLetterCase() throws Exception {
        signUp("Paras24");
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(creds("PARAS24", "password1")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("login-taken"))
                .andExpect(jsonPath("$.detail").value("That user ID is already used."));
    }

    @Test
    void signupRaceOnTheUniqueIndexIsLoginTakenToo() throws Exception {
        UserRepository repo = mock(UserRepository.class);
        when(repo.save(any())).thenThrow(new DataIntegrityViolationException("dup"));
        MockMvc m = MockMvcBuilders.standaloneSetup(authController(repo)).setControllerAdvice(new ProblemHandler()).build();
        m.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(creds("racer", "password1")))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("login-taken"));
    }

    // ---- login ----

    @Test
    void loginSucceedsWithTheSameShapeAsSignup() throws Exception {
        signUp("paras24");
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(creds("PARAS24", "password1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.loginId").value("paras24"))
                .andExpect(result -> assertThat(result.getResponse().getCookie("rdt_session")).isNotNull());
    }

    @Test
    void unknownUserAndWrongPasswordLookIdentical() throws Exception {
        signUp("paras24");
        String unknown = send("/api/auth/login", creds("nobody", "password1")).getResponse().getContentAsString();
        MvcResult wrong = send("/api/auth/login", creds("paras24", "wrongpass1"));
        assertThat(wrong.getResponse().getStatus()).isEqualTo(401);
        assertThat(wrong.getResponse().getContentAsString()).isEqualTo(unknown)
                .contains("\"type\":\"unauthorized\"", "That user ID or password is not right.");
        assertThat(wrong.getResponse().getCookie("rdt_session")).isNull();
    }

    @Test
    void fiveFailuresBlockForFiveMinutesEvenWithTheRightPassword() throws Exception {
        signUp("paras24");
        for (int i = 0; i < 5; i++) {
            assertThat(statusOf("/api/auth/login", creds("paras24", "wrongpass1"))).isEqualTo(401);
        }
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(creds("Paras24", "password1")))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.type").value("throttled"))
                .andExpect(jsonPath("$.retryAfterSeconds").value(300));

        clock.now = T0.plusSeconds(299);
        assertThat(statusOf("/api/auth/login", creds("paras24", "password1"))).isEqualTo(429);
        clock.now = T0.plusSeconds(301);
        assertThat(statusOf("/api/auth/login", creds("paras24", "password1"))).isEqualTo(200);
    }

    @Test
    void unknownIdsAreThrottledTooSoTheBlockDoesNotRevealWhoExists() throws Exception {
        for (int i = 0; i < 5; i++) send("/api/auth/login", creds("ghost", "wrongpass1"));
        assertThat(statusOf("/api/auth/login", creds("ghost", "wrongpass1"))).isEqualTo(429);
    }

    @Test
    void aSuccessfulLoginResetsTheFailureCount() throws Exception {
        signUp("paras24");
        for (int i = 0; i < 4; i++) send("/api/auth/login", creds("paras24", "wrongpass1"));
        assertThat(statusOf("/api/auth/login", creds("paras24", "password1"))).isEqualTo(200);
        for (int i = 0; i < 4; i++) send("/api/auth/login", creds("paras24", "wrongpass1"));
        assertThat(statusOf("/api/auth/login", creds("paras24", "wrongpass1"))).isEqualTo(401);
    }

    // ---- logout ----

    @Test
    void logoutClearsTheCookieAndCanBeRepeatedWithoutOne() throws Exception {
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isNoContent())
                    .andExpect(result -> assertThat(result.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                            .startsWith("rdt_session=;").contains("Max-Age=0", "HttpOnly", "SameSite=Strict"));
        }
    }

    // ---- /me and the cookie check ----

    @Test
    void meWithoutACookieIsUnauthorized() throws Exception {
        mvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("unauthorized"));
    }

    @Test
    void meWithTheSignupCookieReturnsTheUser() throws Exception {
        Cookie c = signUp("paras24");
        mvc.perform(get("/api/me").cookie(c))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.loginId").value("paras24"))
                .andExpect(jsonPath("$.createdAt").value("2026-10-03T10:00:00.000Z"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void tamperedExpiredAndForeignSignedCookiesAreUnauthorized() throws Exception {
        Cookie good = signUp("paras24");
        String token = good.getValue();
        String tampered = token.substring(0, token.length() - 3) + (token.endsWith("AAA") ? "BBB" : "AAA");
        String foreign = new TokenService("another-secret-another-secret-12345", clock).issue(1);
        for (String bad : new String[] {"garbage", tampered, foreign}) {
            mvc.perform(get("/api/me").cookie(new Cookie("rdt_session", bad))).andExpect(status().isUnauthorized());
        }
        clock.now = T0.plusSeconds(8 * 3600 + 1);
        mvc.perform(get("/api/me").cookie(good)).andExpect(status().isUnauthorized());
    }

    // ---- delete account ----

    private MvcResult deleteMe(Cookie c, String json) throws Exception {
        return mvc.perform(delete("/api/me").cookie(c).contentType(MediaType.APPLICATION_JSON).content(json)).andReturn();
    }

    @Test
    void deleteWithWrongPasswordIs400WrongPasswordNever401AndDeletesNothing() throws Exception {
        Cookie c = signUp("paras24");
        for (String body : new String[] {"{\"password\":\"nope-nope\"}", "{}"}) {
            MvcResult r = deleteMe(c, body);
            assertThat(r.getResponse().getStatus()).isEqualTo(400);
            assertThat(r.getResponse().getContentAsString()).contains("\"type\":\"wrong-password\"");
            assertThat(r.getResponse().getCookie("rdt_session")).isNull();
        }
        assertThat(db).containsKey("paras24");
    }

    @Test
    void deleteWithRightPasswordRemovesTheUserAndClearsTheCookie() throws Exception {
        Cookie c = signUp("paras24");
        MvcResult r = deleteMe(c, "{\"password\":\"password1\"}");
        assertThat(r.getResponse().getStatus()).isEqualTo(204);
        assertThat(r.getResponse().getHeader(HttpHeaders.SET_COOKIE)).startsWith("rdt_session=;").contains("Max-Age=0");
        assertThat(db).isEmpty();
        mvc.perform(get("/api/me").cookie(c)).andExpect(status().isUnauthorized());
    }

    @Test
    void deleteWithoutACookieIsUnauthorized() throws Exception {
        mvc.perform(delete("/api/me").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aValidTokenForADeletedUserIsUnauthorized() throws Exception {
        Cookie c = signUp("paras24");
        db.clear();
        mvc.perform(get("/api/me").cookie(c)).andExpect(status().isUnauthorized());
    }
}
