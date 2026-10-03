package tech.abhyas.trainer.auth;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.common.ApiException;

@RestController
@RequestMapping("/api")
public class MeController {
    // api-contracts 1: ISO-8601 UTC with milliseconds (Instant.toString drops them when they are zero)
    private static final DateTimeFormatter MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    record DeleteRequest(String password) {}

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final boolean secureCookie;

    public MeController(UserRepository users, PasswordEncoder encoder,
                        @Value("${app.cookie.secure:false}") boolean secureCookie) {
        this.users = users;
        this.encoder = encoder;
        this.secureCookie = secureCookie;
    }

    @GetMapping("/me")
    Map<String, Object> me(@RequestAttribute(AuthInterceptor.USER_ID) long userId) {
        AppUser u = find(userId);
        return Map.of("id", u.getId(), "loginId", u.getLoginId(), "createdAt", MILLIS.format(u.getCreatedAt()));
    }

    /** Hard delete: the database cascades from app_user to all of the user's data (database-schema decision 9). */
    @DeleteMapping("/me")
    @Transactional
    ResponseEntity<Void> delete(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestBody DeleteRequest body) {
        AppUser u = find(userId);
        String password = body.password() == null ? "" : body.password();
        // 400, never 401: the page must not mistake a wrong password for an expired login
        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "wrong-password", "That password is not right.");
        }
        users.delete(u);
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, AuthController.cookie("", Duration.ZERO, secureCookie).toString()).build();
    }

    // a valid token for a user that no longer exists is just "not signed in"
    private AppUser find(long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", "Please log in."));
    }
}
