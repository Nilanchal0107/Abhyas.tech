package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import tech.abhyas.trainer.session.Grading.Scored;
import tech.abhyas.trainer.session.Grading.Tick;
import tools.jackson.databind.json.JsonMapper;

/** The mark formula, blank matching, the evidence rule, checklist lengths, mastery marks and the report numbers (tickets 18 to 20). */
class GradingTest {
    static final JsonMapper JSON = JsonMapper.builder().build();

    @Test
    void multipleCorrectMarksFollowTheFormulaAndRoundToHalves() {
        Set<String> ac = Set.of("A", "C");
        assertThat(Grading.multipleCorrectMarks(Set.of("A", "C"), ac)).isEqualTo(2.0); // both
        assertThat(Grading.multipleCorrectMarks(Set.of("A"), ac)).isEqualTo(1.0); // one correct of two
        assertThat(Grading.multipleCorrectMarks(Set.of("A", "B"), ac)).isEqualTo(0.0); // one right, one wrong
        assertThat(Grading.multipleCorrectMarks(Set.of("B", "D"), ac)).isEqualTo(0.0); // never below zero
        assertThat(Grading.multipleCorrectMarks(Set.of(), ac)).isEqualTo(0.0);
        assertThat(Grading.multipleCorrectMarks(Set.of("A", "B", "C"), ac)).isEqualTo(1.0); // two right, one wrong
        assertThat(Grading.multipleCorrectMarks(Set.of("A"), Set.of("A", "B", "C"))).isEqualTo(0.5); // 0.67 rounds to the nearest half
        assertThat(Grading.multipleCorrectMarks(Set.of("A", "B"), Set.of("A", "B", "C"))).isEqualTo(1.5); // 1.33 rounds to 1.5
    }

    @Test
    void blanksTrimAndIgnoreCaseAndAreWorthTwoMarksSplitAcrossBlanks() {
        assertThat(Grading.blankMatches("  restcontroller ", List.of("@RestController", "RestController"))).isTrue();
        assertThat(Grading.blankMatches("Controller", List.of("RestController"))).isFalse();
        assertThat(Grading.blankMatches("   ", List.of("x"))).isFalse();
        assertThat(Grading.blankMatches(null, List.of("x"))).isFalse();
        assertThat(Grading.marksPerBlank(1)).isEqualTo(2.0);
        assertThat(Grading.marksPerBlank(2)).isEqualTo(1.0);
    }

    private static final List<String> KEYS = List.of("key_fact_1", "explains_why");
    private static final String ANSWER = "I added a Redis cache in front of MySQL.\nIt cut the repeated reads.";

    @Test
    void aYesWithoutARealQuoteBecomesNoAndTheScoreIsCountedByJava() {
        var reply = JSON.readTree("""
                {"score": 5, "items": [
                  {"item": "key_fact_1", "passed": true, "quote": "  I ADDED a redis   cache in front of MySQL. "},
                  {"item": "explains_why", "passed": true, "quote": "Because reads were slow"}]}""");
        List<Tick> ticks = Grading.applyEvidence(reply, KEYS, List.of("Added a cache"), ANSWER);
        assertThat(ticks).extracting(Tick::passed).containsExactly(true, false); // the invented quote is flipped
        assertThat(ticks.get(0).quote()).containsIgnoringCase("redis"); // the quote is shown as the AI gave it, trimmed
        assertThat(ticks.get(0).label()).isEqualTo("Mentions: Added a cache");
        assertThat(ticks.get(1).quote()).isNull();
        assertThat(Grading.score(ticks)).isEqualTo(1); // the AI's own "score" is ignored
    }

    @Test
    void anAnswerWithNoFactsTicksNothingEvenWhenTheAiSaysYes() {
        var reply = JSON.readTree("""
                {"items": [{"item": "key_fact_1", "passed": true, "quote": "I added a Redis cache"},
                           {"item": "explains_why", "passed": true, "quote": ""}]}""");
        assertThat(Grading.score(Grading.applyEvidence(reply, KEYS, List.of(), "I do not remember."))).isZero();
        assertThat(Grading.score(Grading.applyEvidence(reply, KEYS, List.of(), ""))).isZero();
        assertThat(Grading.score(Grading.applyEvidence(JSON.readTree("{}"), KEYS, List.of(), ANSWER))).isZero(); // a reply with no items
    }

