package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** The export shape (api-contracts 8): section order, phone and email removed, gradedBy only here. */
class ExportTest {
    static final JsonMapper JSON = JsonMapper.builder().build();

    static Map<String, Object> detail() {
        Map<String, Object> q = new LinkedHashMap<>(Map.of("type", "multiple_correct", "text", "Which?", "options", List.of(Map.of("key", "A", "text", "a"))));
        Map<String, Object> a1 = new LinkedHashMap<>();
        a1.put("position", 1); a1.put("maxMarks", 2); a1.put("question", q); a1.put("score", 1); a1.put("mastered", false);
        a1.put("answer", Map.of("selectedOptions", List.of("A"), "answeredVia", "click")); a1.put("correctOptions", List.of("A", "C"));
        a1.put("feedback", Map.of("status", "none"));
        Map<String, Object> q2 = Map.of("type", "brief", "theme", "teamwork", "text", "Tell me.");
        Map<String, Object> a2 = new LinkedHashMap<>();
        a2.put("position", 2); a2.put("maxMarks", 5); a2.put("question", q2); a2.put("answer", null); a2.put("score", 0); a2.put("mastered", false);
        a2.put("checklist", List.of()); a2.put("feedback", Map.of("status", "ready", "text", "Good.", "whatToFix", "More detail."));
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("completedAt", "2026-10-03T10:15:30.123Z"); d.put("role", Map.of("id", 1, "name", "Backend Developer")); d.put("voiceSkipped", true);
        d.put("totalMarks", 1); d.put("maxMarks", 50); d.put("marksByPart", List.of()); d.put("report", Map.of("headline", "H", "summary", "S"));
        d.put("attempts", List.of(a1, a2));
        return d;
    }

    @Test
    void sectionsComeInTheDocumentedOrderWithGradedByOnlyInGrades() {
        JsonNode resume = JSON.readTree("{\"profile\":{\"name\":\"Asha\",\"email\":\"a@b.c\",\"phone\":\"+91 1\",\"links\":[]},\"skills\":[\"Java\"]}");
        Map<String, Object> out = Export.build(detail(), "2026-10-04T00:00:00Z", resume, Map.of("withEvidence", List.of(), "withoutEvidence", List.of()),
                Map.of(1, "java", 2, "fallback"), List.of(Map.of("title", "T")));
        assertThat(out.keySet()).containsExactly("description", "metadata", "resume", "gapCheck", "questionsAndAnswers", "grades", "feedback", "storyBank");
        JsonNode json = JSON.valueToTree(out);
        assertThat(json.at("/metadata/sessionDate").asString()).isEqualTo("2026-10-03");
        assertThat(json.at("/metadata/voicePartSkipped").asBoolean()).isTrue();
        assertThat(json.at("/grades/perQuestion/0/gradedBy").asString()).isEqualTo("java");
        assertThat(json.at("/grades/perQuestion/1/gradedBy").asString()).isEqualTo("fallback");
        assertThat(json.at("/questionsAndAnswers").toString()).doesNotContain("gradedBy");
        assertThat(json.at("/questionsAndAnswers/1/userAnswer").isNull()).isTrue();
        assertThat(json.at("/feedback/perAnswer")).hasSize(1); // the objective answer has no feedback text
    }

    @Test
    void phoneAndEmailKeysAreGoneButNameAndLinksStay() {
        JsonNode resume = JSON.readTree("{\"profile\":{\"name\":\"Asha\",\"email\":\"a@b.c\",\"phone\":\"+91 1\",\"links\":[{\"label\":\"GitHub\"}]}}");
        JsonNode clean = Export.cleanResume(resume);
        assertThat(clean.at("/profile").has("email")).isFalse();
        assertThat(clean.at("/profile").has("phone")).isFalse();
        assertThat(clean.at("/profile/name").asString()).isEqualTo("Asha");
        assertThat(clean.at("/profile/links")).hasSize(1);
        assertThat(resume.at("/profile").has("phone")).isTrue(); // the original is not touched
    }
}
