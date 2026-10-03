package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.api.Test;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.AiFailure;
import tech.abhyas.trainer.ai.AiGate;
import tech.abhyas.trainer.ai.AiTransport;
import tech.abhyas.trainer.ai.Provider;
import tools.jackson.databind.json.JsonMapper;

/** Gemma calls one at a time with a fixed gap, Retry-After honored once, then template text (ticket 21). Fake provider, no clock. */
class GemmaFeedbackTest {
    static final String FEEDBACK = "{\"text\":\"Good start.\",\"whatToFix\":\"Add the why.\",\"deliveryComment\":\"Steady pace.\"}";

    static class Fake implements AiTransport {
        final Queue<Object> script = new ArrayDeque<>();
        final List<String> prompts = new ArrayList<>();
        @Override public String send(Provider p, String prompt, Duration timeout) {
            prompts.add(prompt);
            Object step = script.remove();
            if (step instanceof AiFailure f) throw f;
            return (String) step;
        }
        @Override public String transcribe(byte[] audio, Duration timeout) { throw new AiFailure(AiFailure.Kind.OTHER); }
    }

    final Fake transport = new Fake();
    final List<Long> sleeps = new ArrayList<>();
    final AiClient ai = new AiClient(transport, new AiGate(Clock.systemUTC()), JsonMapper.builder().build(), 20, 40);
    final GemmaFeedback gemma = new GemmaFeedback(ai, sleeps::add, 7_000);

    @Test
    void callsAreSpacedByAFixedGapAndTheFirstOneHasNoWait() {
        transport.script.addAll(List.of(FEEDBACK, FEEDBACK, "{\"headline\":\"Nice start.\",\"summary\":\"Three points need work.\"}"));
        assertThat(gemma.feedback("p1", true)).get().satisfies(m -> assertThat(m.get("deliveryComment")).isEqualTo("Steady pace."));
        assertThat(sleeps).isEmpty();
        assertThat(gemma.feedback("p2", false)).get().satisfies(m -> assertThat(m.get("deliveryComment")).isNull()); // only voice answers carry a delivery comment
        assertThat(gemma.report("p3")).get().satisfies(m -> assertThat(m.get("headline")).isEqualTo("Nice start."));
        assertThat(sleeps).containsExactly(7_000L, 7_000L);
        assertThat(transport.prompts).hasSize(3); // one call at a time, in order
    }

    @Test
    void aRateLimitWithRetryAfterIsWaitedOutOnceThenTheCallSucceeds() {
        transport.script.addAll(List.of(new AiFailure(AiFailure.Kind.RATE_LIMIT, 12), FEEDBACK));
        assertThat(gemma.feedback("p1", false)).isPresent();
        assertThat(sleeps).containsExactly(12_000L);
    }

    @Test
    void aSecondRateLimitGivesUpAndLaterAnswersGetTemplateTextWithoutAnyCallOrWait() {
        transport.script.addAll(List.of(new AiFailure(AiFailure.Kind.RATE_LIMIT, 3), new AiFailure(AiFailure.Kind.RATE_LIMIT, 3)));
        assertThat(gemma.feedback("p1", false)).isEmpty(); // the caller writes the template
        assertThat(sleeps).containsExactly(3_000L); // waited once
        assertThat(gemma.feedback("p2", false)).isEmpty();
        assertThat(gemma.report("p3")).isEmpty();
        assertThat(transport.prompts).hasSize(2); // Gemma is on cooldown: no further call, no gap sleep
        assertThat(sleeps).hasSize(1);
    }

    @Test
    void aRateLimitWithoutRetryAfterAndUnusableRepliesFallBackAtOnce() {
        transport.script.add(new AiFailure(AiFailure.Kind.RATE_LIMIT));
        assertThat(gemma.feedback("p1", false)).isEmpty();
        assertThat(sleeps).isEmpty();

        Fake t2 = new Fake();
        t2.script.addAll(List.of("{\"text\":\"\",\"whatToFix\":\"x\"}", "not json")); // blank text, then the retry is not JSON
        GemmaFeedback g2 = new GemmaFeedback(new AiClient(t2, new AiGate(Clock.systemUTC()), JsonMapper.builder().build(), 20, 40), sleeps::add, 7_000);
        assertThat(g2.feedback("p", false)).isEmpty();
    }

    @Test
    void thePromptCarriesTheQuestionAnswerChecklistFactsAndStatsButNoMarkToChange() {
        String prompt = GemmaFeedback.feedbackPrompt("Why Redis?", "Because it is fast.",
                List.of(new Grading.Tick("explains_why", "Explains why", true, "Because it is fast.")), List.of("Added a cache"), java.util.Map.of("wordsPerMinute", 142));
        assertThat(prompt).contains("Why Redis?").contains("Because it is fast.").contains("yes: Explains why").contains("Added a cache").contains("wordsPerMinute").contains("do not change");
    }
}