    @Test
    void checklistLengthEqualsTheMarksForEveryCase() {
        assertThat(Grading.itemKeys(false, false)).hasSize(5).contains("key_fact_3", "explains_why", "consistent_with_resume"); // brief technical, and a typed technical voice answer
        assertThat(Grading.itemKeys(false, true)).hasSize(5).containsExactly("situation_task", "action", "result", "own_role", "lesson_learned");
        assertThat(Grading.itemKeys(true, false)).hasSize(10).contains("key_fact_6", "concrete_example", "own_role");
        assertThat(Grading.itemKeys(true, true)).hasSize(10).contains("situation", "task", "concrete_detail", "answers_question", "clear_and_focused");
    }

    @Test
    void missingKeyFactsAreCoveredByAGenericCheckSoTheListStaysFiveOrTen() {
        assertThat(Grading.label("key_fact_2", List.of("a", "b"))).isEqualTo("Mentions: b");
        assertThat(Grading.label("key_fact_3", List.of("a", "b"))).isEqualTo(Grading.GENERIC_FACT);
        assertThat(Grading.label("explains_why", List.of())).isEqualTo("Explains why");
    }

    @Test
    void anIncompleteReplyIsNotAccepted() {
        assertThat(Grading.complete(JSON.readTree("{\"items\":[{\"item\":\"key_fact_1\"}]}"), KEYS)).isFalse();
        assertThat(Grading.complete(JSON.readTree("{\"items\":[{\"item\":\"key_fact_1\"},{\"item\":\"explains_why\"}]}"), KEYS)).isTrue();
    }

    @Test
    void masteryMarksAreTwoOfTwoFourOfFiveAndEightOfTen() {
        assertThat(Grading.mastered("multiple_correct", 2, 2)).isTrue();
        assertThat(Grading.mastered("multiple_correct", 2, 1.5)).isFalse();
        assertThat(Grading.mastered("fill_blank", 2, 1)).isFalse(); // two blanks, one right
        assertThat(Grading.mastered("brief", 5, 4)).isTrue();
        assertThat(Grading.mastered("brief", 5, 3)).isFalse();
        assertThat(Grading.mastered("voice", 10, 8)).isTrue();
        assertThat(Grading.mastered("voice", 10, 7)).isFalse();
        assertThat(Grading.mastered("voice", 5, 4)).isTrue(); // typed voice answer: 4 of 5
        assertThat(Grading.mastered("voice", 5, 3)).isFalse();
    }

    @Test
    void reportNumbersGroupByPartCountMasteredAndPickTheWeakestPoints() {
        List<Scored> all = List.of(new Scored("multiple_correct", 2, 2, 1L, "p1"), new Scored("multiple_correct", 2, 0, 2L, "p2"),
                new Scored("brief", 5, 2, 3L, "p3"), new Scored("brief", 5, 5, 4L, "p4"), new Scored("brief", 5, 1, 3L, "p3"), new Scored("brief", 5, 3, 5L, "p5"),
                new Scored("brief", 5, 0, null, null));
        Grading.Summary s = Grading.summarize(all);
        assertThat(s.byPart().get("multiple_correct")).containsExactly(2.0, 4.0);
        assertThat(s.byPart().get("brief")).containsExactly(11.0, 25.0);
        assertThat(s.byPart()).doesNotContainKey("voice"); // no voice row when the part was skipped
        assertThat(s.mastered()).isEqualTo(2);
        assertThat(s.weakest()).extracting(Scored::pointId).containsExactly(2L, 3L, 5L); // lowest ratio first, one row per point, none without a point
    }

    @Test
    void templateReportAndFeedbackUseCoachToneAndNeutralNumbers() {
        var report = TemplateFeedback.report(61, 80, 9, 19, List.of("REST APIs"));
        assertThat(report.get("headline")).isEqualTo("Good progress. A few points still need work.");
        assertThat(report.get("summary")).asString().contains("61 of 80 (76%)").contains("9 of 19").contains("REST APIs");
        var fb = TemplateFeedback.feedback(List.of(new Tick("explains_why", "Explains why", false, null), new Tick("result", "States the result", true, "It cut reads.")),
                List.of(), java.util.Map.of("wordsPerMinute", 142, "fillerCount", 5, "wordCount", 96, "durationSeconds", 40));
        assertThat(fb.get("text")).asString().startsWith("1 of 2 items covered.");
        assertThat(fb.get("whatToFix")).asString().contains("Explains why");
        assertThat(fb.get("deliveryComment")).asString().contains("142 words per minute").contains("Filler words: 5");
    }
}
