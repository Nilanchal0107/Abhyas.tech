package tech.abhyas.trainer.role;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.common.ApiException;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleSearch search;

    public RoleController(RoleSearch search) { this.search = search; }

    @GetMapping
    Map<String, Object> find(@RequestAttribute(AuthInterceptor.USER_ID) long userId,
                             @RequestParam(required = false) String q) {
        String term = q == null ? "" : q.trim();
        if (term.isEmpty() || term.length() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.",
                    Map.of("errors", List.of(Map.of("field", "q", "message", "Type 1 to 100 characters."))));
        }
        return Map.of("roles", search.search(userId, term));
    }
}
