package tech.abhyas.trainer.role;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.JsonNode;

/** Add a missing role (FR-3, W5): AI skill draft and the private role insert. */
@Service
public class RoleService {
    static final int MAX_SKILLS = 30, MAX_ALIASES = 10, MAX_LEN = 100;

    private final AiClient ai;
    private final NamedParameterJdbcTemplate jdbc;

    public RoleService(AiClient ai, NamedParameterJdbcTemplate jdbc) { this.ai = ai; this.jdbc = jdbc; }

    /** Skills for a role name. Empty when AI is unavailable or its reply is unusable: the user types their own. */
    public List<String> draftSkills(String name) {
        String prompt = "List the 8 to 12 most important skills (tools, languages, concepts) for the job role \""
                + name + "\". Reply as {\"skills\": [\"Skill\", ...]}, each skill under 100 characters.";
        Optional<JsonNode> reply = ai.json(Provider.GROQ_CHAT, prompt, RoleService::validDraft);
        List<String> out = new ArrayList<>();
        if (reply.isPresent()) {
            for (JsonNode s : reply.get().get("skills")) out.add(s.asString().strip());
        }
        return dedupe(out.stream().filter(s -> !s.isEmpty() && s.length() <= MAX_LEN).toList())
                .stream().limit(MAX_SKILLS).toList();
    }

    private static boolean validDraft(JsonNode n) {
        JsonNode skills = n.get("skills");
        if (skills == null || !skills.isArray()) return false;
        for (JsonNode s : skills) if (!s.isString()) return false;
        return true;
    }

    @Transactional
    public Map<String, Object> add(long userId, String name, List<String> aliases, List<String> skills) {
        name = name.strip();
        aliases = dedupe(aliases.stream().map(String::strip).toList());
        skills = dedupe(skills.stream().map(String::strip).toList());
        validate(name, aliases, skills);
        checkDuplicate(userId, name, aliases);

        GeneratedKeyHolder keys = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO role (name, owner_user_id) VALUES (:name, :me)",
                new MapSqlParameterSource("name", name).addValue("me", userId), keys);
        long roleId = keys.getKey().longValue();
        for (String a : aliases) {
            jdbc.update("INSERT INTO role_alias (role_id, alias) VALUES (:r, :a)", Map.of("r", roleId, "a", a));
        }
        for (String s : skills) {
            // the name column is case-insensitive, so INSERT IGNORE + SELECT reuses an existing skill in any casing
            jdbc.update("INSERT IGNORE INTO skill (name) VALUES (:s)", Map.of("s", s));
            jdbc.update("INSERT INTO role_skill (role_id, skill_id) SELECT :r, id FROM skill WHERE name = :s",
                    Map.of("r", roleId, "s", s));
        }
        Map<String, Object> role = new LinkedHashMap<>();
        role.put("id", roleId);
        role.put("name", name);
        role.put("category", null);
        role.put("aliases", aliases);
        role.put("skills", jdbc.queryForList("""
                SELECT s.name FROM role_skill rs JOIN skill s ON s.id = rs.skill_id
                WHERE rs.role_id = :r ORDER BY s.name""", Map.of("r", roleId), String.class));
        role.put("isOwn", true);
        return role;
    }

    private static void validate(String name, List<String> aliases, List<String> skills) {
        List<Map<String, String>> errors = new ArrayList<>();
        if (name.length() < 2 || name.length() > MAX_LEN) errors.add(err("name", "Use 2 to 100 characters."));
        if (aliases.size() > MAX_ALIASES) errors.add(err("aliases", "Add at most 10 aliases."));
        if (skills.size() > MAX_SKILLS) errors.add(err("skills", "Add at most 30 skills."));
        if (aliases.stream().anyMatch(a -> a.isEmpty() || a.length() > MAX_LEN)) {
            errors.add(err("aliases", "Use 1 to 100 characters for each alias."));
        }
        if (skills.stream().anyMatch(s -> s.isEmpty() || s.length() > MAX_LEN)) {
            errors.add(err("skills", "Use 1 to 100 characters for each skill."));
        }
        if (!errors.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "validation", "Some fields need attention.",
                    Map.of("errors", errors));
        }
    }

    private static Map<String, String> err(String field, String message) { return Map.of("field", field, "message", message); }

    /** Near-duplicate: same name or alias as a starter role or the user's own, ignoring case, spaces and punctuation. */
    private void checkDuplicate(long userId, String name, List<String> aliases) {
        Set<String> wanted = new LinkedHashSet<>();
        wanted.add(key(name));
        aliases.forEach(a -> wanted.add(key(a)));
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT r.id, r.name, r.name AS term FROM role r WHERE r.owner_user_id IS NULL OR r.owner_user_id = :me
                UNION ALL
                SELECT r.id, r.name, a.alias FROM role r JOIN role_alias a ON a.role_id = r.id
                WHERE r.owner_user_id IS NULL OR r.owner_user_id = :me
                """, Map.of("me", userId));
        for (Map<String, Object> row : rows) {
            if (wanted.contains(key((String) row.get("term")))) {
                throw new ApiException(HttpStatus.CONFLICT, "role-exists",
                        "A similar role exists: " + row.get("name") + ".",
                        Map.of("existingRoleId", row.get("id"), "existingRoleName", row.get("name")));
            }
        }
    }

    static String key(String s) { return s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", ""); }

    private static List<String> dedupe(List<String> items) {
        Set<String> seen = new LinkedHashSet<>();
        List<String> out = new ArrayList<>();
        for (String s : items) if (seen.add(s.toLowerCase(Locale.ROOT))) out.add(s);
        return out;
    }
}
