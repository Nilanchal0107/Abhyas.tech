package tech.abhyas.trainer.resume;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tools.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeParser parser;
    private final ResumeService resumes;

    public ResumeController(ResumeParser parser, ResumeService resumes) { this.parser = parser; this.resumes = resumes; }

    record NewResume(String fileName, Long targetRoleId, String interviewDate, String parseSource, JsonNode parsed) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> save(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestBody NewResume b) {
        return Map.of("resume", resumes.save(userId, b.fileName(), b.targetRoleId(), b.interviewDate(), b.parseSource(), b.parsed()));
    }

    @GetMapping
    Map<String, Object> list(@RequestAttribute(AuthInterceptor.USER_ID) long userId) {
        return Map.of("resumes", resumes.list(userId));
    }

    @GetMapping("/{id}")
    Map<String, Object> get(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        return Map.of("resume", resumes.get(userId, id));
    }

    @PatchMapping("/{id}")
    Map<String, Object> patch(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @RequestBody Map<String, Object> b) {
        return Map.of("resume", resumes.setInterviewDate(userId, id, b));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        resumes.delete(userId, id);
    }

    @PostMapping("/parse-fallback")
    Map<String, Object> parseFallback(@RequestParam("file") MultipartFile file) throws IOException {
        return Map.of("resume", parser.parse(file.getBytes()));
    }
}
