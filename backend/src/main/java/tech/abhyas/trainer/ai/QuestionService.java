package tech.abhyas.trainer.ai;

import java.util.Optional;
import tools.jackson.databind.JsonNode;

/** Questions, key facts and role skill drafts (trd 7.5). AI is Groq, fallback is BankQuestionService. Stub: signatures firm up in the Questions ticket. */
public interface QuestionService {
    Optional<JsonNode> generateQuestions(String prompt);
    Optional<JsonNode> extractKeyFacts(String prompt);
    Optional<JsonNode> draftRoleSkills(String prompt);
}
