package tech.abhyas.trainer.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

/** Builds the export JSON (api-contracts 8, FR-19) from already loaded parts. Pure, so the section order and the removals are unit-tested. */
final class Export {
    private Export() {}

    static final String DESCRIPTION = "Practice session export from Abhyas.tech. Sections: resume (the structured resume used), gapCheck (role skills with and without evidence), "
            + "questionsAndAnswers (every question with the user's answer and the correct answer), grades (marks, checklists with quoted proof, mastered or not, and gradedBy: "
            + "ai, fallback or java), feedback (per-answer feedback, voice stats and the session report).";

    /** The resume without phone and email. Name, links and the rest stay. */
    static JsonNode cleanResume(JsonNode parsed) {
        JsonNode copy = parsed.deepCopy();
        if (copy.path("profile") instanceof ObjectNode profile) profile.remove(List.of("phone", "email"));
        return copy;
    }

    /**
     * @param detail   the SessionDetail map of a completed, fully graded session
     * @param gradedBy position to java, ai or fallback
     * @param gap      withEvidence and withoutEvidence with skill names and resume point texts (no IDs)
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> build(Map<String, Object> detail, String exportedAt, JsonNode resume, Map<String, Object> gap, Map<Integer, String> gradedBy) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("description", DESCRIPTION);
        String completed = (String) detail.get("completedAt");
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("exportedAt", exportedAt);
        meta.put("sessionDate", completed == null ? null : completed.substring(0, 10));
        meta.put("roleName", ((Map<String, Object>) detail.get("role")).get("name"));
        meta.put("voicePartSkipped", detail.get("voiceSkipped"));
        meta.put("mode", detail.get("mode"));
        out.put("metadata", meta);
        out.put("resume", cleanResume(resume));
        out.put("gapCheck", gap);

        List<Map<String, Object>> attempts = (List<Map<String, Object>>) detail.get("attempts");
        List<Map<String, Object>> qa = new ArrayList<>(), perQuestion = new ArrayList<>(), perAnswer = new ArrayList<>();
        for (Map<String, Object> a : attempts) {
            Map<String, Object> q = (Map<String, Object>) a.get("question");
            Map<String, Object> ans = (Map<String, Object>) a.get("answer");
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("position", a.get("position"));
            row.put("type", q.get("type"));
            row.put("theme", q.get("theme"));
            row.put("question", q.get("text"));
            row.put("options", q.get("options"));
            Map<String, Object> user = null;
            if (ans != null) {
                user = new LinkedHashMap<>();
                for (String k : List.of("selectedOptions", "blankAnswers", "text", "transcript", "answeredVia")) user.put(k, ans.get(k));
            }
            row.put("userAnswer", user);
            if (a.get("followUp") != null) row.put("followUp", a.get("followUp")); // interview mode: the AI follow-up and the reply
            row.put("correctOptions", a.get("correctOptions"));
            row.put("acceptedAnswers", a.get("acceptedAnswers"));
            qa.add(row);

            Map<String, Object> g = new LinkedHashMap<>();
            g.put("position", a.get("position"));
            g.put("score", a.get("score"));
            g.put("maxMarks", a.get("maxMarks"));
            g.put("mastered", a.get("mastered"));
            g.put("gradedBy", gradedBy.get((Integer) a.get("position")));
            g.put("checklist", a.get("checklist"));
            g.put("blankResults", a.get("blankResults"));
            perQuestion.add(g);

            Map<String, Object> fb = (Map<String, Object>) a.get("feedback");
            if (fb != null && fb.get("text") != null) {
                Map<String, Object> f = new LinkedHashMap<>();
                f.put("position", a.get("position"));
                f.put("text", fb.get("text"));
                f.put("whatToFix", fb.get("whatToFix"));
                f.put("deliveryComment", fb.get("deliveryComment"));
                f.put("voiceStats", ans == null ? null : ans.get("voiceStats"));
                perAnswer.add(f);
            }
        }
        out.put("questionsAndAnswers", qa);

        Map<String, Object> grades = new LinkedHashMap<>();
        grades.put("sessionTotal", detail.get("totalMarks"));
        grades.put("maxMarks", detail.get("maxMarks"));
        grades.put("marksByPart", detail.get("marksByPart"));
        grades.put("perQuestion", perQuestion);
        out.put("grades", grades);

        Map<String, Object> report = (Map<String, Object>) detail.get("report");
        Map<String, Object> feedback = new LinkedHashMap<>();
        feedback.put("perAnswer", perAnswer);
        Map<String, Object> session = new LinkedHashMap<>();
        session.put("headline", report == null ? null : report.get("headline"));
        session.put("summary", report == null ? null : report.get("summary"));
        feedback.put("sessionReport", session);
        out.put("feedback", feedback);
        return out;
    }
}
