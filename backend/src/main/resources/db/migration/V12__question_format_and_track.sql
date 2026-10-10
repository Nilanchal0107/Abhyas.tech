-- V12: question formats and role tracks (sde-focus tickets 09 to 11).
-- format: 'design' is an interview-only spoken design question graded on five areas; 'code' is a brief question answered in a code box.
-- track: a role flavour. NULL suits every role; 'backend' and 'frontend' are picked by the resume's target role.
ALTER TABLE bank_question
  ADD COLUMN format ENUM('standard','design','code') NOT NULL DEFAULT 'standard',
  ADD COLUMN track  ENUM('backend','frontend') NULL;
ALTER TABLE question
  ADD COLUMN format ENUM('standard','design','code') NOT NULL DEFAULT 'standard';
