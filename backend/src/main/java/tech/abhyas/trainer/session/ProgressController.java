package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.auth.AuthInterceptor;
import tech.abhyas.trainer.common.ApiException;

/**
 * Progress view (skill-bank ticket 14): marks per skill over the completed sessions of one resume, weakest first, and a short "study next"
 * list. Built from stored attempts, no AI. Only bank questions carry a skill, so project questions are not counted.
 */
@RestController
@RequestMapping("/api/resumes/{id}/progress")
public class ProgressController {
    private static final int STUDY_WEAK = 3, STUDY_GAP = 3;

    private final NamedParameterJdbcTemplate jdbc;

    public ProgressController(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping
    Map<String, Object> progress(@RequestAttribute(AuthInterceptor.USER_ID) long userId, @PathVariable long id) {
        List<Long> role = jdbc.queryForList("SELECT target_role_id FROM resume WHERE id = :r AND user_id = :me", Map.of("r", id, "me", userId), Long.class);
        if (role.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that resume.");

        Map<Long, Map<String, Object>> bySkill = new LinkedHashMap<>();
        jdbc.query("""
                SELECT b.skill_id, s.name, a.session_id,
                       CONCAT(DATE_FORMAT(ps.completed_at, '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(ps.completed_at) DIV 1000, 3, '0'), 'Z') AS at,
                       SUM(a.score) AS marks, SUM(a.max_marks) AS max
                FROM attempt a JOIN practice_session ps ON ps.id = a.session_id
                JOIN question q ON q.id = a.question_id JOIN bank_question b ON b.id = q.bank_question_id JOIN skill s ON s.id = b.skill_id
                WHERE ps.resume_id = :r AND ps.user_id = :me AND ps.status = 'completed' AND a.score IS NOT NULL
                GROUP BY b.skill_id, s.name, a.session_id, ps.completed_at ORDER BY ps.completed_at, a.session_id""",
                Map.of("r", id, "me", userId), rs -> {
            String name = rs.getString("name");
            Map<String, Object> skill = bySkill.computeIfAbsent(rs.getLong("skill_id"), k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("skillId", k); m.put("skill", name);
                m.put("marks", 0.0); m.put("maxMarks", 0.0); m.put("bySession", new ArrayList<Map<String, Object>>());
                return m;
            });
            double marks = rs.getDouble("marks"), max = rs.getDouble("max");
            skill.put("marks", (double) skill.get("marks") + marks);
            skill.put("maxMarks", (double) skill.get("maxMarks") + max);
            @SuppressWarnings("unchecked") List<Map<String, Object>> list = (List<Map<String, Object>>) skill.get("bySession");
            list.add(Map.of("sessionId", rs.getLong("session_id"), "completedAt", rs.getString("at"), "percent", percent(marks, max)));
        });
        List<Map<String, Object>> skills = new ArrayList<>(bySkill.values());
        for (Map<String, Object> s : skills) s.put("percent", percent((double) s.get("marks"), (double) s.get("maxMarks")));
        skills.sort((x, y) -> Integer.compare((int) x.get("percent"), (int) y.get("percent")));

        // study next: the lowest skills still short of full marks, then role skills the resume does not show (never asked from the resume)
        List<Map<String, Object>> next = new ArrayList<>();
        skills.stream().filter(s -> (int) s.get("percent") < 100).limit(STUDY_WEAK)
                .forEach(s -> next.add(Map.of("skillId", s.get("skillId"), "skill", s.get("skill"), "reason", "low-marks")));
        jdbc.query("""
                SELECT s.id, s.name FROM role_skill rs JOIN skill s ON s.id = rs.skill_id
                WHERE rs.role_id = :role AND NOT EXISTS (SELECT 1 FROM resume_point p WHERE p.resume_id = :r AND p.skill_id = s.id) ORDER BY s.name""",
                Map.of("role", role.get(0), "r", id), rs -> {
            long skillId = rs.getLong("id");
            if (next.stream().noneMatch(n -> n.get("skillId").equals(skillId)) && next.stream().filter(n -> "gap".equals(n.get("reason"))).count() < STUDY_GAP)
                next.add(Map.of("skillId", skillId, "skill", rs.getString("name"), "reason", "gap"));
        });

        Integer sessions = jdbc.queryForObject("SELECT COUNT(*) FROM practice_session WHERE resume_id = :r AND user_id = :me AND status = 'completed'", Map.of("r", id, "me", userId), Integer.class);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("resumeId", id);
        out.put("sessionCount", sessions);
        out.put("skills", skills);
        out.put("studyNext", next);
        return out;
    }

    private static int percent(double marks, double max) { return max == 0 ? 0 : (int) Math.round(100 * marks / max); }
}
