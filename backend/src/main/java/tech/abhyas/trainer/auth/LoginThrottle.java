package tech.abhyas.trainer.auth;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Five failed logins block that login ID for five minutes (trd 6.2). Resets on restart. */
@Component
public class LoginThrottle {
    static final int MAX_FAILURES = 5;
    static final Duration BLOCK = Duration.ofMinutes(5);

    private record State(int failures, Instant blockedUntil) {}

    // ponytail: entries for IDs that never log in again stay until restart; add a sweep if memory ever matters
    private final Map<String, State> states = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginThrottle(Clock clock) { this.clock = clock; }

    /** Seconds left of a block, or 0 when this login ID may try. */
    public long retryAfterSeconds(String loginId) {
        String key = key(loginId);
        State s = states.get(key);
        if (s == null || s.blockedUntil() == null) return 0;
        long ms = Duration.between(clock.instant(), s.blockedUntil()).toMillis();
        if (ms <= 0) {
            states.remove(key, s);
            return 0;
        }
        return (ms + 999) / 1000;
    }

    public void failed(String loginId) {
        states.merge(key(loginId), new State(1, null), (old, one) -> {
            if (old.blockedUntil() != null && old.blockedUntil().isAfter(clock.instant())) return old;
            int n = old.blockedUntil() == null ? old.failures() + 1 : 1; // a lapsed block starts a fresh count
            return n >= MAX_FAILURES ? new State(n, clock.instant().plus(BLOCK)) : new State(n, null);
        });
    }

    public void succeeded(String loginId) { states.remove(key(loginId)); }

    // login IDs are case-insensitive, so "Bob" and "bob" share one counter
    private static String key(String loginId) { return loginId.toLowerCase(Locale.ROOT); }
}
