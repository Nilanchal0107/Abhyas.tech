-- A user's performance on a bank question follows the user across resumes (ticket 18).
-- question keeps its own columns for project questions; for a question with bank_question_id these rows are the truth.
CREATE TABLE bank_performance (
  user_id          BIGINT NOT NULL,
  bank_question_id BIGINT NOT NULL,
  mastered         BOOLEAN NOT NULL DEFAULT FALSE,
  last_score       DECIMAL(4,1) NULL,
  last_asked_at    DATETIME(3) NULL,
  times_asked      INT NOT NULL DEFAULT 0,
  PRIMARY KEY (user_id, bank_question_id),
  CONSTRAINT fk_bank_perf_user FOREIGN KEY (user_id)          REFERENCES app_user (id)      ON DELETE CASCADE,
  CONSTRAINT fk_bank_perf_bank FOREIGN KEY (bank_question_id) REFERENCES bank_question (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Carry over what resumes already recorded. Where one user asked the same bank question on several resumes,
-- keep the best result: mastered if any copy is, the highest score, the latest ask, the total count.
INSERT INTO bank_performance (user_id, bank_question_id, mastered, last_score, last_asked_at, times_asked)
SELECT r.user_id, q.bank_question_id, MAX(q.mastered), MAX(q.last_score), MAX(q.last_asked_at), SUM(q.times_asked)
FROM question q JOIN resume r ON r.id = q.resume_id
WHERE q.bank_question_id IS NOT NULL AND q.times_asked > 0
GROUP BY r.user_id, q.bank_question_id;
