package tech.abhyas.trainer.story;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    private final StoryService stories;

    public StoryController(StoryService stories) { this.stories = stories; }

    @GetMapping
    Map<String, Object> list(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestParam(required = false) String tag) {
        return stories.list(userId, tag);
    }

    @GetMapping("/{id}")
    Map<String, Object> get(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        return Map.of("story", stories.get(userId, id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> create(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestBody JsonNode body) {
        return Map.of("story", stories.create(userId, body));
    }

    @PutMapping("/{id}")
    Map<String, Object> update(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @RequestBody JsonNode body) {
        return Map.of("story", stories.update(userId, id, body));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) { stories.delete(userId, id); }
}
