package tech.abhyas.trainer.ai;

import java.util.Optional;
import tools.jackson.databind.JsonNode;

/** Per-answer feedback and the session report (trd 7.5). AI is Gemma, fallback is TemplateFeedback. Stub: signatures firm up in the Feedback ticket. */
public interface FeedbackService {
    Optional<JsonNode> writeFeedback(String prompt);
    Optional<JsonNode> writeReport(String prompt);
}
