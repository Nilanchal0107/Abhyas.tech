package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
 * The practice bank (FR-20). {@link #fill} picks reviewed questions for the resume's skills and fills their placeholders from the resume, then falls back to generic templates built from the resume itself.
 * Nothing here tells the user that AI was off.
 */
@Component
class PracticeBank {
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{(project|skill|company|bullet|theme)}");
    private static final Pattern OWNER = Pattern.compile("^(experience|projects)\\[(\\d+)]\\.bullets\\[");
    private static final Map<String, String> THEME_TEXT = Map.of("teamwork", "worked through a teamwork challenge", "conflict", "had a conflict with a teammate",
            "failure", "faced a failure or setback", "leadership", "took the lead", "deadline_pressure", "worked under deadline pressure");

    /** Which role flavours a target role gets (ticket 10). Software Engineer gets the backend flavour; a role not listed (an old user-added one) gets both. */
    static final Map<String, List<String>> TRACKS = Map.of("Backend Developer", List.of("backend"), "Software Engineer", List.of("backend"),
            "Frontend Developer", List.of("frontend"), "Full Stack Developer", List.of("backend", "frontend"));

    private final NamedParameterJdbcTemplate jdbc;
    private final Random random = new Random();

    PracticeBank(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    /** A resume point with the project or company it came from (null for a skill, or when unknown). */
    record Pt(long id, String kind, String text, Long skillId, String owner, boolean ownerIsCompany) {
        boolean bullet() { return !kind.equals("skill"); }
    }

    /** What placeholders and templates are filled from. */
    record Ctx(long resumeId, List<Pt> points, List<String> projects, List<String> companies, Map<Long, String> skills, Set<Long> wanted, List<String> tracks) {}

    // ---- context ----

    Ctx context(long resumeId) {
        Map<String, Object> resume = jdbc.queryForMap("SELECT r.parsed_json, r.target_role_id, ro.name AS role_name FROM resume r JOIN role ro ON ro.id = r.target_role_id WHERE r.id = :r", Map.of("r", resumeId));
        JsonNode parsed = JSON.readTree(String.valueOf(resume.get("parsed_json")));
        List<String> projects = names(parsed.path("projects"), "name"), companies = names(parsed.path("experience"), "company");

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
        return new Ctx(resumeId, points, projects, companies, skills, wanted, TRACKS.getOrDefault(String.valueOf(resume.get("role_name")), List.of("backend", "frontend")));
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

    // ---- fill: questions without AI ----

    /** Creates up to the missing number of questions for this resume: bank rows first, then generic templates. */
    void fill(long resumeId, String type, int technical, int behavioral) {
        Ctx c = context(resumeId);
        if (technical > 0) technical -= fromBank(c, type, technical, false, false);
        if (technical > 0) technical -= fromBank(c, type, technical, false, true); // then the bank's other skills (ticket 19)
        if (behavioral > 0) behavioral -= fromBank(c, type, behavioral, true, false);
        if (technical > 0) fromTemplates(c, type, technical, false);
        if (behavioral > 0) fromTemplates(c, type, behavioral, true);
    }

    /** {@code anySkill}: skills outside the resume and the role's list, a last step before the generic templates. */
    private int fromBank(Ctx c, String type, int need, boolean behavioral, boolean anySkill) {
        if (!behavioral && !anySkill && c.wanted().isEmpty()) return 0;
        String sql = "SELECT b.id, b.skill_id, b.theme, b.format, b.text_template, b.options, b.correct_options, b.accepted_answers, b.key_facts, b.keywords FROM bank_question b WHERE b.source = 'seed' AND b.format <> 'design' AND (b.track IS NULL OR b.track IN (:tracks)) AND b.type = :type AND "
                + (behavioral ? "b.theme IS NOT NULL" : anySkill ? "b.skill_id IS NOT NULL" : "b.skill_id IN (:skills)")
                + " AND NOT EXISTS (SELECT 1 FROM question q WHERE q.resume_id = :r AND q.bank_question_id = b.id)"
                // a question this user mastered on any resume rests for a month before it can come back (ticket 18)
                + " AND NOT EXISTS (SELECT 1 FROM bank_performance bp JOIN resume r ON r.user_id = bp.user_id WHERE r.id = :r AND bp.bank_question_id = b.id"
                + " AND bp.mastered AND bp.last_asked_at > UTC_TIMESTAMP(3) - INTERVAL 1 MONTH)";
        List<Map<String, Object>> rows = jdbc.queryForList(sql,
                new MapSqlParameterSource("type", type).addValue("r", c.resumeId()).addValue("skills", behavioral ? List.of(0L) : c.wanted()).addValue("tracks", c.tracks()));
        Collections.shuffle(rows, random);
        rows = mix(rows, need, behavioral ? 0 : 1, r -> onResume(c, r));
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
                    (String) row.get("options"), (String) row.get("correct_options"), (String) row.get("accepted_answers"), (String) row.get("key_facts"), (String) row.get("keywords"), (String) row.get("format"))) made++;
        }
        return made;
    }

    /**
     * Interview design questions (ticket 09): adds up to {@code need} reviewed design questions of the resume's role flavours that the resume does not have yet,
     * leaving out ones this user mastered within the last month. Does nothing when the bank has none.
     */
    void fillDesign(long resumeId, int need) {
        Ctx c = context(resumeId);
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT b.id, b.text_template, b.key_facts, b.keywords FROM bank_question b
                WHERE b.source = 'seed' AND b.format = 'design' AND b.type = 'voice' AND b.track IN (:tracks)
                  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.resume_id = :r AND q.bank_question_id = b.id)
                  AND NOT EXISTS (SELECT 1 FROM bank_performance bp JOIN resume r ON r.user_id = bp.user_id WHERE r.id = :r AND bp.bank_question_id = b.id
                                  AND bp.mastered AND bp.last_asked_at > UTC_TIMESTAMP(3) - INTERVAL 1 MONTH)""",
                new MapSqlParameterSource("r", resumeId).addValue("tracks", c.tracks()));
        Collections.shuffle(rows, random);
        for (Map<String, Object> row : rows.subList(0, Math.min(need, rows.size())))
            insert(resumeId, null, ((Number) row.get("id")).longValue(), "voice", null, (String) row.get("text_template"), null, null, null,
                    (String) row.get("key_facts"), (String) row.get("keywords"), "design");
    }

    /**
     * Orders rows so about 80% of the first {@code need} come from skills on the resume and 20% from the role's gap (skill-bank D4).
     * {@code gapShare} is 0 for behavioral rows (no skill, no mix). Either side fills in for the other when it runs short.
     */
    static <T> List<T> mix(List<T> rows, int need, int gapShare, java.util.function.Predicate<T> onResume) {
        if (gapShare == 0) return rows;
        List<T> mine = rows.stream().filter(onResume).toList(), gap = rows.stream().filter(onResume.negate()).toList();
        int gapN = Math.min(gap.size(), Math.round(need * 0.2f));
        List<T> out = new ArrayList<>(mine.subList(0, Math.min(mine.size(), need - gapN)));
        out.addAll(gap.subList(0, gapN));
        out.addAll(mine.subList(Math.min(mine.size(), need - gapN), mine.size())); // the rest, resume skills first
        out.addAll(gap.subList(gapN, gap.size()));
        return out;
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
                if (insert(c.resumeId(), null, null, type, x[0], "Tell me about a time you " + THEME_TEXT.get(x[0]) + x[1] + ".", null, null, null, null, null, null)) made++;
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
                                JSON.writeValueAsString(List.of(List.of(skill))), null, null, null)) made++;
                        break;
                    }
                }
            }
            case "multiple_correct" -> { } // no resume-recital template: a short slot is filled from the bank by SessionService.relax
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
                    if (insert(c.resumeId(), p.id(), null, type, null, text, null, null, null, null, null, null)) made++;
                }
            }
        }
    }

    /** Inserts one question for the resume. The unique (resume_id, text_hash) index drops a repeat. */
    private boolean insert(long resumeId, Long pointId, Long bankId, String type, String theme, String text, String options, String correct, String accepted, String keyFacts, String keywords, String format) {
        return jdbc.update("""
                INSERT IGNORE INTO question (resume_id, resume_point_id, bank_question_id, type, theme, text, options, correct_options, accepted_answers, key_facts, keywords, format, source, text_hash)
                VALUES (:r, :p, :b, :type, :theme, :text, :opts, :correct, :accepted, :facts, :kw, :format, 'bank', :hash)""",
                new MapSqlParameterSource("r", resumeId).addValue("p", pointId).addValue("b", bankId).addValue("type", type).addValue("theme", theme)
                        .addValue("text", text).addValue("opts", options).addValue("correct", correct).addValue("accepted", accepted)
                        .addValue("facts", keyFacts).addValue("kw", keywords).addValue("format", format == null ? "standard" : format).addValue("hash", SessionService.hash(text))) == 1;
    }
}
