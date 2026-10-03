package tech.abhyas.trainer.role;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    private final RoleService roles;

    public RoleController(RoleSearch search, RoleService roles) { this.search = search; this.roles = roles; }

    record Name(String name) {}
    record NewRole(String name, List<String> aliases, List<String> skills) {}

    @PostMapping("/draft-skills")
    Map<String, Object> draftSkills(@RequestBody Name body) {
        String name = body.name() == null ? "" : body.name().strip();
        if (name.length() < 2 || name.length() > 100) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.",
                    Map.of("errors", List.of(Map.of("field", "name", "message", "Use 2 to 100 characters."))));
        }
        return Map.of("skills", roles.draftSkills(name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> add(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestBody NewRole body) {
        return Map.of("role", roles.add(userId, body.name() == null ? "" : body.name(),
                body.aliases() == null ? List.of() : body.aliases(),
                body.skills() == null ? List.of() : body.skills()));
    }

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
