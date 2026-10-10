-- V6: ticket 10. Key facts now live on the question itself (bank rows and project questions), so the per-point table and worker columns go.
DROP TABLE IF EXISTS key_fact;
-- fk_resume_point_resume needs an index on resume_id once ix_resume_point_status (resume_id, ...) goes
ALTER TABLE resume_point
  ADD KEY ix_resume_point_resume (resume_id),
  DROP INDEX ix_resume_point_status,
  DROP COLUMN key_facts_status,
  DROP COLUMN key_facts_tries,
  DROP COLUMN last_try_at;
