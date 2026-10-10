-- V3: narrow the starter roles from 42 to 11 (prd change log, 4 October 2026). Aliases and role skills go with the role (ON DELETE CASCADE).
-- A role a resume targets is kept: resume.target_role_id cascades, so deleting it would delete that resume and its sessions.
-- Skills stay: they still match skills on a resume, and bank_question rows may point at them.

DELETE FROM role
WHERE owner_user_id IS NULL
  AND name NOT IN ('Software Engineer', 'Backend Developer', 'Frontend Developer', 'Full Stack Developer',
                   'Data Analyst', 'Data Scientist', 'Data Engineer', 'Machine Learning Engineer', 'AI Engineer',
                   'DevOps Engineer', 'Cloud Engineer')
  AND id NOT IN (SELECT target_role_id FROM resume);
