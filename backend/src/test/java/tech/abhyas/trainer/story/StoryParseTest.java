package tech.abhyas.trainer.story;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tech.abhyas.trainer.common.ApiException;
import tools.jackson.databind.json.JsonMapper;

/** Story limits from api-contracts 1.2 and the list preview from 7. */
class StoryParseTest {
    static final JsonMapper JSON = JsonMapper.builder().build();

    private static StoryService.Input parse(String json) { return StoryService.parse(JSON.readTree(json)); }

    private static void rejects(String json, String field) {
        assertThatThrownBy(() -> parse(json)).isInstanceOfSatisfying(ApiException.class, e -> {
            assertThat(e.type()).isEqualTo("validation");
            assertThat(e.extras().get("errors").toString()).contains("field=" + field);
        });
    }

    @Test
    void onlyTheTitleIsRequiredAndBlanksBecomeNull() {
        StoryService.Input in = parse("{\"title\":\"  Outage \",\"situation\":\"   \",\"action\":\"I fixed it\"}");
        assertThat(in.title()).isEqualTo("Outage");
        assertThat(in.boxes().get("situation")).isNull();
        assertThat(in.boxes().get("action")).isEqualTo("I fixed it");
        assertThat(in.tags()).isEmpty();
    }

    @Test
    void titleLimits() {
        rejects("{}", "title");
        rejects("{\"title\":\" \"}", "title");
        rejects("{\"title\":\"" + "x".repeat(201) + "\"}", "title");
        assertThat(parse("{\"title\":\"" + "x".repeat(200) + "\"}").title()).hasSize(200);
    }

    @Test
    void boxLimit() {
        rejects("{\"title\":\"t\",\"result\":\"" + "x".repeat(5001) + "\"}", "result");
        parse("{\"title\":\"t\",\"result\":\"" + "x".repeat(5000) + "\"}");
    }

    @Test
    void tagLimitsAndDuplicates() {
        assertThat(parse("{\"title\":\"t\",\"tags\":[\"a\",\"a\",\" b \"]}").tags()).containsExactly("a", "b");
        rejects("{\"title\":\"t\",\"tags\":[\"" + "x".repeat(41) + "\"]}", "tags");
        rejects("{\"title\":\"t\",\"tags\":[\"\"]}", "tags");
        rejects("{\"title\":\"t\",\"tags\":[\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\"]}", "tags");
    }

    @Test
    void resumePointLimit() {
        String ids = String.join(",", java.util.stream.IntStream.rangeClosed(1, 21).mapToObj(String::valueOf).toList());
        rejects("{\"title\":\"t\",\"resumePointIds\":[" + ids + "]}", "resumePointIds");
        rejects("{\"title\":\"t\",\"resumePointIds\":[\"x\"]}", "resumePointIds");
        assertThat(parse("{\"title\":\"t\",\"resumePointIds\":[5,5,6],\"sourceAttemptId\":9}").pointIds()).containsExactly(5L, 6L);
    }

    @Test
    void previewIsTheFirstNonEmptyOfSituationThenActionCutAt140() {
        assertThat(StoryService.preview("S", "A")).isEqualTo("S");
        assertThat(StoryService.preview(null, "A")).isEqualTo("A");
        assertThat(StoryService.preview(" ", "A")).isEqualTo("A");
        assertThat(StoryService.preview(null, null)).isNull();
        assertThat(StoryService.preview("x".repeat(200), null)).hasSize(140);
    }
}
