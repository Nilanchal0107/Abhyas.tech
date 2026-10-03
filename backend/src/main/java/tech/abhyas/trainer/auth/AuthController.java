package tech.abhyas.trainer.auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.common.ApiException;

/** Sign up, log in and log out (api-contracts 3). Never logs a login ID or password. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public static final String COOKIE = "rdt_session";
    private static final Pattern LOGIN_ID = Pattern.compile("[A-Za-z0-9_.-]{3,50}");
    // same message for an unknown user and a wrong password (api-contracts 1.1)
    private static final String BAD_CREDENTIALS = "That user ID or password is not right.";

    record Credentials(String loginId, String password) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final LoginThrottle throttle;
    private final boolean secureCookie;
    // checked when the user is unknown, so an unknown ID and a wrong password take equally long
    private final String dummyHash;

    public AuthController(UserRepository users, PasswordEncoder encoder, TokenService tokens, LoginThrottle throttle,
                   @Value("${app.cookie.secure:false}") boolean secureCookie) {
        this.users = users;
        this.encoder = encoder;
        this.tokens = tokens;
        this.throttle = throttle;
        this.secureCookie = secureCookie;
        this.dummyHash = encoder.encode("not-a-real-password");
    }

    @PostMapping("/signup")
    ResponseEntity<Map<String, Object>> signup(@RequestBody Credentials body) {
        String loginId = body.loginId() == null ? "" : body.loginId();
        String password = body.password() == null ? "" : body.password();
        validate(loginId, password);
        if (users.existsByLoginId(loginId)) throw taken();
        AppUser user;
        try {
            user = users.save(new AppUser(loginId, encoder.encode(password)));
        } catch (DataIntegrityViolationException e) { // two signups for one ID at the same moment
            throw taken();
        }
        return signedIn(HttpStatus.CREATED, user);
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> login(@RequestBody Credentials body) {
        String loginId = body.loginId() == null ? "" : body.loginId();
        String password = body.password() == null ? "" : body.password();
        long wait = throttle.retryAfterSeconds(loginId);
        if (wait > 0) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "throttled",
                    "Too many tries. Try again in " + ((wait + 59) / 60) + " minute(s).",
                    Map.of("retryAfterSeconds", wait));
        }
        AppUser user = users.findByLoginId(loginId).orElse(null);
        boolean ok = encoder.matches(password, user == null ? dummyHash : user.getPasswordHash()) && user != null;
        if (!ok) {
            throttle.failed(loginId);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", BAD_CREDENTIALS);
        }
        throttle.succeeded(loginId);
        return signedIn(HttpStatus.OK, user);
    }

    @PostMapping("/logout")
    ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString()).build();
    }

    private ResponseEntity<Map<String, Object>> signedIn(HttpStatus status, AppUser user) {
        String token = tokens.issue(user.getId());
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookie(token, TokenService.LIFETIME).toString())
                .body(Map.of("user", Map.of("id", user.getId(), "loginId", user.getLoginId())));
    }

    private ResponseCookie cookie(String value, Duration maxAge) { return cookie(value, maxAge, secureCookie); }

    static ResponseCookie cookie(String value, Duration maxAge, boolean secure) {
        return ResponseCookie.from(COOKIE, value).httpOnly(true).sameSite("Strict").path("/")
                .secure(secure).maxAge(maxAge).build();
    }

    private static ApiException taken() {
        return new ApiException(HttpStatus.CONFLICT, "login-taken", "That user ID is already used.");
    }

    private static void validate(String loginId, String password) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (!LOGIN_ID.matcher(loginId).matches()) {
            errors.add(Map.of("field", "loginId", "message", "Use 3 to 50 letters, digits, _ . or -."));
        }
        if (password.length() < 8) {
            errors.add(Map.of("field", "password", "message", "Use at least 8 characters."));
        } else if (password.length() > 72 || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            // BCrypt reads only 72 bytes, so a longer password would be silently cut
            errors.add(Map.of("field", "password", "message", "Use at most 72 characters."));
        }
        if (!errors.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.",
                    Map.of("errors", errors));
        }
    }
}
