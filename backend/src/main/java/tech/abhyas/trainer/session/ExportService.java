package tech.abhyas.trainer.session;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** FR-19: one completed, fully graded practice session as JSON for other AI tools. Built on request, never stored. */
@Service
public class ExportService {
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final NamedParameterJdbcTemplate jdbc;
    private final SessionService sessions;
    private final Clock clock;

    public ExportService(NamedParameterJdbcTemplate jdbc, SessionService sessions, Clock clock) {
        this.jdbc = jdbc; this.sessions = sessions; this.clock = clock;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> export(long userId, long id) {
        Map<String, Object> detail = sessions.detail(userId, id); // 404 for another user's session
        if (!"completed".equals(detail.get("status")) || !Boolean.TRUE.equals(detail.get("gradingComplete"))) {
            throw new ApiException(HttpStatus.CONFLICT, "session-not-completed", "Only a finished and fully marked practice session can be exported.");
        }
        long resumeId = (Long) ((Map<String, Object>) detail.get("resume")).get("id");
        JsonNode resume = JSON.readTree(jdbc.queryForObject("SELECT parsed_json FROM resume WHERE id = :r", Map.of("r", resumeId), String.class));
        Map<Integer, String> gradedBy = new HashMap<>();
        jdbc.query("SELECT position, graded_by FROM attempt WHERE session_id = :s", Map.of("s", id), rs -> { gradedBy.put(rs.getInt("position"), rs.getString("graded_by")); });
        return Export.build(detail, Instant.now(clock).toString(), resume, gap(resumeId), gradedBy);
    }

    /** The saved role skills check with skill names and resume point texts instead of IDs. */
    private Map<String, Object> gap(long resumeId) {
        List<String> stored = jdbc.queryForList("SELECT result FROM gap_result WHERE resume_id = :r", Map.of("r", resumeId), String.class);
        List<Object> with = new ArrayList<>(), without = new ArrayList<>();
        if (!stored.isEmpty()) {
            JsonNode result = JSON.readTree(stored.get(0));
            Map<Long, String> texts = new HashMap<>();
            jdbc.query("SELECT id, text FROM resume_point WHERE resume_id = :r", Map.of("r", resumeId), rs -> { texts.put(rs.getLong("id"), rs.getString("text")); });
            for (JsonNode s : result.path("withEvidence")) {
                List<String> points = new ArrayList<>();
                for (JsonNode p : s.path("resumePointIds")) if (texts.containsKey(p.asLong())) points.add(texts.get(p.asLong()));
                with.add(Map.of("skill", s.path("skill").asString(""), "resumePoints", points));
            }
            for (JsonNode s : result.path("withoutEvidence")) without.add(Map.of("skill", s.path("skill").asString("")));
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("withEvidence", with);
        out.put("withoutEvidence", without);
        return out;
    }
}
