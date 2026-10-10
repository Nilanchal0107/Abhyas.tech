-- The interview date is removed (sde-focus ticket 02). It was stored and never used.
ALTER TABLE resume DROP COLUMN interview_date;
