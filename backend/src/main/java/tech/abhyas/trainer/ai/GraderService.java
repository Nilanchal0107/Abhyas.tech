package tech.abhyas.trainer.ai;

import java.util.Optional;
import tools.jackson.databind.JsonNode;

/** Checklists and the blank re-check (trd 7.5). AI is Groq, fallback is KeywordGrader. Stub: signatures firm up in the Feedback ticket. */
public interface GraderService {
    Optional<JsonNode> gradeChecklist(String prompt);
    Optional<JsonNode> recheckBlanks(String prompt);
}
