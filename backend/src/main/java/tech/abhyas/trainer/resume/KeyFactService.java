package tech.abhyas.trainer.resume;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import tech.abhyas.trainer.ai.AiClient;
import tech.abhyas.trainer.ai.Provider;
import tools.jackson.databind.JsonNode;

/**
 * Drafts the key facts of each resume point with AI, one point at a time (FR-8, trd 7.1). A failure leaves the point
 * {@code pending}, counts the try, and keeps it out of the next passes for {@link #RETRY_DELAY}. Nothing is ever shown
 * to the user, and nothing about the resume or the AI reply is logged (trd 6.5).
 */
@Service
public class KeyFactService {
    static final Duration RETRY_DELAY = Duration.ofMinutes(10);
    static final int MAX_FACTS = 6, MAX_FACT_LENGTH = 500;
    private static final Logger log = LoggerFactory.getLogger(KeyFactService.class);

    private final AiClient ai;
    private final NamedParameterJdbcTemplate jdbc;
    private final TransactionTemplate tx;
    private final Clock clock;
    // one worker: points are drafted strictly one at a time across all users (free AI limits)
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "key-facts");
        t.setDaemon(true);
        return t;
    });
    private final Set<Long> queued = ConcurrentHashMap.newKeySet(); // resume ids waiting or running, so a double click does not queue twice

    public KeyFactService(AiClient ai, NamedParameterJdbcTemplate jdbc, TransactionTemplate tx, Clock clock) {
        this.ai = ai;
        this.jdbc = jdbc;
        this.tx = tx;
        this.clock = clock;
    }

    /** After a save: draft this resume's points in the background. */
    public void draftInBackground(long resumeId) {
        if (queued.add(resumeId)) worker.execute(() -> {
            try { draftPending(resumeId); } finally { queued.remove(resumeId); }
        });
    }

    /** On opening the Resume list: retry the user's pending points in the background. */
    public void retryInBackground(long userId) {
        for (long id : jdbc.queryForList("SELECT DISTINCT p.resume_id FROM resume_point p JOIN resume r ON r.id = p.resume_id WHERE r.user_id = :me AND p.key_facts_status = 'pending'",
                Map.of("me", userId), Long.class)) draftInBackground(id);
    }

    /**
     * One pass over the pending points of a resume that are due, in order. Stops at the first failure: AI is most
     * likely down, and the rest wait for the next pass without their try count going up. Returns how many became ready.
     */
    public int draftPending(long resumeId) {
        LocalDateTime now = LocalDateTime.now(clock.withZone(ZoneOffset.UTC));
        List<Map<String, Object>> due = jdbc.queryForList("""
                SELECT p.id, p.text, r.parsed_json FROM resume_point p JOIN resume r ON r.id = p.resume_id
                WHERE p.resume_id = :r AND p.key_facts_status = 'pending' AND (p.last_try_at IS NULL OR p.last_try_at <= :cutoff)
                ORDER BY p.position""", new MapSqlParameterSource("r", resumeId).addValue("cutoff", now.minus(RETRY_DELAY)));
        int ready = 0;
        for (Map<String, Object> row : due) {
            long pointId = ((Number) row.get("id")).longValue();
            try {
                Optional<List<String>> facts = ai.json(Provider.GROQ_CHAT, prompt((String) row.get("text"), String.valueOf(row.get("parsed_json"))), KeyFactService::valid)
                        .map(KeyFactService::facts);
                if (facts.isPresent()) {
                    tx.executeWithoutResult(s -> store(pointId, facts.get()));
                    ready++;
                    continue;
                }
            } catch (DataAccessException e) {
                log.warn("key facts: database error {}", e.getClass().getSimpleName()); // never the message: it can quote the data
            }
            jdbc.update("UPDATE resume_point SET key_facts_tries = LEAST(key_facts_tries + 1, 127), last_try_at = :now WHERE id = :p",
                    new MapSqlParameterSource("now", LocalDateTime.now(clock.withZone(ZoneOffset.UTC))).addValue("p", pointId));
            break;
        }
        return ready;
    }

    private void store(long pointId, List<String> facts) {
        jdbc.update("DELETE FROM key_fact WHERE resume_point_id = :p", Map.of("p", pointId));
        for (int i = 0; i < facts.size(); i++) {
            jdbc.update("INSERT INTO key_fact (resume_point_id, position, fact_text) VALUES (:p, :pos, :t)",
                    Map.of("p", pointId, "pos", i + 1, "t", facts.get(i)));
        }
        jdbc.update("UPDATE resume_point SET key_facts_status = 'ready' WHERE id = :p", Map.of("p", pointId));
    }

    private static String prompt(String point, String resumeJson) {
        return "You prepare an answer key for an interview practice app. From the resume below, list 3 to " + MAX_FACTS
                + " short, true facts that a good answer about this resume point should mention. Use only what the resume supports; do not invent numbers or tools.\n"
                + "Resume point: " + point + "\nResume JSON: " + resumeJson + "\nReply as {\"keyFacts\":[\"...\"]}.";
    }

    static boolean valid(JsonNode reply) {
        return !facts(reply).isEmpty();
    }

    /** The cleaned facts: trimmed, blanks and overlong ones dropped, at most {@link #MAX_FACTS}. */
    static List<String> facts(JsonNode reply) {
        List<String> out = new ArrayList<>();
        JsonNode list = reply.path("keyFacts");
        if (!list.isArray()) return out;
        for (JsonNode n : list) {
            String t = n.isString() ? n.asString().strip() : "";
            if (!t.isEmpty() && t.length() <= MAX_FACT_LENGTH && out.size() < MAX_FACTS) out.add(t);
        }
        return out;
    }
}
