package tech.abhyas.trainer.ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Removes emails and phone numbers from text before it leaves the server (trd 7.5, 7.6). */
public final class Scrubber {
    private static final Pattern EMAIL = Pattern.compile("[\\w.%+-]+@[\\w-]+(\\.[\\w-]+)+");
    private static final Pattern PHONE = Pattern.compile("(?<!\\w)\\+?\\d[\\d\\s().-]{6,}\\d");

    private Scrubber() {}

    /** First email in the text, or null. For filling the user's own profile locally, never for the AI. */
    public static String firstEmail(String text) {
        Matcher m = EMAIL.matcher(text);
        return m.find() ? m.group() : null;
    }

    /** First phone number in the text (9+ digits, same rule as {@link #scrub}), or null. */
    public static String firstPhone(String text) {
        Matcher m = PHONE.matcher(EMAIL.matcher(text).replaceAll(" "));
        while (m.find()) if (m.group().chars().filter(Character::isDigit).count() >= 9) return m.group();
        return null;
    }

    public static String scrub(String text) {
        String noEmail = EMAIL.matcher(text).replaceAll("[email removed]");
        // ponytail: 9+ digits counts as a phone so year ranges like 2020-2023 survive; a long ID number would also go
        return PHONE.matcher(noEmail).replaceAll(m -> m.group().chars().filter(Character::isDigit).count() >= 9
                ? "[phone removed]" : Matcher.quoteReplacement(m.group()));
    }
}
