-- V7: ticket 13. A session is a quiz (19 questions) or an interview (6 spoken questions, each with one AI follow-up).
ALTER TABLE practice_session ADD COLUMN mode ENUM('quiz','interview') NOT NULL DEFAULT 'quiz' AFTER status;
ALTER TABLE attempt
  ADD COLUMN follow_up_text   TEXT NULL AFTER voice_stats,
  ADD COLUMN follow_up_answer TEXT NULL AFTER follow_up_text;
