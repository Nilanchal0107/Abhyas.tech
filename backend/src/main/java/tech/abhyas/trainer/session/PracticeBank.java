package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * The practice bank (FR-20). When AI cannot write the questions of a session, {@link #fill} picks ready-made questions for the
 * resume's skills and fills their placeholders from the resume, then falls back to generic templates built from the resume itself.
 * {@link #learn} goes the other way: a technical, skill-based question the AI wrote and Java validated is saved for everyone,
 * but only when it holds nothing personal. Nothing here tells the user that AI was off.
 */
@Component
class PracticeBank {
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(project|skill|company|bullet|theme)}");
    private static final Pattern OWNER = Pattern.compile("^(experience|projects)\\[(\\d+)]\\.bullets\\[");
    private static final int WINDOW = 5; // a run of this many words copied from a bullet makes a question personal
    private static final Map<String, String> THEME_TEXT = Map.of("teamwork", "worked through a teamwork challenge", "conflict", "had a conflict with a teammate",
            "failure", "faced a failure or setback", "leadership", "took the lead", "deadline_pressure", "worked under deadline pressure");

    private final NamedParameterJdbcTemplate jdbc;
    private final Random random = new Random();

    PracticeBank(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    /** A resume point with the project or company it came from (null for a skill, or when unknown). */
    record Pt(long id, String kind, String text, Long skillId, String owner, boolean ownerIsCompany) {
        boolean bullet() { return !kind.equals("skill"); }
    }

    /** What placeholders and templates are filled from. */
    record Ctx(long resumeId, List<Pt> points, List<String> projects, List<String> companies, Map<Long, String> skills, Set<Long> wanted, List<String> hidden) {}

    // ---- context ----

    Ctx context(long resumeId) {
        Map<String, Object> resume = jdbc.queryForMap("SELECT parsed_json, target_role_id FROM resume WHERE id = :r", Map.of("r", resumeId));
        JsonNode parsed = JSON.readTree(String.valueOf(resume.get("parsed_json")));
        List<String> projects = names(parsed.path("projects"), "name"), companies = names(parsed.path("experience"), "company");
        List<String> hidden = new ArrayList<>(projects); // text the shared bank must never hold
        hidden.addAll(companies);
        hidden.addAll(names(parsed.path("education"), "school"));
        String me = parsed.path("profile").path("name").asString("").strip();
        if (!me.isEmpty()) hidden.add(me);

        List<Pt> points = new ArrayList<>();
        jdbc.query("SELECT id, kind, source_ref, text, skill_id FROM resume_point WHERE resume_id = :r ORDER BY position", Map.of("r", resumeId), rs -> {
            String kind = rs.getString("kind"), ref = rs.getString("source_ref");
            String owner = null;
            boolean company = false;
            Matcher m = OWNER.matcher(ref);
            if (m.find()) {
                company = m.group(1).equals("experience");
                List<String> list = company ? companies : projects;
                int i = Integer.parseInt(m.group(2));
                owner = i < list.size() ? list.get(i) : null;
            }
            points.add(new Pt(rs.getLong("id"), kind, rs.getString("text"), rs.getObject("skill_id") == null ? null : rs.getLong("skill_id"), owner, company));
        });
        Map<Long, String> skills = new LinkedHashMap<>();
        jdbc.query("SELECT id, name FROM skill ORDER BY id", rs -> { skills.put(rs.getLong("id"), rs.getString("name")); });
        Set<Long> wanted = new LinkedHashSet<>(); // skills on the resume first, then the target role's skills (the gap)
        points.stream().filter(p -> p.skillId() != null).forEach(p -> wanted.add(p.skillId()));
        wanted.addAll(jdbc.queryForList("SELECT skill_id FROM role_skill WHERE role_id = :ro", Map.of("ro", ((Number) resume.get("target_role_id")).longValue()), Long.class));
        List<String> bullets = points.stream().filter(Pt::bullet).map(Pt::text).toList();
        List<String> windows = new ArrayList<>(hidden);
        windows.addAll(bullets);
        return new Ctx(resumeId, points, projects, companies, skills, wanted, windows);
    }

    private static List<String> names(JsonNode list, String field) {
        List<String> out = new ArrayList<>();
        if (list.isArray()) for (JsonNode n : list) out.add(n.path(field).asString("").strip());
        return out; // keeps its place even when blank, so a bullet's index still finds its entry
    }

    // ---- pure helpers ----

    /** How many times the skill appears in the text as a whole word (case-insensitive). */
    static int mentions(String text, String skill) {
        if (skill.length() < 2) return 0; // ponytail: a one-letter skill such as C would match any stray letter; it is left to the bank rows
        Pattern p = Pattern.compile("(?<![\\p{L}\\p{N}])" + Pattern.quote(skill) + "(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE);
        return (int) p.matcher(text).results().count();
    }

    /** Fills {project}, {skill}, {company}, {bullet} and {theme}. Returns null when one of them has nothing to fill it with. */
    static String fillPlaceholders(String template, String project, String skill, String company, String bullet, String theme) {
        Matcher m = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String v = switch (m.group(1)) {
                case "project" -> project;
                case "skill" -> skill;
                case "company" -> company;
                case "bullet" -> bullet;
                default -> theme;
            };
            if (v == null || v.isBlank()) return null;
            m.appendReplacement(sb, Matcher.quoteReplacement(v));
        }
        return m.appendTail(sb).toString();
    }

    private static String words(String s) { return s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}+#]+", " ").strip(); }

    /**
     * True when any of the strings holds one of the user's own names (project, company, school, name) or a run of {@value #WINDOW}
     * words copied from one of their bullets. {@code hidden} is {@link Ctx#hidden}: the names first, then the bullets.
     */
    static boolean personal(List<String> hidden, List<String> strings) {
        String all = " " + words(String.join(" ", strings)) + " ";
        for (String h : hidden) {
            String w = words(h);
            if (w.length() < 3) continue;
            String[] parts = w.split(" ");
            if (parts.length < WINDOW) { if (all.contains(" " + w + " ")) return true; continue; } // a name, or a short bullet
            for (int i = 0; i + WINDOW <= parts.length; i++) {
                if (all.contains(" " + String.join(" ", java.util.Arrays.copyOfRange(parts, i, i + WINDOW)) + " ")) return true;
            }
        }
        return false;
    }

    // ---- learn: AI questions into the shared bank ----

    /**
     * Saves a validated AI question into the bank when it is technical, tied to a skill named in its text, and has nothing personal.
     * Returns whether a new row was added (the unique text hash drops a repeat, even when two saves race).
     */
    boolean learn(Ctx c, String type, String theme, String text, String optionsJson, String correctJson, String acceptedJson, String provider) {
        if (theme != null) return false; // behavioral questions are built from the user's own story
        List<String> strings = new ArrayList<>(List.of(text));
        if (optionsJson != null) strings.add(optionsJson);
        if (acceptedJson != null) strings.add(acceptedJson);
        if (personal(c.hidden(), strings)) return false;
        Long skill = null;
        for (long id : c.skills().keySet().stream().sorted((a, b) -> Boolean.compare(!c.wanted().contains(a), !c.wanted().contains(b))).toList()) {
            if (mentions(text, c.skills().get(id)) > 0) { skill = id; break; }
        }
        if (skill == null) return false;
        return jdbc.update("""
                INSERT IGNORE INTO bank_question (type, skill_id, text_template, options, correct_options, accepted_answers, source, provider, text_hash)
                VALUES (:type, :skill, :text, :opts, :correct, :accepted, 'ai', :provider, :hash)""",
                new MapSqlParameterSource("type", type).addValue("skill", skill).addValue("text", text).addValue("opts", optionsJson)
                        .addValue("correct", correctJson).addValue("accepted", acceptedJson).addValue("provider", provider)
                        .addValue("hash", SessionService.hash(skill + "|" + type + "|" + text))) == 1;
    }

    // ---- fill: questions without AI ----

    /** Creates up to the missing number of questions for this resume: bank rows first, then generic templates. */
    void fill(long resumeId, String type, int technical, int behavioral) {
        Ctx c = context(resumeId);
        if (technical > 0) technical -= fromBank(c, type, technical, false);
        if (behavioral > 0) behavioral -= fromBank(c, type, behavioral, true);
        if (technical > 0) fromTemplates(c, type, technical, false);
        if (behavioral > 0) fromTemplates(c, type, behavioral, true);
    }

    private int fromBank(Ctx c, String type, int need, boolean behavioral) {
        if (!behavioral && c.wanted().isEmpty()) return 0;
        String sql = "SELECT b.id, b.skill_id, b.theme, b.text_template, b.options, b.correct_options, b.accepted_answers, b.key_facts, b.keywords FROM bank_question b WHERE b.type = :type AND "
                + (behavioral ? "b.theme IS NOT NULL" : "b.skill_id IN (:skills)")
                + " AND NOT EXISTS (SELECT 1 FROM question q WHERE q.resume_id = :r AND q.bank_question_id = b.id)";
        List<Map<String, Object>> rows = jdbc.queryForList(sql,
                new MapSqlParameterSource("type", type).addValue("r", c.resumeId()).addValue("skills", behavioral ? List.of(0L) : c.wanted()));
        Collections.shuffle(rows, random);
        rows.sort((a, b) -> Boolean.compare(!onResume(c, a), !onResume(c, b))); // skills the resume claims come before the role's gap
        int made = 0;
        for (Map<String, Object> row : rows) {
            if (made >= need) break;
            String theme = (String) row.get("theme");
            Long skillId = row.get("skill_id") == null ? null : ((Number) row.get("skill_id")).longValue();
            String skill = skillId == null ? null : c.skills().get(skillId);
            Pt anchor = skill == null ? null : c.points().stream().filter(p -> p.bullet() && mentions(p.text(), skill) > 0).findFirst()
                    .orElse(c.points().stream().filter(p -> skillId.equals(p.skillId())).findFirst().orElse(null));
            Pt owned = anchor != null && anchor.owner() != null ? anchor : randomOwned(c);
            String text = fillPlaceholders((String) row.get("text_template"), owned == null || owned.ownerIsCompany() ? pick(c.projects()) : owned.owner(), skill,
                    owned != null && owned.ownerIsCompany() ? owned.owner() : pick(c.companies()),
                    anchor != null && anchor.bullet() ? anchor.text() : randomBullet(c), theme == null ? null : THEME_TEXT.get(theme));
            if (text == null) continue;
            if (insert(c.resumeId(), anchor == null ? null : anchor.id(), ((Number) row.get("id")).longValue(), type, theme, text,
                    (String) row.get("options"), (String) row.get("correct_options"), (String) row.get("accepted_answers"), (String) row.get("key_facts"), (String) row.get("keywords"))) made++;
        }
        return made;
    }

    private static boolean onResume(Ctx c, Map<String, Object> row) {
        return row.get("skill_id") != null && c.points().stream().anyMatch(p -> row.get("skill_id").equals(p.skillId()));
    }

    private String pick(List<String> names) {
        List<String> filled = names.stream().filter(s -> !s.isBlank()).toList();
        return filled.isEmpty() ? null : filled.get(random.nextInt(filled.size()));
    }

    private Pt randomOwned(Ctx c) {
        List<Pt> owned = c.points().stream().filter(p -> p.bullet() && p.owner() != null && !p.owner().isBlank()).toList();
        return owned.isEmpty() ? null : owned.get(random.nextInt(owned.size()));
    }

    private String randomBullet(Ctx c) {
        List<Pt> b = c.points().stream().filter(Pt::bullet).toList();
        return b.isEmpty() ? null : b.get(random.nextInt(b.size())).text();
    }

    // ---- generic templates, built from the resume so the answer is known ----

    private void fromTemplates(Ctx c, String type, int need, boolean behavioral) {
        int made = 0;
        if (behavioral) {
            List<String[]> combos = new ArrayList<>(); // theme, owner label
            for (String theme : SessionService.THEMES) {
                combos.add(new String[] {theme, ""});
                for (Pt p : c.points()) if (p.bullet() && p.owner() != null && !p.owner().isBlank()) combos.add(new String[] {theme, (p.ownerIsCompany() ? " while working at " : " while working on ") + p.owner()});
            }
            Collections.shuffle(combos, random);
            // one question per theme first, so a part does not repeat one theme while others are unused
            Set<String> seen = new LinkedHashSet<>();
            List<String[]> ordered = new ArrayList<>();
            for (String[] x : combos) if (seen.add(x[0])) ordered.add(x);
            for (String[] x : combos) if (!ordered.contains(x)) ordered.add(x);
            for (String[] x : ordered) {
                if (made >= need) break;
                if (insert(c.resumeId(), null, null, type, x[0], "Tell me about a time you " + THEME_TEXT.get(x[0]) + x[1] + ".", null, null, null, null, null)) made++;
            }
            return;
        }
        List<Pt> bullets = new ArrayList<>(c.points().stream().filter(Pt::bullet).toList());
        Collections.shuffle(bullets, random);
        switch (type) {
            case "fill_blank" -> {
                for (Pt p : bullets) {
                    if (made >= need) break;
                    for (String skill : c.skills().values()) {
                        if (mentions(p.text(), skill) != 1) continue;
                        String blanked = Pattern.compile("(?<![\\p{L}\\p{N}])" + Pattern.quote(skill) + "(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE)
                                .matcher(p.text()).replaceFirst(Matcher.quoteReplacement(SessionService.BLANK));
                        if (insert(c.resumeId(), p.id(), null, type, null, "Your resume says: \"" + blanked + "\" Which word completes it?", null, null,
                                JSON.writeValueAsString(List.of(List.of(skill))), null, null)) made++;
                        break;
                    }
                }
            }
            case "multiple_correct" -> {
                for (Pt p : bullets) {
                    if (made >= need) break;
                    List<String> inside = c.skills().values().stream().filter(s -> mentions(p.text(), s) > 0).distinct().limit(3).toList();
                    List<String> others = new ArrayList<>(c.skills().values().stream().filter(s -> s.length() >= 2 && mentions(p.text(), s) == 0).distinct().toList());
                    if (inside.isEmpty() || others.size() < 4 - inside.size()) continue;
                    Collections.shuffle(others, random);
                    List<String> options = new ArrayList<>(inside);
                    options.addAll(others.subList(0, 4 - inside.size()));
                    Collections.shuffle(options, random);
                    List<Map<String, String>> opts = new ArrayList<>();
                    List<String> correct = new ArrayList<>();
                    for (int i = 0; i < 4; i++) {
                        String key = String.valueOf((char) ('A' + i));
                        opts.add(Map.of("key", key, "text", options.get(i)));
                        if (inside.contains(options.get(i))) correct.add(key);
                    }
                    if (insert(c.resumeId(), p.id(), null, type, null, "Which of these technologies are mentioned in this point of your resume: \"" + p.text() + "\"?",
                            JSON.writeValueAsString(opts), JSON.writeValueAsString(correct), null, null, null)) made++;
                }
            }
            default -> {
                List<Pt> pool = new ArrayList<>(bullets);
                pool.addAll(c.points().stream().filter(p -> !p.bullet()).toList());
                for (Pt p : pool) {
                    if (made >= need) break;
                    String text;
                    if (!p.bullet()) text = type.equals("voice") ? "Talk me through how you have used " + p.text() + " and what you would do differently."
                            : "You list " + p.text() + " on your resume. Where have you used it, and why did you choose it?";
                    else {
                        String where = p.owner() == null || p.owner().isBlank() ? "" : (p.ownerIsCompany() ? "At " : "In ") + p.owner() + ", ";
                        text = type.equals("voice") ? "Walk me through this point from your resume: \"" + p.text() + "\" What did you do, and why?"
                                : (where.isEmpty() ? "You" : where + "you") + " wrote: \"" + p.text() + "\" What was your part in this, and why did you take that approach?";
                    }
                    if (insert(c.resumeId(), p.id(), null, type, null, text, null, null, null, null, null)) made++;
                }
            }
        }
    }

    /** Inserts one question for the resume. The unique (resume_id, text_hash) index drops a repeat. */
    private boolean insert(long resumeId, Long pointId, Long bankId, String type, String theme, String text, String options, String correct, String accepted, String keyFacts, String keywords) {
        return jdbc.update("""
                INSERT IGNORE INTO question (resume_id, resume_point_id, bank_question_id, type, theme, text, options, correct_options, accepted_answers, key_facts, keywords, source, text_hash)
                VALUES (:r, :p, :b, :type, :theme, :text, :opts, :correct, :accepted, :facts, :kw, 'bank', :hash)""",
                new MapSqlParameterSource("r", resumeId).addValue("p", pointId).addValue("b", bankId).addValue("type", type).addValue("theme", theme)
                        .addValue("text", text).addValue("opts", options).addValue("correct", correct).addValue("accepted", accepted)
                        .addValue("facts", keyFacts).addValue("kw", keywords).addValue("hash", SessionService.hash(text))) == 1;
    }
}
