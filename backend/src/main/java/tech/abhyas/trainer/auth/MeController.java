package tech.abhyas.trainer.auth;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.common.ApiException;

@RestController
@RequestMapping("/api")
public class MeController {
    // api-contracts 1: ISO-8601 UTC with milliseconds (Instant.toString drops them when they are zero)
    private static final DateTimeFormatter MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    private final UserRepository users;

    public MeController(UserRepository users) { this.users = users; }

    @GetMapping("/me")
    Map<String, Object> me(@RequestAttribute(AuthInterceptor.USER_ID) long userId) {
        // a valid token for a user that no longer exists is just "not signed in"
        AppUser u = users.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", "Please log in."));
        return Map.of("id", u.getId(), "loginId", u.getLoginId(), "createdAt", MILLIS.format(u.getCreatedAt()));
    }
}
