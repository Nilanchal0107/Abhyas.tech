package tech.abhyas.trainer.role;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/** Role search (FR-3): starter roles plus the user's own, matched on name or alias, case-insensitive "contains". */
@Repository
public class RoleSearch {
    static final int LIMIT = 20;

    private final NamedParameterJdbcTemplate jdbc;

    public RoleSearch(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    // Case-insensitivity comes from the utf8mb4_0900_ai_ci column collation, so plain LIKE is enough.
    public List<Map<String, Object>> search(long userId, String q) {
        String like = "%" + q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        return jdbc.query("""
                SELECT r.id, r.name, r.category, r.owner_user_id IS NOT NULL AS own,
                       r.name LIKE :like AS name_match,
                       (SELECT MIN(a.alias) FROM role_alias a WHERE a.role_id = r.id AND a.alias LIKE :like) AS alias
                FROM role r
                WHERE (r.owner_user_id IS NULL OR r.owner_user_id = :me)
                  AND (r.name LIKE :like
                       OR EXISTS (SELECT 1 FROM role_alias a WHERE a.role_id = r.id AND a.alias LIKE :like))
                ORDER BY name_match DESC, r.name
                LIMIT :limit
                """,
                new MapSqlParameterSource("like", like).addValue("me", userId).addValue("limit", LIMIT),
                (rs, i) -> {
                    boolean byName = rs.getBoolean("name_match");
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getLong("id"));
                    m.put("name", rs.getString("name"));
                    m.put("category", rs.getString("category"));
                    m.put("matchedOn", byName ? "name" : "alias");
                    m.put("matchedAlias", byName ? null : rs.getString("alias"));
                    m.put("isOwn", rs.getBoolean("own"));
                    return m;
                });
    }
}
