package tech.abhyas.trainer.resume;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Save a reviewed resume and its resume points in one transaction (FR-6, FR-7). */
@Service
public class ResumeService {
    static final int MAX_BULLETS = 50, MAX_POINTS = 200, MAX_TEXT = 500, MAX_SKILLS = 100;
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final NamedParameterJdbcTemplate jdbc;

    public ResumeService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public Map<String, Object> save(long userId, String fileName, Long roleId, String interviewDate, String parseSource, JsonNode parsed) {
        List<Map<String, String>> errors = new ArrayList<>();
        fileName = fileName == null ? "" : fileName.strip();
        if (fileName.isEmpty() || fileName.length() > 255) errors.add(err("fileName", "Use 1 to 255 characters."));
        if (roleId == null) errors.add(err("targetRoleId", "Choose a target role to continue."));
        if (!Set.of("openresume", "ai_fallback").contains(parseSource)) errors.add(err("parseSource", "Use openresume or ai_fallback."));
        LocalDate date = null;
        if (interviewDate != null) {
            try { date = LocalDate.parse(interviewDate); } catch (DateTimeParseException e) { errors.add(err("interviewDate", "Use a date like 2026-11-20.")); }
        }
        if (parsed == null || !parsed.isObject()) errors.add(err("parsed", "The resume is missing."));
        if (!errors.isEmpty()) throw invalid(errors);

        // phone and email are kept as typed: the Review form shows them, and shape() leaves them null for the AI path
        Map<String, Object> resume = ResumeParser.shape(parsed);
        @SuppressWarnings("unchecked") Map<String, Object> profile = (Map<String, Object>) resume.get("profile");
        profile.put("email", ResumeParser.text(parsed.path("profile").path("email")));
        profile.put("phone", ResumeParser.text(parsed.path("profile").path("phone")));
        List<Point> points = points(resume, errors);
        if (!errors.isEmpty()) throw invalid(errors);

        List<Map<String, Object>> role = jdbc.queryForList(
                "SELECT id, name FROM role WHERE id = :r AND (owner_user_id IS NULL OR owner_user_id = :me)",
                Map.of("r", roleId, "me", userId));
        if (role.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that role.");

        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.update("""
                INSERT INTO resume (user_id, file_name, target_role_id, parsed_json, parse_source, interview_date)
                VALUES (:me, :file, :role, :json, :src, :date)""",
                new MapSqlParameterSource("me", userId).addValue("file", fileName).addValue("role", roleId)
                        .addValue("json", JSON.writeValueAsString(resume)).addValue("src", parseSource).addValue("date", date), keys);
        long id = keys.getKey().longValue();
        int position = 1;
        for (Point p : points) {
            // the name column is case-insensitive; no skill row is ever added from a resume
            jdbc.update("""
                    INSERT INTO resume_point (resume_id, kind, source_ref, text, position, skill_id)
                    VALUES (:r, :kind, :ref, :text, :pos,
                            CASE WHEN :kind = 'skill' THEN (SELECT MIN(id) FROM skill WHERE name = :text) END)""",
                    Map.of("r", id, "kind", p.kind, "ref", p.ref, "text", p.text, "pos", position++));
        }
        return summaries(userId, id).get(0);
    }

    /** Most recently used first, never-used ones by creation time (api-contracts 5, GET /resumes). */
    public List<Map<String, Object>> list(long userId) { return summaries(userId, null); }

    public Map<String, Object> get(long userId, long id) {
        Map<String, Object> out = one(userId, id);
        out.put("parsed", JSON.readTree(jdbc.queryForObject("SELECT parsed_json FROM resume WHERE id = :r", Map.of("r", id), String.class)));
        out.put("points", jdbc.query("SELECT id, kind, text, position, skill_id FROM resume_point WHERE resume_id = :r ORDER BY position",
                Map.of("r", id), (rs, i) -> {
                    Map<String, Object> p = new LinkedHashMap<>();
                    p.put("id", rs.getLong("id"));
                    p.put("kind", rs.getString("kind"));
                    p.put("text", rs.getString("text"));
                    p.put("position", rs.getInt("position"));
                    p.put("skillId", rs.getObject("skill_id") == null ? null : rs.getLong("skill_id"));
                    return p;
                }));
        return out;
    }

    /** The interview date is the only editable field of a saved resume. */
    public Map<String, Object> setInterviewDate(long userId, long id, Map<String, Object> body) {
        one(userId, id);
        if (body == null || !body.containsKey("interviewDate") || body.size() != 1) throw invalid(List.of(err("interviewDate", "Only the interview date can be changed.")));
        LocalDate date = null;
        try {
            if (body.get("interviewDate") != null) date = LocalDate.parse(String.valueOf(body.get("interviewDate")));
        } catch (DateTimeParseException e) { throw invalid(List.of(err("interviewDate", "Use a date like 2026-11-20."))); }
        jdbc.update("UPDATE resume SET interview_date = :d WHERE id = :r AND user_id = :me",
                new MapSqlParameterSource("d", date).addValue("r", id).addValue("me", userId));
        return one(userId, id);
    }

    /** Points, key facts, gap result, questions, sessions and attempts go with it by ON DELETE CASCADE. */
    @Transactional
    public void delete(long userId, long id) {
        one(userId, id);
        List<Long> open = jdbc.queryForList("SELECT id FROM practice_session WHERE resume_id = :r AND status = 'in_progress'", Map.of("r", id), Long.class);
        if (!open.isEmpty()) throw new ApiException(HttpStatus.CONFLICT, "session-open", "Finish or leave your unfinished practice session first.", Map.of("sessionId", open.get(0)));
        jdbc.update("DELETE FROM resume WHERE id = :r AND user_id = :me", Map.of("r", id, "me", userId));
    }

    private Map<String, Object> one(long userId, long id) {
        List<Map<String, Object>> found = summaries(userId, id);
        if (found.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that resume.");
        return found.get(0);
    }

    /** ResumeSummary rows (api-contracts 2.2) of one user, optionally one resume. */
    private List<Map<String, Object>> summaries(long userId, Long id) {
        return jdbc.query("""
                SELECT r.id, r.file_name, r.target_role_id, ro.name AS role_name, r.interview_date, r.parse_source,
                       (SELECT COUNT(*) FROM resume_point p WHERE p.resume_id = r.id) AS point_count,
                       (SELECT COUNT(*) FROM question q WHERE q.resume_id = r.id) AS question_count,
                       (SELECT COUNT(*) FROM question q WHERE q.resume_id = r.id AND q.mastered) AS mastered_count,
                       CONCAT(DATE_FORMAT(r.created_at, '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(r.created_at) DIV 1000, 3, '0'), 'Z') AS created_at,
                       CONCAT(DATE_FORMAT(r.last_used_at, '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(r.last_used_at) DIV 1000, 3, '0'), 'Z') AS last_used_at
                FROM resume r JOIN role ro ON ro.id = r.target_role_id
                WHERE r.user_id = :me AND (:id IS NULL OR r.id = :id)
                ORDER BY r.last_used_at IS NULL, r.last_used_at DESC, r.created_at DESC, r.id DESC""",
                new MapSqlParameterSource("me", userId).addValue("id", id), (rs, i) -> {
                    Map<String, Object> out = new LinkedHashMap<>();
                    out.put("id", rs.getLong("id"));
                    out.put("fileName", rs.getString("file_name"));
                    out.put("targetRole", Map.of("id", rs.getLong("target_role_id"), "name", rs.getString("role_name")));
                    out.put("interviewDate", rs.getString("interview_date"));
                    out.put("parseSource", rs.getString("parse_source"));
                    out.put("pointCount", rs.getInt("point_count"));
                    out.put("questionCount", rs.getInt("question_count"));
                    out.put("masteredCount", rs.getInt("mastered_count"));
                    out.put("createdAt", rs.getString("created_at"));
                    out.put("lastUsedAt", rs.getString("last_used_at"));
                    return out;
                });
    }

    private record Point(String kind, String ref, String text) {}

    /** Experience bullets, then project bullets, then skills (api-contracts 2.1). Collects limit errors (1.2). */
    @SuppressWarnings("unchecked")
    private static List<Point> points(Map<String, Object> resume, List<Map<String, String>> errors) {
        List<Point> out = new ArrayList<>();
        for (String[] s : new String[][] {{"experience", "experience_bullet"}, {"projects", "project_bullet"}}) {
            List<Map<String, Object>> entries = (List<Map<String, Object>>) resume.get(s[0]);
            for (int i = 0; i < entries.size(); i++) {
                List<String> bullets = (List<String>) entries.get(i).get("bullets");
                String field = s[0] + "[" + i + "].bullets";
                if (bullets.size() > MAX_BULLETS) errors.add(err(field, "Use at most 50 bullets in one entry."));
                for (int b = 0; b < bullets.size(); b++) {
                    if (bullets.get(b).length() > MAX_TEXT) errors.add(err(field + "[" + b + "]", "Use at most 500 characters."));
                    out.add(new Point(s[1], field + "[" + b + "]", bullets.get(b)));
                }
            }
        }
        List<String> skills = (List<String>) resume.get("skills");
        if (skills.size() > MAX_SKILLS) errors.add(err("skills", "Use at most 100 skills."));
        for (int i = 0; i < skills.size(); i++) {
            if (skills.get(i).length() > MAX_TEXT) errors.add(err("skills[" + i + "]", "Use at most 500 characters."));
            out.add(new Point("skill", "skills[" + i + "]", skills.get(i)));
        }
        if (out.size() > MAX_POINTS) errors.add(err("parsed", "Use at most 200 resume points in total."));
        return out;
    }

    private static Map<String, String> err(String field, String message) { return Map.of("field", field, "message", message); }

    private static ApiException invalid(List<Map<String, String>> errors) {
        return new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.", Map.of("errors", errors));
    }
}
