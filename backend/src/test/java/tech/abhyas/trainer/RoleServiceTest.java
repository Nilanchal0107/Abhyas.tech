package tech.abhyas.trainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.role.RoleService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** AI draft handling and add-role limits (ticket 06). The inserts run against MySQL in RoleAddDbTest. */
class RoleServiceTest {
    static final JsonMapper JSON = JsonMapper.builder().build();
    AiClient ai = mock(AiClient.class);
    RoleService service = new RoleService(ai, null);

    @Test
    void draftIsCleanedAndCapped() {
        String many = String.join(",", IntStream.range(0, 40).mapToObj(i -> "\"S" + i + "\"").toList());
        when(ai.json(eq(Provider.GROQ_CHAT), any(), any())).thenReturn(Optional.of(JSON.readTree(
                "{\"skills\":[\" Python \",\"python\",\"\",\"" + "x".repeat(101) + "\"," + many + "]}")));
        assertThat(service.draftSkills("Prompt engineer")).hasSize(30).startsWith("Python", "S0");
    }

    @Test
    void unavailableAiIsAnEmptyList() {
        when(ai.json(any(), any(), any())).thenReturn(Optional.empty());
        assertThat(service.draftSkills("Prompt engineer")).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void draftReplyValidatorRejectsAWrongShape() {
        ArgumentCaptor<Predicate<JsonNode>> captor = ArgumentCaptor.forClass(Predicate.class);
        when(ai.json(any(), any(), captor.capture())).thenReturn(Optional.empty());
        service.draftSkills("x1");
        Predicate<JsonNode> valid = captor.getValue();
        assertThat(valid.test(JSON.readTree("{\"skills\":[\"a\"]}"))).isTrue();
        assertThat(valid.test(JSON.readTree("{\"skills\":\"a\"}"))).isFalse();
        assertThat(valid.test(JSON.readTree("{\"skills\":[1]}"))).isFalse();
        assertThat(valid.test(JSON.readTree("{}"))).isFalse();
    }

    @Test
    void limitsAreEnforcedBeforeTouchingTheDatabase() {
        List<String> eleven = IntStream.range(0, 11).mapToObj(i -> "a" + i).toList();
        List<String> thirtyOne = IntStream.range(0, 31).mapToObj(i -> "s" + i).toList();
        assertThatThrownBy(() -> service.add(1, "x", List.of(), List.of())).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> service.add(1, "Role", eleven, List.of())).isInstanceOf(ApiException.class);
        assertThatThrownBy(() -> service.add(1, "Role", List.of(), thirtyOne)).isInstanceOf(ApiException.class);
    }
}
