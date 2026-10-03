package tech.abhyas.trainer.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

class AiClientTest {
    private static final String OK = "{\"ok\":true}";

    /** Fake provider: plays scripted replies (a String is a reply, an AiFailure.Kind is a thrown failure). */
    static class FakeTransport implements AiTransport {
        final Queue<Object> script = new ArrayDeque<>();
        final List<String> prompts = new ArrayList<>();
        final List<Duration> timeouts = new ArrayList<>();

        FakeTransport then(Object... steps) { script.addAll(List.of(steps)); return this; }

        @Override public String send(Provider p, String prompt, Duration timeout) { return next(prompt, timeout); }
        @Override public String transcribe(byte[] audio, Duration timeout) { return next("<audio>", timeout); }

        private String next(String prompt, Duration timeout) {
            prompts.add(prompt);
            timeouts.add(timeout);
            Object step = script.remove();
            if (step instanceof AiFailure.Kind k) throw new AiFailure(k);
            return (String) step;
        }
    }

    static class MutableClock extends Clock {
        final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-10-03T10:00:00Z"));
        @Override public ZoneOffset getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return now.get(); }
    }

    FakeTransport fake;
    MutableClock clock;
    AiClient client;
    ListAppender<ILoggingEvent> logs;

    @BeforeEach
    void setUp() {
        fake = new FakeTransport();
        clock = new MutableClock();
        client = new AiClient(fake, new AiGate(clock), JsonMapper.builder().build(), 20, 40);
        logs = new ListAppender<>();
        logs.start();
        ((Logger) LoggerFactory.getLogger(AiClient.class)).addAppender(logs);
    }

    private boolean ok(Provider p) { return client.json(p, "hi", n -> n.path("ok").asBoolean()).isPresent(); }

    @Test
    void scrubberRemovesEmailAndPhoneButKeepsYears() {
        String out = Scrubber.scrub("Mail a.b+c@mail.example.com or call +91 98765 43210 / (555) 123-4567. Worked 2020-2023.");
        assertFalse(out.contains("@"));
        assertFalse(out.contains("98765"));
        assertFalse(out.contains("123-4567"));
        assertTrue(out.contains("2020-2023"));
    }

    @Test
    void promptIsScrubbedAndAsksForJsonWithTimeouts() {
        fake.then(OK, OK);
        client.json(Provider.GROQ_CHAT, "contact me@x.com", n -> true);
        client.transcribe(new byte[] {1}, n -> true);
        assertFalse(fake.prompts.get(0).contains("me@x.com"));
        assertTrue(fake.prompts.get(0).contains("JSON"));
        assertEquals(List.of(Duration.ofSeconds(20), Duration.ofSeconds(40)), fake.timeouts);
    }

    @Test
    void requestBodiesUseTemperatureZero() {
        var json = JsonMapper.builder().build();
        assertTrue(new String(HttpAiTransport.groqBody(json, "m", "p")).contains("\"temperature\":0"));
        assertTrue(new String(HttpAiTransport.gemmaBody(json, "p")).contains("\"temperature\":0"));
    }

    @Test
    void retriesOnceOnServerErrorTimeoutAndInvalidJson() {
        fake.then(AiFailure.Kind.SERVER_ERROR, OK);
        assertTrue(ok(Provider.GROQ_CHAT));
        fake.then(AiFailure.Kind.TIMEOUT, OK);
        assertTrue(ok(Provider.GROQ_CHAT));
        fake.then("not json", "```json\n{\"ok\":true}\n```");
        assertTrue(ok(Provider.GROQ_CHAT));
        assertEquals(6, fake.prompts.size());
    }

    @Test
    void givesUpAfterOneRetry() {
        fake.then(AiFailure.Kind.SERVER_ERROR, AiFailure.Kind.SERVER_ERROR);
        assertFalse(ok(Provider.GROQ_CHAT));
        fake.then("{\"ok\":false}", "{\"ok\":false}"); // parses, fails validation
        assertFalse(ok(Provider.GROQ_CHAT));
        assertEquals(4, fake.prompts.size());
    }

    @Test
    void rateLimitCoolsDownFor15MinutesWithoutCalling() {
        fake.then(AiFailure.Kind.RATE_LIMIT);
        assertFalse(ok(Provider.GEMMA));
        assertFalse(ok(Provider.GEMMA)); // script is empty: a call would throw
        assertEquals(1, fake.prompts.size());
        fake.then(OK);
        assertTrue(ok(Provider.GROQ_CHAT)); // other providers are unaffected
        clock.now.set(clock.instant().plus(Duration.ofMinutes(15)));
        fake.then(OK);
        assertTrue(ok(Provider.GEMMA));
    }

    @Test
    void dailyLimitLastsUntilUtcMidnight() {
        fake.then(AiFailure.Kind.DAILY_LIMIT);
        assertFalse(ok(Provider.GROQ_CHAT));
        clock.now.set(Instant.parse("2026-10-03T23:59:59Z"));
        assertFalse(ok(Provider.GROQ_CHAT));
        clock.now.set(Instant.parse("2026-10-04T00:00:00Z"));
        fake.then(OK);
        assertTrue(ok(Provider.GROQ_CHAT));
    }

    @Test
    void nothingFromTheRequestOrReplyReachesTheLog() {
        String secret = "SECRET-RESUME-TEXT";
        fake.then("{\"echo\":\"" + secret + "\"}", "garbage " + secret, AiFailure.Kind.SERVER_ERROR, "garbage " + secret, "garbage " + secret);
        client.json(Provider.GROQ_CHAT, secret, n -> true);
        client.json(Provider.GROQ_CHAT, secret, n -> n.has("none"));
        client.json(Provider.GROQ_CHAT, secret, n -> true);
        assertFalse(logs.list.isEmpty());
        for (ILoggingEvent e : logs.list) assertFalse(e.getFormattedMessage().contains(secret), e.getFormattedMessage());
    }
}
