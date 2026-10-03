package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import tech.abhyas.trainer.session.Grading.Tick;

/** The fallback grader (ticket 22): ticks by keyword and pattern, always with a quote cut from the answer itself. */
class KeywordGraderTest {
    static final List<String> FACTS = List.of("Added a Redis cache in front of the database", "Cache entries expire after a time to live", "Cut repeated reads");
    static final String POINT = "Reduced API response time by 40% with a Redis cache";
    static final String QUESTION = "How did you reduce the API response time at Acme?";

    private static List<Tick> grade(String answer, boolean voice, boolean behavioral) {
        return KeywordGrader.grade(answer, Grading.itemKeys(voice, behavioral), FACTS, List.of(), POINT, QUESTION);
    }

    @Test
    void ticksFactsAndWhyWithTheMatchingSentenceAsTheQuote() {
        String answer = "I added a Redis cache in front of the database. I chose it because the same reads repeated all day. We saw the response time drop by 40%.";
        List<Tick> ticks = grade(answer, false, false);
        assertThat(ticks).hasSize(5);
        assertThat(ticks).filteredOn(Tick::passed).extracting(Tick::item).contains("key_fact_1", "explains_why", "consistent_with_resume");
        for (Tick t : ticks.stream().filter(Tick::passed).toList()) assertThat(Grading.norm(answer)).contains(Grading.norm(t.quote())); // the evidence rule holds
        assertThat(ticks).filteredOn(t -> !t.passed()).allMatch(t -> t.quote() == null);
        assertThat(ticks.get(1).passed()).isFalse(); // the time-to-live fact is not in the answer
    }

    @Test
    void anAnswerWithNoFactsTicksNothing() {
        assertThat(Grading.score(grade("I do not know.", false, false))).isZero();
        assertThat(Grading.score(grade("", false, false))).isZero();
        assertThat(Grading.score(grade("It was fine and everyone liked it.", false, false))).isZero();
    }

    @Test
    void behavioralItemsUseCueWordsAndIVersusWe() {
        String answer = "When our checkout broke before the launch, I needed to find the cause fast. I rewrote the retry logic and added a test. "
                + "The result was 30% fewer failed payments. I learned to test the failure paths first.";
        assertThat(grade(answer, false, true)).allMatch(Tick::passed).hasSize(5);
        String weAnswer = "We fixed it. We shipped a patch. We were happy.";
        assertThat(grade(weAnswer, false, true)).filteredOn(t -> t.item().equals("own_role")).allMatch(t -> !t.passed());
    }

    @Test
    void recordedBehavioralAnswersHaveTenItemsAndFillerHeavyOnesAreNotClear() {
        String clear = "At my last job the release was late and the team was stressed. I needed to cut scope. I built a smaller plan and shared it with 4 people. "
                + "The result was that we shipped 2 days early. I learned that scope is the lever. This is what I did about the deadline pressure.";
        assertThat(grade(clear, true, true)).hasSize(10);
        String filler = "um um um um um um um um um um um um um um um um um um um um um um um um um um um um um um um um um um";
        assertThat(grade(filler, true, true)).filteredOn(t -> t.item().equals("clear_and_focused")).allMatch(t -> !t.passed());
    }

    @Test
    void sentencesAreCutFromTheAnswerItself() {
        assertThat(KeywordGrader.sentences("One. Two!\nThree? Four")).containsExactly("One.", "Two!", "Three?", "Four");
        assertThat(KeywordGrader.significant("The Redis cache, and a TTL.")).contains("redis", "cache", "ttl").doesNotContain("the", "and");
    }
}
