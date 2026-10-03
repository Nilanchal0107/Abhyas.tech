package tech.abhyas.trainer.session;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/** Reads the drafted key facts of resume points, in order. Read separately because GROUP_CONCAT would cut long facts at 1,024 bytes. */
final class KeyFacts {
    private KeyFacts() {}

    static Map<Long, List<String>> load(NamedParameterJdbcTemplate jdbc, Collection<Long> pointIds) {
        Map<Long, List<String>> out = new LinkedHashMap<>();
        if (pointIds.isEmpty()) return out;
        jdbc.query("SELECT resume_point_id, fact_text FROM key_fact WHERE resume_point_id IN (:ids) ORDER BY resume_point_id, position", Map.of("ids", pointIds),
                rs -> { out.computeIfAbsent(rs.getLong("resume_point_id"), k -> new java.util.ArrayList<>()).add(rs.getString("fact_text")); });
        return out;
    }
}
