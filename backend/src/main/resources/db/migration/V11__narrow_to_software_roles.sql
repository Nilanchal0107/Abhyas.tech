-- V11: only the 4 software development roles stay (sde-focus ticket 03, 10 October 2026).
-- A resume that targets a removed role is deleted with it (resume.target_role_id cascades), with its questions and sessions.
DELETE FROM role
WHERE owner_user_id IS NULL
  AND name IN ('Data Analyst', 'Data Scientist', 'Data Engineer', 'Machine Learning Engineer', 'AI Engineer', 'DevOps Engineer', 'Cloud Engineer');

-- Practice bank rows of skills that no remaining role lists. Behavioral rows have no skill and stay.
-- Skills stay in `skill`, so they still match on a resume. bank_performance rows cascade; a user's question keeps its text.
DELETE FROM bank_question
WHERE skill_id IS NOT NULL
  AND skill_id NOT IN (SELECT skill_id FROM role_skill);
