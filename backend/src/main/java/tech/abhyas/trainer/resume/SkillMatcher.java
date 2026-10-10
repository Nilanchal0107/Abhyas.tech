package tech.abhyas.trainer.resume;

import java.util.regex.Pattern;

/** Finds a skill name or alias in resume text as a whole word (skill-bank ticket 04). */
final class SkillMatcher {
    private SkillMatcher() {}

    /**
     * Whole word, ignoring case: "Java" is not in "JavaScript", "SQL" is not in "MySQL", "Go" is not in "Google".
     * A term ending in a letter or digit is also not followed by + or #, so "C" is not in "C++" or "C#".
     * ponytail: terms of 1 or 2 characters (R, Go, JS) match case-sensitively, so a lower-case "go to market" is no hit;
     * a capitalised "Go to market" still is. Add context rules if that shows up.
     */
    static Pattern of(String term) {
        String t = term.strip();
        boolean wordEnd = Character.isLetterOrDigit(t.charAt(t.length() - 1));
        return Pattern.compile("(?<![\\p{L}\\p{N}])" + Pattern.quote(t) + (wordEnd ? "(?![\\p{L}\\p{N}+#])" : "(?![\\p{L}\\p{N}])"),
                t.length() <= 2 ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    static boolean found(String text, String term) { return of(term).matcher(text).find(); }
}
