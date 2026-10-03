package tech.abhyas.trainer.story;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;

/** STAR stories (FR-17, api-contracts 7). Every query is scoped to the signed-in user; another user's story is "not found". */
@Service
public class StoryService {
    static final int MAX_TAGS = 10, MAX_POINTS = 20, MAX_TAG_LENGTH = 40, MAX_TITLE = 200, MAX_TEXT = 5000;
    private static final List<String> BOXES = List.of("situation", "task", "action", "result", "lesson");

    private final NamedParameterJdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final Clock clock;

    public StoryService(NamedParameterJdbcTemplate jdbc, TransactionTemplate tx, Clock clock) { this.jdbc = jdbc; this.tx = tx; this.clock = clock; }

    /** A story as the user typed it, already checked against the limits in api-contracts 1.2. */
    record Input(String title, Map<String, String> boxes, List<String> tags, List<Long> pointIds, Long sourceAttemptId) {}

    /** Checks the body and returns it cleaned (trimmed text, blank boxes as null, duplicate tags and points dropped). */
    static Input parse(JsonNode b) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (b == null || !b.isObject()) throw invalid(List.of(err("title", "Give the story a title.")));
        String title = text(b, "title");
        if (title == null) errors.add(err("title", "Give the story a title."));
        else if (title.length() > MAX_TITLE) errors.add(err("title", "Use at most 200 characters."));
        Map<String, String> boxes = new LinkedHashMap<>();
        for (String box : BOXES) {
            String v = text(b, box);
            if (v != null && v.length() > MAX_TEXT) errors.add(err(box, "Use at most 5,000 characters."));
            boxes.put(box, v);
        }
        Set<String> tags = new LinkedHashSet<>();
        for (JsonNode t : b.path("tags")) {
            String v = t.isString() ? t.asString().trim() : "";
            if (v.isEmpty() || v.length() > MAX_TAG_LENGTH) { errors.add(err("tags", "Each tag needs 1 to 40 characters.")); break; }
            tags.add(v);
        }
        if (tags.size() > MAX_TAGS) errors.add(err("tags", "Use at most 10 tags."));
        Set<Long> points = new LinkedHashSet<>();
        for (JsonNode p : b.path("resumePointIds")) {
            if (!p.canConvertToLong()) { errors.add(err("resumePointIds", "Choose resume points from the list.")); break; }
            points.add(p.asLong());
        }
        if (points.size() > MAX_POINTS) errors.add(err("resumePointIds", "Link at most 20 resume points."));
        Long source = b.path("sourceAttemptId").canConvertToLong() ? b.path("sourceAttemptId").asLong() : null;
        if (!errors.isEmpty()) throw invalid(errors);
        return new Input(title, boxes, new ArrayList<>(tags), new ArrayList<>(points), source);
    }

    /** The trimmed string of a field, or null when it is missing, not a string or blank. */
    private static String text(JsonNode b, String key) {
        JsonNode n = b.path(key);
        if (!n.isString()) return null;
        String s = n.asString().trim();
        return s.isEmpty() ? null : s;
    }

    public Map<String, Object> create(long userId, JsonNode body) {
        Input in = parse(body);
        if (in.sourceAttemptId() != null && ownedAttempts(userId, in.sourceAttemptId()) == 0) throw notFound();
        checkPoints(userId, in.pointIds());
        LocalDateTime now = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));
        long id = tx.execute(s -> {
            MapSqlParameterSource p = params(in).addValue("me", userId).addValue("src", in.sourceAttemptId()).addValue("now", now);
            org.springframework.jdbc.support.GeneratedKeyHolder keys = new org.springframework.jdbc.support.GeneratedKeyHolder();
            jdbc.update("""
                    INSERT INTO story (user_id, title, situation, task, action, result, lesson, source_attempt_id, created_at, updated_at)
                    VALUES (:me, :title, :situation, :task, :action, :result, :lesson, :src, :now, :now)""", p, keys);
            long newId = keys.getKey().longValue();
            links(newId, in);
            return newId;
        });
        return get(userId, id);
    }

    public Map<String, Object> update(long userId, long id, JsonNode body) {
        Input in = parse(body);
        findOwned(userId, id);
        checkPoints(userId, in.pointIds());
        LocalDateTime now = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));
        tx.executeWithoutResult(s -> {
            jdbc.update("""
                    UPDATE story SET title = :title, situation = :situation, task = :task, action = :action, result = :result, lesson = :lesson, updated_at = :now
                    WHERE id = :id AND user_id = :me""", params(in).addValue("id", id).addValue("me", userId).addValue("now", now));
            jdbc.update("DELETE FROM story_tag WHERE story_id = :id", Map.of("id", id));
            jdbc.update("DELETE FROM story_resume_point WHERE story_id = :id", Map.of("id", id));
            links(id, in);
        });
        return get(userId, id);
    }

    public void delete(long userId, long id) {
        if (jdbc.update("DELETE FROM story WHERE id = :id AND user_id = :me", Map.of("id", id, "me", userId)) == 0) throw notFound();
    }

    public Map<String, Object> get(long userId, long id) {
        Map<String, Object> story = new LinkedHashMap<>(findOwned(userId, id));
        story.put("tags", jdbc.queryForList("SELECT tag FROM story_tag WHERE story_id = :id ORDER BY tag", Map.of("id", id), String.class));
        story.put("resumePoints", points(List.of(id)).getOrDefault(id, List.of()));
        return story;
    }

    /** The list for the Story bank: all stories (or those with one tag), newest first, and every tag the user has used. */
    public Map<String, Object> list(long userId, String tag) {
        List<Map<String, Object>> rows = jdbc.query("""
                SELECT s.id, s.title, s.situation, s.action, %s AS created_at, %s AS updated_at FROM story s
                WHERE s.user_id = :me AND (:tag IS NULL OR EXISTS (SELECT 1 FROM story_tag t WHERE t.story_id = s.id AND t.tag = :tag))
                ORDER BY s.created_at DESC, s.id DESC""".formatted(iso("s.created_at"), iso("s.updated_at")),
                new MapSqlParameterSource("me", userId).addValue("tag", tag == null || tag.isBlank() ? null : tag), (rs, i) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("title", rs.getString("title"));
                    m.put("preview", preview(rs.getString("situation"), rs.getString("action")));
                    m.put("createdAt", rs.getString("created_at"));
                    m.put("updatedAt", rs.getString("updated_at"));
                    return m;
                });
        Map<Long, List<String>> tags = new LinkedHashMap<>();
        jdbc.query("SELECT t.story_id, t.tag FROM story_tag t JOIN story s ON s.id = t.story_id WHERE s.user_id = :me ORDER BY t.tag", Map.of("me", userId),
                rs -> { tags.computeIfAbsent(rs.getLong("story_id"), k -> new ArrayList<>()).add(rs.getString("tag")); });
        for (Map<String, Object> m : rows) m.put("tags", tags.getOrDefault((Long) m.get("id"), List.of()));
        List<String> all = jdbc.queryForList("SELECT DISTINCT t.tag FROM story_tag t JOIN story s ON s.id = t.story_id WHERE s.user_id = :me ORDER BY t.tag", Map.of("me", userId), String.class);
        return Map.of("stories", rows, "tags", all);
    }

    /** First 140 characters of the first non-empty of situation, action (api-contracts 7). */
    static String preview(String situation, String action) {
        String s = situation != null && !situation.isBlank() ? situation : action;
        if (s == null || s.isBlank()) return null;
        return s.length() <= 140 ? s : s.substring(0, 140);
    }

    /** Every story of the user for the export: the boxes, tags and linked resume point texts, with no database IDs. */
    public List<Map<String, Object>> forExport(long userId) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Long id : jdbc.queryForList("SELECT id FROM story WHERE user_id = :me ORDER BY created_at, id", Map.of("me", userId), Long.class)) {
            Map<String, Object> s = get(userId, id);
            Map<String, Object> m = new LinkedHashMap<>();
            for (String k : List.of("title", "situation", "task", "action", "result", "lesson", "tags")) m.put(k, s.get(k));
            m.put("resumePoints", points(List.of(id)).getOrDefault(id, List.of()).stream().map(p -> p.get("text")).toList());
            out.add(m);
        }
        return out;
    }

    // ---- helpers ----

    private static String iso(String col) {
        return "CONCAT(DATE_FORMAT(" + col + ", '%Y-%m-%dT%H:%i:%s.'), LPAD(MICROSECOND(" + col + ") DIV 1000, 3, '0'), 'Z')";
    }

    private Map<String, Object> findOwned(long userId, long id) {
        List<Map<String, Object>> found = jdbc.query("""
                SELECT s.id, s.title, s.situation, s.task, s.action, s.result, s.lesson, s.source_attempt_id, %s AS created_at, %s AS updated_at
                FROM story s WHERE s.id = :id AND s.user_id = :me""".formatted(iso("s.created_at"), iso("s.updated_at")),
                Map.of("id", id, "me", userId), (rs, i) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("title", rs.getString("title"));
                    for (String k : List.of("situation", "task", "action", "result", "lesson")) m.put(k, rs.getString(k));
                    m.put("sourceAttemptId", rs.getObject("source_attempt_id") == null ? null : rs.getLong("source_attempt_id"));
                    m.put("createdAt", rs.getString("created_at"));
                    m.put("updatedAt", rs.getString("updated_at"));
                    return m;
                });
        if (found.isEmpty()) throw notFound();
        return found.get(0);
    }

    private Map<Long, List<Map<String, Object>>> points(List<Long> storyIds) {
        Map<Long, List<Map<String, Object>>> out = new LinkedHashMap<>();
        jdbc.query("""
                SELECT l.story_id, p.id, p.text, p.resume_id FROM story_resume_point l JOIN resume_point p ON p.id = l.resume_point_id
                WHERE l.story_id IN (:ids) ORDER BY p.resume_id, p.position""", Map.of("ids", storyIds),
                rs -> { out.computeIfAbsent(rs.getLong("story_id"), k -> new ArrayList<>())
                        .add(Map.of("id", rs.getLong("id"), "text", rs.getString("text"), "resumeId", rs.getLong("resume_id"))); });
        return out;
    }

    private static MapSqlParameterSource params(Input in) {
        MapSqlParameterSource p = new MapSqlParameterSource("title", in.title());
        in.boxes().forEach(p::addValue);
        return p;
    }

    private void links(long id, Input in) {
        for (String tag : in.tags()) jdbc.update("INSERT INTO story_tag (story_id, tag) VALUES (:id, :tag)", Map.of("id", id, "tag", tag));
        for (Long point : in.pointIds()) jdbc.update("INSERT INTO story_resume_point (story_id, resume_point_id) VALUES (:id, :p)", Map.of("id", id, "p", point));
    }

    /** Linked resume points must belong to the user, through their resume. */
    private void checkPoints(long userId, List<Long> pointIds) {
        if (pointIds.isEmpty()) return;
        Long owned = jdbc.queryForObject("SELECT COUNT(*) FROM resume_point p JOIN resume r ON r.id = p.resume_id WHERE r.user_id = :me AND p.id IN (:ids)",
                Map.of("me", userId, "ids", pointIds), Long.class);
        if (owned == null || owned != pointIds.size()) throw notFound();
    }

    /** The attempt belongs to the user through its session. */
    private long ownedAttempts(long userId, long attemptId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM attempt a JOIN practice_session s ON s.id = a.session_id WHERE a.id = :a AND s.user_id = :me",
                Map.of("a", attemptId, "me", userId), Long.class);
    }

    private static Map<String, String> err(String field, String message) { return Map.of("field", field, "message", message); }

    private static ApiException invalid(List<Map<String, String>> errors) {
        return new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.", Map.of("errors", errors));
    }

    private static ApiException notFound() { return new ApiException(HttpStatus.NOT_FOUND, "not-found", "We could not find that."); }
}
