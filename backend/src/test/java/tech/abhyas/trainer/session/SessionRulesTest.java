package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import tech.abhyas.trainer.common.ApiException;
import tech.abhyas.trainer.session.SessionService.Candidate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/** Slot selection, generated-question validation and answer shape for the multiple-correct part. */
class SessionRulesTest {
    static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 3, 12, 0);
    static final JsonMapper JSON = JsonMapper.builder().build();

    private static Candidate c(long id, boolean mastered, int asked, Double score, LocalDateTime last) { return new Candidate(id, mastered, asked, score, last, false); }

    @Test
    void unmasteredComeFirstLowestScoreThenOldest() {
        List<Candidate> all = List.of(
                c(1, false, 1, 1.5, NOW.minusDays(1)), c(2, false, 2, null, NOW.minusDays(2)), c(3, false, 1, 0.0, NOW.minusDays(9)),
                c(4, false, 0, null, null), c(5, true, 3, 2.0, NOW.minusDays(3)));
        // no score counts as 0, so 3 (older) comes before 2, then 1; the new one fills the last slot, the recent mastered one never does
        assertThat(SessionService.pick(all, 4, NOW, new Random(1))).containsExactly(3L, 2L, 1L, 4L);
    }

    @Test
    void restedMasteredQuestionsJoinTheRandomPoolButRecentOnesDoNot() {
        List<Candidate> all = List.of(c(1, true, 1, 2.0, NOW.minusMonths(2)), c(2, true, 1, 2.0, NOW.minusDays(5)), c(3, false, 0, null, null));
        assertThat(SessionService.pick(all, 5, NOW, new Random(1))).containsExactlyInAnyOrder(1L, 3L);
    }

    @Test
    void tooManyUnmasteredAreCutAtTheSlotCount() {
        List<Candidate> all = List.of(c(1, false, 1, 1.0, NOW), c(2, false, 1, 0.5, NOW), c(3, false, 1, 1.5, NOW));
        assertThat(SessionService.pick(all, 2, NOW, new Random(1))).containsExactly(2L, 1L);
    }

    private static List<SessionService.Generated> gen(String json) { return SessionService.validGenerated(JSON.readTree(json), Set.of(7L)); }

    @Test
    void generatedQuestionsNeedFourOptionsACorrectKeyAndALinkedPoint() {
        String ok = "{\"resumePointId\":7,\"text\":\"Q?\",\"options\":[\"a\",\"b\",\"c\",\"d\"],\"correct\":[\"A\",\"c\"]}";
        assertThat(gen("{\"questions\":[" + ok + "]}")).hasSize(1).first().satisfies(g -> assertThat(g.correct()).containsExactly("A", "C"));
        for (String bad : List.of(
                "{\"resumePointId\":8,\"text\":\"Q?\",\"options\":[\"a\",\"b\",\"c\",\"d\"],\"correct\":[\"A\"]}", // point not on this resume
                "{\"resumePointId\":7,\"text\":\"Q?\",\"options\":[\"a\",\"b\",\"c\"],\"correct\":[\"A\"]}", // 3 options
                "{\"resumePointId\":7,\"text\":\"Q?\",\"options\":[\"a\",\"b\",\"c\",\"d\"],\"correct\":[]}", // none correct
                "{\"resumePointId\":7,\"text\":\"Q?\",\"options\":[\"a\",\"b\",\"c\",\"d\"],\"correct\":[\"E\"]}", // key out of range
                "{\"resumePointId\":7,\"text\":\"Q?\",\"options\":[\"a\",\"a\",\"c\",\"d\"],\"correct\":[\"A\"]}", // repeated option
                "{\"resumePointId\":7,\"text\":\" \",\"options\":[\"a\",\"b\",\"c\",\"d\"],\"correct\":[\"A\"]}")) {
            assertThat(gen("{\"questions\":[" + bad + "," + ok + "]}")).as(bad).hasSize(1); // the bad one is dropped, the good one kept
        }
        assertThat(gen("{\"questions\":\"nope\"}")).isEmpty();
    }

    @Test
    void answersAreOptionKeysFromTheQuestion() {
        assertThat(SessionService.selectedOptions(JSON.readTree("{\"selectedOptions\":[\"A\",\"C\",\"A\"]}"), 4)).containsExactly("A", "C");
        assertThat(SessionService.selectedOptions(null, 4)).isNull();
        assertThat(SessionService.selectedOptions(JSON.readTree("{\"selectedOptions\":[]}"), 4)).isNull();
        for (String bad : List.of("{\"selectedOptions\":[\"E\"]}", "{\"selectedOptions\":\"A\"}", "{\"text\":\"x\"}", "[\"A\"]")) {
            JsonNode n = JSON.readTree(bad);
            assertThatThrownBy(() -> SessionService.selectedOptions(n, 4)).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
    }

    private static List<SessionService.GeneratedBlank> blanks(String json) { return SessionService.validBlanks(JSON.readTree(json), Set.of(7L)); }

    @Test
    void generatedBlankQuestionsNeedOneOrTwoMarkedBlanksEachWithAcceptedAnswers() {
        String one = "{\"resumePointId\":7,\"text\":\"The ________ annotation marks a controller.\",\"accepted\":[[\"@RestController\",\"RestController\"]]}";
        String two = "{\"resumePointId\":7,\"text\":\"Use ________ to map and ________ to inject.\",\"accepted\":[[\"GetMapping\"],[\"Autowired\"]]}";
        assertThat(blanks("{\"questions\":[" + one + "," + two + "]}")).extracting(g -> g.accepted().size()).containsExactly(1, 2);
        for (String bad : List.of(
                "{\"resumePointId\":7,\"text\":\"No blank here.\",\"accepted\":[[\"x\"]]}", // no marked blank
                "{\"resumePointId\":7,\"text\":\"A ________ B ________ C ________\",\"accepted\":[[\"x\"],[\"y\"],[\"z\"]]}", // 3 blanks
                "{\"resumePointId\":7,\"text\":\"A ________ B ________\",\"accepted\":[[\"x\"]]}", // fewer answer lists than blanks
                "{\"resumePointId\":7,\"text\":\"A ________\",\"accepted\":[[]]}", // no accepted wording
                "{\"resumePointId\":7,\"text\":\"A ________\",\"accepted\":[[\" \"]]}",
                "{\"resumePointId\":9,\"text\":\"A ________\",\"accepted\":[[\"x\"]]}")) { // point not on this resume
            assertThat(blanks("{\"questions\":[" + bad + "," + one + "]}")).as(bad).hasSize(1);
        }
    }

    @Test
    void blankAnswersNeedOneEntryPerBlankUpTo200Characters() {
        assertThat(SessionService.blankAnswers(JSON.readTree("{\"blankAnswers\":[\"RestController\"]}"), 1)).containsExactly("RestController");
        assertThat(SessionService.blankAnswers(JSON.readTree("{\"blankAnswers\":[\"a\",\"\"]}"), 2)).containsExactly("a", ""); // one blank skipped
        assertThat(SessionService.blankAnswers(JSON.readTree("{\"blankAnswers\":[\" \",\"\"]}"), 2)).isNull(); // nothing typed clears it
        assertThat(SessionService.blankAnswers(null, 1)).isNull();
        for (String bad : List.of("{\"blankAnswers\":[\"a\",\"b\"]}", "{\"blankAnswers\":[]}", "{\"blankAnswers\":[1]}", "{\"text\":\"x\"}",
                "{\"blankAnswers\":[\"" + "x".repeat(201) + "\"]}")) {
            JsonNode n = JSON.readTree(bad);
            assertThatThrownBy(() -> SessionService.blankAnswers(n, 1)).as(bad).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
    }

    @Test
    void briefPartPicksFourTechnicalAndTwoBehavioralEachFromItsOwnPool() {
        List<Candidate> all = new java.util.ArrayList<>();
        for (long i = 1; i <= 6; i++) all.add(new Candidate(i, false, 0, null, null, false)); // 6 new technical
        all.add(new Candidate(10, false, 1, 0.0, NOW.minusDays(1), true)); // unmastered behavioral comes first
        all.add(new Candidate(11, false, 0, null, null, true));
        all.add(new Candidate(12, false, 0, null, null, true)); // a third behavioral does not fit
        List<List<Long>> got = SessionService.pickQuotas(all, SessionService.PARTS.get(2).quotas(), NOW, new Random(3));
        assertThat(got.get(0)).hasSize(4).allMatch(id -> id <= 6);
        assertThat(got.get(1)).hasSize(2).startsWith(10L).allMatch(id -> id >= 10);
        // behavioral questions never fill technical slots when technical ones are short
        List<List<Long>> short_ = SessionService.pickQuotas(List.of(new Candidate(10, false, 0, null, null, true)), SessionService.PARTS.get(2).quotas(), NOW, new Random(3));
        assertThat(short_.get(0)).isEmpty();
        assertThat(short_.get(1)).containsExactly(10L);
    }

    @Test
    void generatedBriefQuestionsKeepTheTechnicalOnesTiedToPointsWithKeyFactsAndBehavioralOnesToAKnownTheme() {
        String json = "{\"questions\":["
                + "{\"kind\":\"technical\",\"resumePointId\":1,\"text\":\"How did you measure it?\"},"            // ok
                + "{\"kind\":\"technical\",\"resumePointId\":2,\"text\":\"No key facts for point 2\"},"           // dropped
                + "{\"kind\":\"technical\",\"text\":\"No point\"},"                                                 // dropped
                + "{\"kind\":\"behavioral\",\"theme\":\"Teamwork\",\"resumePointId\":2,\"text\":\"A time you led?\"}," // ok, linked
                + "{\"kind\":\"behavioral\",\"theme\":\"conflict\",\"text\":\"A time you disagreed?\"},"        // ok, no point
                + "{\"kind\":\"behavioral\",\"theme\":\"luck\",\"text\":\"Bad theme\"},"                        // dropped
                + "{\"kind\":\"behavioral\",\"theme\":\"failure\",\"resumePointId\":99,\"text\":\"Unknown point\"}]}"; // dropped
        List<SessionService.GeneratedBrief> got = SessionService.validBrief(JSON.readTree(json), Set.of(1L), Set.of(1L, 2L));
        assertThat(got).extracting(SessionService.GeneratedBrief::text).containsExactly("How did you measure it?", "A time you led?", "A time you disagreed?");
        assertThat(got).extracting(SessionService.GeneratedBrief::theme).containsExactly(null, "teamwork", "conflict");
        assertThat(got).extracting(SessionService.GeneratedBrief::pointId).containsExactly(1L, 2L, null);
    }

    @Test
    void briefAnswersAreTextUpTo5000Characters() {
        assertThat(SessionService.briefText(JSON.readTree("{\"text\":\"I added an index.\"}"))).isEqualTo("I added an index.");
        assertThat(SessionService.briefText(JSON.readTree("{\"text\":\"" + "x".repeat(5000) + "\"}"))).hasSize(5000);
        assertThat(SessionService.briefText(JSON.readTree("{\"text\":\"  \"}"))).isNull();
        assertThat(SessionService.briefText(null)).isNull();
        for (String bad : List.of("{\"text\":\"" + "x".repeat(5001) + "\"}", "{\"text\":5}", "{\"blankAnswers\":[]}")) {
            JsonNode n = JSON.readTree(bad);
            assertThatThrownBy(() -> SessionService.briefText(n)).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
    }

    @Test
    void theVoicePartIsLeftOutWhenTranscriptionIsUnavailable() {
        assertThat(SessionService.partsFor(false)).extracting(SessionService.Part::type).containsExactly("multiple_correct", "fill_blank", "brief", "voice");
        assertThat(SessionService.partsFor(true)).extracting(SessionService.Part::type).containsExactly("multiple_correct", "fill_blank", "brief");
        int full = SessionService.partsFor(false).stream().mapToInt(p -> p.marks() * p.quotas().stream().mapToInt(SessionService.Quota::slots).sum()).sum();
        int skipped = SessionService.partsFor(true).stream().mapToInt(p -> p.marks() * p.quotas().stream().mapToInt(SessionService.Quota::slots).sum()).sum();
        assertThat(full).isEqualTo(80);
        assertThat(skipped).isEqualTo(50);
        assertThat(SessionService.partsFor(false).get(3).quotas()).extracting(SessionService.Quota::slots).containsExactly(1, 2); // 1 technical, 2 behavioral
    }

    @Test
    void aTypedVoiceAnswerIsTextOnlyAndARecordedOneNeedsTranscriptAndStats() {
        SessionService.VoiceAnswer typed = SessionService.voiceAnswer(JSON.readTree("{\"text\":\"I wrote it down.\",\"answeredVia\":\"typed\"}"));
        assertThat(typed.via()).isEqualTo("typed");
        assertThat(typed.text()).isEqualTo("I wrote it down.");
        assertThat(typed.stats()).isNull(); // no voice stats for a typed answer
        SessionService.VoiceAnswer rec = SessionService.voiceAnswer(JSON.readTree(
                "{\"text\":null,\"transcript\":\"At Acme we had two weeks\",\"voiceStats\":{\"wordsPerMinute\":142,\"fillerCount\":5,\"wordCount\":96,\"durationSeconds\":40},\"answeredVia\":\"voice\"}"));
        assertThat(rec.via()).isEqualTo("voice");
        assertThat(rec.transcript()).isEqualTo("At Acme we had two weeks");
        assertThat(JSON.readTree(rec.stats()).path("wordsPerMinute").asInt()).isEqualTo(142);
        assertThat(SessionService.voiceAnswer(null)).isNull();
        assertThat(SessionService.voiceAnswer(JSON.readTree("{\"text\":\" \",\"answeredVia\":\"typed\"}"))).isNull();
        for (String bad : List.of("{\"text\":\"x\"}", "{\"answeredVia\":\"click\"}", "{\"answeredVia\":\"voice\",\"transcript\":\"\",\"voiceStats\":{}}",
                "{\"answeredVia\":\"voice\",\"transcript\":\"hi\",\"voiceStats\":{\"wordsPerMinute\":1}}",
                "{\"answeredVia\":\"voice\",\"transcript\":\"hi\",\"voiceStats\":{\"wordsPerMinute\":-1,\"fillerCount\":0,\"wordCount\":1,\"durationSeconds\":1}}",
                "{\"answeredVia\":\"typed\",\"text\":\"" + "x".repeat(5001) + "\"}")) {
            JsonNode n = JSON.readTree(bad);
            assertThatThrownBy(() -> SessionService.voiceAnswer(n)).as(bad).isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.type()).isEqualTo("validation"));
        }
    }

    // ---- ticket 23: weak questions come back ----

    @Test
    void aMasteredQuestionReturnsOnlyAfterAMonthOfRest() {
        Candidate mastered = c(1, true, 3, 2.0, NOW);
        Candidate fresh = c(2, false, 0, null, null);
        // the clock moves forward: the day it was asked and 29 days later it stays out, from exactly one month on it may come back
        for (LocalDateTime now : List.of(NOW, NOW.plusDays(29), NOW.plusMonths(1).minusSeconds(1))) {
            assertThat(SessionService.pick(List.of(mastered, fresh), 5, now, new Random(1))).as(now.toString()).containsExactly(2L);
        }
        assertThat(SessionService.pick(List.of(mastered, fresh), 5, NOW.plusMonths(1), new Random(1))).containsExactlyInAnyOrder(1L, 2L);
        assertThat(SessionService.pick(List.of(mastered, fresh), 5, NOW.plusMonths(2), new Random(1))).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void anUnmasteredQuestionKeepsComingBackNoMatterHowRecentlyItWasAsked() {
        Candidate weak = c(1, false, 1, 1.0, NOW); // asked just now and still not mastered
        for (int days : List.of(0, 1, 30)) {
            assertThat(SessionService.pick(List.of(weak, c(2, false, 0, null, null)), 1, NOW.plusDays(days), new Random(days))).containsExactly(1L);
        }
    }

    @Test
    void anUnscoredQuestionCountsAsZeroSoItComesBackBeforeALowScoredOne() {
        // a brief answer graded only by the fallback has no score: it counts as 0 and is ahead of one that scored 1
        List<Candidate> all = List.of(c(1, false, 1, 1.0, NOW.minusDays(5)), c(2, false, 1, null, NOW.minusDays(1)));
        assertThat(SessionService.pick(all, 2, NOW, new Random(1))).containsExactly(2L, 1L);
    }

    @Test
    void theOtherSlotsAreFilledAtRandomFromNewAndRestedQuestionsOnly() {
        List<Candidate> all = List.of(c(1, false, 1, 0.0, NOW.minusDays(1)), c(2, false, 0, null, null), c(3, true, 2, 2.0, NOW.minusMonths(3)),
                c(4, true, 2, 2.0, NOW.minusDays(2)), c(5, false, 0, null, null));
        for (long seed = 0; seed < 20; seed++) {
            List<Long> got = SessionService.pick(all, 3, NOW, new Random(seed));
            assertThat(got).hasSize(3).startsWith(1L).doesNotContain(4L); // the unmastered one first, the recently mastered one never
        }
    }

    @Test
    void behavioralQuotasAreFilledByTheirOwnUnmasteredQuestionsFirst() {
        List<Candidate> all = List.of(new Candidate(1, false, 1, 0.5, NOW.minusDays(3), true), new Candidate(2, false, 0, null, null, true),
                new Candidate(3, false, 0, null, null, false), new Candidate(4, false, 1, 0.0, NOW.minusDays(1), false));
        List<List<Long>> got = SessionService.pickQuotas(all, List.of(new SessionService.Quota(false, 1), new SessionService.Quota(true, 1)), NOW, new Random(1));
        assertThat(got).containsExactly(List.of(4L), List.of(1L)); // technical 4 and behavioral 1 are the unmastered ones
    }
}
