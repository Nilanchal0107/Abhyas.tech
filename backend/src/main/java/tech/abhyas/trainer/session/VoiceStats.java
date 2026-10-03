package tech.abhyas.trainer.session;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/** Delivery numbers of a voice answer, counted by Java from the transcript and the audio length (FR-15, trd 7.3). */
public record VoiceStats(int wordsPerMinute, int fillerCount, int wordCount, int durationSeconds) {
    private static final Pattern WORD = Pattern.compile("[\\p{L}\\p{N}][\\p{L}\\p{N}'’-]*");
    // whole tokens only, so "error" and "umbrella" do not count; "like" is left out because it is usually a real word
    private static final Pattern FILLER = Pattern.compile(
            "(?<![\\p{L}\\p{N}])(?:um+|uh+|uhm+|er+m?|ah+|hmm+|you know|i mean)(?![\\p{L}\\p{N}])", Pattern.CASE_INSENSITIVE);

    public static VoiceStats of(String transcript, double seconds) {
        String text = transcript == null ? "" : transcript;
        int words = (int) WORD.matcher(text).results().count();
        int fillers = (int) FILLER.matcher(text).results().count();
        int wpm = seconds <= 0 ? 0 : (int) Math.round(words * 60.0 / seconds);
        return new VoiceStats(wpm, fillers, words, (int) Math.round(seconds));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("wordsPerMinute", wordsPerMinute);
        m.put("fillerCount", fillerCount);
        m.put("wordCount", wordCount);
        m.put("durationSeconds", durationSeconds);
        return m;
    }
}
