package tech.abhyas.trainer.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** Words, fillers, pace and duration are counted by Java from the transcript (FR-15). */
class VoiceStatsTest {
    @Test
    void countsWordsFillersAndPace() {
        VoiceStats s = VoiceStats.of("Um, at Acme we, uh, had two weeks. You know, I mean, it was tight.", 30);
        assertThat(s.wordCount()).isEqualTo(15);
        assertThat(s.fillerCount()).isEqualTo(4); // um, uh, you know, i mean
        assertThat(s.durationSeconds()).isEqualTo(30);
        assertThat(s.wordsPerMinute()).isEqualTo(30); // 15 words in half a minute
    }

    @Test
    void fillersAreWholeWordsOnly() {
        assertThat(VoiceStats.of("The umbrella caused an error in the hummus", 10).fillerCount()).isZero();
        assertThat(VoiceStats.of("Hmm... UH-huh, ERM", 10).fillerCount()).isEqualTo(3);
    }

    @Test
    void emptyTranscriptAndZeroDurationGiveZeros() {
        assertThat(VoiceStats.of("", 12.4)).isEqualTo(new VoiceStats(0, 0, 0, 12));
        assertThat(VoiceStats.of("one two three", 0).wordsPerMinute()).isZero();
        assertThat(VoiceStats.of(null, 5).wordCount()).isZero();
    }
}
