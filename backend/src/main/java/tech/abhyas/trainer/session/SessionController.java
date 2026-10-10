package tech.abhyas.trainer.session;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.resume.ProjectQuestionService;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService sessions;
    private final ProjectQuestionService projectQuestions;
    private final ExportService exports;

    public SessionController(SessionService sessions, ProjectQuestionService projectQuestions, ExportService exports) { this.sessions = sessions; this.projectQuestions = projectQuestions; this.exports = exports; }

    record Start(Long resumeId, String mode) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> start(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestBody Start b) {
        if (b == null || b.resumeId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.",
                    Map.of("errors", java.util.List.of(Map.of("field", "resumeId", "message", "Choose a resume to practice with."))));
        }
        projectQuestions.retryInBackground(userId); // resumes still without project questions are retried when a session starts (FR-8)
        return Map.of("session", sessions.start(userId, b.resumeId(), b.mode() == null ? "quiz" : b.mode()));
    }

    @GetMapping
    Map<String, Object> list(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @RequestParam(required = false) String status, @RequestParam(required = false) Integer limit) {
        return Map.of("sessions", sessions.list(userId, status, limit));
    }

    @GetMapping("/{id}")
    Map<String, Object> get(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        return Map.of("session", sessions.detail(userId, id));
    }

    @PutMapping("/{id}/attempts/{position}")
    Map<String, Object> answer(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @PathVariable int position, @RequestBody JsonNode body) {
        return Map.of("attempt", sessions.saveAnswer(userId, id, position, body));
    }

    /** Interview mode: the AI follow-up to an answered question (written once, then kept), and the answer to it. */
    @PostMapping("/{id}/attempts/{position}/follow-up")
    Map<String, Object> followUp(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @PathVariable int position) {
        return Map.of("attempt", sessions.followUp(userId, id, position));
    }

    @PutMapping("/{id}/attempts/{position}/follow-up")
    Map<String, Object> followUpAnswer(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @PathVariable int position, @RequestBody JsonNode body) {
        return Map.of("attempt", sessions.saveFollowUpAnswer(userId, id, position, body));
    }

    @PostMapping("/{id}/attempts/{position}/transcribe")
    Map<String, Object> transcribe(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @PathVariable int position,
                                   @RequestParam("audio") MultipartFile audio) throws java.io.IOException {
        return sessions.transcribe(userId, id, position, audio.getBytes(), audio.getContentType());
    }

    /** 202 when this call started grading, 200 when the session was already submitted (api-contracts 6). */
    @PostMapping("/{id}/submit")
    ResponseEntity<Map<String, Object>> submit(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        SessionService.Submitted r = sessions.submit(userId, id);
        return ResponseEntity.status(r.started() ? HttpStatus.ACCEPTED : HttpStatus.OK).body(Map.of("session", r.session()));
    }

    /** 202: the answer is back with the grading worker; poll the session as after submit (api-contracts 6). */
    @PostMapping("/{id}/attempts/{position}/regrade")
    @ResponseStatus(HttpStatus.ACCEPTED)
    Map<String, Object> regrade(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id, @PathVariable int position) {
        return Map.of("session", sessions.regrade(userId, id, position));
    }

    @PostMapping("/{id}/abandon")
    Map<String, Object> abandon(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        return Map.of("session", sessions.abandon(userId, id));
    }

    /** A download, not a preview: UTF-8 JSON with a filename (api-contracts 6). */
    @GetMapping("/{id}/export")
    ResponseEntity<Map<String, Object>> export(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        Map<String, Object> file = exports.export(userId, id);
        @SuppressWarnings("unchecked") String date = (String) ((Map<String, Object>) file.get("metadata")).get("sessionDate");
        return ResponseEntity.ok()
                .contentType(new org.springframework.http.MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8))
                .header("Content-Disposition", "attachment; filename=\"practice-session-" + id + "-" + date + ".json\"")
                .body(file);
    }
}
