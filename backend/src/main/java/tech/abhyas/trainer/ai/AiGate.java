package tech.abhyas.trainer.ai;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** Marks a provider unavailable after a limit reply: 15 minutes, or until UTC midnight (trd 7.5). Resets on restart. */
@Component
public class AiGate {
    static final Duration RATE_LIMIT_COOLDOWN = Duration.ofMinutes(15);

    private final Map<Provider, Instant> unavailableUntil = new ConcurrentHashMap<>();
    private final Clock clock;

    public AiGate(Clock clock) { this.clock = clock; }

    public boolean available(Provider p) {
        Instant until = unavailableUntil.get(p);
        return until == null || !clock.instant().isBefore(until);
    }

    public void rateLimited(Provider p) { unavailableUntil.put(p, clock.instant().plus(RATE_LIMIT_COOLDOWN)); }

    public void dailyLimited(Provider p) {
        LocalDate tomorrow = LocalDate.now(clock.withZone(ZoneOffset.UTC)).plusDays(1);
        unavailableUntil.put(p, tomorrow.atStartOfDay().toInstant(ZoneOffset.UTC));
    }
}
