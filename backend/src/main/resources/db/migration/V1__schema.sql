-- Run as V1__schema.sql. Server and JDBC connection time zone must be UTC.

CREATE TABLE app_user (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  login_id      VARCHAR(50)  NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  created_at    DATETIME(3)  NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_app_user_login (login_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE skill (
  id         BIGINT NOT NULL AUTO_INCREMENT,
  name       VARCHAR(100) NOT NULL,
  created_at DATETIME(3)  NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_skill_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE role (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  name          VARCHAR(100) NOT NULL,
  category      VARCHAR(60)  NULL,
  owner_user_id BIGINT NULL,
  owner_key     BIGINT GENERATED ALWAYS AS (IFNULL(owner_user_id, 0)) VIRTUAL,
  created_at    DATETIME(3)  NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_role_owner_name (owner_key, name),
  CONSTRAINT fk_role_owner FOREIGN KEY (owner_user_id) REFERENCES app_user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE role_alias (
  id      BIGINT NOT NULL AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  alias   VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_role_alias (role_id, alias),
  KEY ix_role_alias_alias (alias),
  CONSTRAINT fk_role_alias_role FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE role_skill (
  role_id  BIGINT NOT NULL,
  skill_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, skill_id),
  KEY ix_role_skill_skill (skill_id),
  CONSTRAINT fk_role_skill_role  FOREIGN KEY (role_id)  REFERENCES role (id)  ON DELETE CASCADE,
  CONSTRAINT fk_role_skill_skill FOREIGN KEY (skill_id) REFERENCES skill (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE resume (
  id             BIGINT NOT NULL AUTO_INCREMENT,
  user_id        BIGINT NOT NULL,
  file_name      VARCHAR(255) NOT NULL,
  target_role_id BIGINT NOT NULL,
  parsed_json    JSON NOT NULL,
  parse_source   ENUM('openresume','ai_fallback') NOT NULL,
  interview_date DATE NULL,
  last_used_at   DATETIME(3) NULL,
  created_at     DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  KEY ix_resume_user_used (user_id, last_used_at),
  CONSTRAINT fk_resume_user FOREIGN KEY (user_id)        REFERENCES app_user (id) ON DELETE CASCADE,
  CONSTRAINT fk_resume_role FOREIGN KEY (target_role_id) REFERENCES role (id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE resume_point (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  resume_id        BIGINT NOT NULL,
  kind             ENUM('project_bullet','experience_bullet','skill') NOT NULL,
  source_ref       VARCHAR(120) NOT NULL,
  text             TEXT NOT NULL,
  position         INT NOT NULL,
  skill_id         BIGINT NULL,
  key_facts_status ENUM('pending','ready') NOT NULL DEFAULT 'pending',
  key_facts_tries  TINYINT NOT NULL DEFAULT 0,
  last_try_at      DATETIME(3) NULL,
  PRIMARY KEY (id),
  KEY ix_resume_point_status (resume_id, key_facts_status),
  KEY ix_resume_point_skill (skill_id),
  CONSTRAINT fk_resume_point_resume FOREIGN KEY (resume_id) REFERENCES resume (id) ON DELETE CASCADE,
  CONSTRAINT fk_resume_point_skill  FOREIGN KEY (skill_id)  REFERENCES skill (id)  ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE key_fact (
  id              BIGINT NOT NULL AUTO_INCREMENT,
  resume_point_id BIGINT NOT NULL,
  position        INT NOT NULL,
  fact_text       TEXT NOT NULL,
  PRIMARY KEY (id),
  KEY ix_key_fact_point (resume_point_id),
  CONSTRAINT fk_key_fact_point FOREIGN KEY (resume_point_id) REFERENCES resume_point (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE gap_result (
  resume_id   BIGINT NOT NULL,
  result      JSON NOT NULL,
  computed_at DATETIME(3) NOT NULL,
  PRIMARY KEY (resume_id),
  CONSTRAINT fk_gap_result_resume FOREIGN KEY (resume_id) REFERENCES resume (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE bank_question (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  type             ENUM('multiple_correct','fill_blank','brief','voice') NOT NULL,
  skill_id         BIGINT NULL,
  theme            ENUM('teamwork','conflict','failure','leadership','deadline_pressure') NULL,
  difficulty       ENUM('easy','medium','hard') NULL,
  text_template    TEXT NOT NULL,
  options          JSON NULL,
  correct_options  JSON NULL,
  accepted_answers JSON NULL,
  key_facts        JSON NULL,
  keywords         JSON NULL,
  source           ENUM('seed','ai') NOT NULL,
  provider         VARCHAR(30) NULL,
  text_hash        CHAR(64) NOT NULL,
  created_at       DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_bank_question_hash (text_hash),
  KEY ix_bank_question_skill (type, skill_id),
  KEY ix_bank_question_theme (type, theme),
  CONSTRAINT ck_bank_question_target CHECK (skill_id IS NOT NULL OR theme IS NOT NULL),
  CONSTRAINT fk_bank_question_skill FOREIGN KEY (skill_id) REFERENCES skill (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE question (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  resume_id        BIGINT NOT NULL,
  resume_point_id  BIGINT NULL,
  bank_question_id BIGINT NULL,
  type             ENUM('multiple_correct','fill_blank','brief','voice') NOT NULL,
  theme            ENUM('teamwork','conflict','failure','leadership','deadline_pressure') NULL,
  text             TEXT NOT NULL,
  options          JSON NULL,
  correct_options  JSON NULL,
  accepted_answers JSON NULL,
  key_facts        JSON NULL,
  keywords         JSON NULL,
  source           ENUM('ai','bank') NOT NULL,
  provider         VARCHAR(30) NULL,
  text_hash        CHAR(64) NOT NULL,
  mastered         BOOLEAN NOT NULL DEFAULT FALSE,
  last_score       DECIMAL(4,1) NULL,
  last_asked_at    DATETIME(3) NULL,
  times_asked      INT NOT NULL DEFAULT 0,
  created_at       DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_question_resume_hash (resume_id, text_hash),
  KEY ix_question_pick (resume_id, type, mastered, last_score),
  KEY ix_question_asked (resume_id, last_asked_at),
  CONSTRAINT fk_question_resume FOREIGN KEY (resume_id)        REFERENCES resume (id)        ON DELETE CASCADE,
  CONSTRAINT fk_question_point  FOREIGN KEY (resume_point_id)  REFERENCES resume_point (id)  ON DELETE CASCADE,
  CONSTRAINT fk_question_bank   FOREIGN KEY (bank_question_id) REFERENCES bank_question (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE practice_session (
  id            BIGINT NOT NULL AUTO_INCREMENT,
  user_id       BIGINT NOT NULL,
  resume_id     BIGINT NOT NULL,
  status        ENUM('in_progress','completed','abandoned') NOT NULL,
  voice_skipped BOOLEAN NOT NULL DEFAULT FALSE,
  max_marks     DECIMAL(5,1) NOT NULL,
  total_marks   DECIMAL(5,1) NULL,
  report_text   TEXT NULL,
  report_status ENUM('pending','ready','none') NOT NULL DEFAULT 'pending',
  started_at    DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  completed_at  DATETIME(3) NULL,
  open_user_id  BIGINT GENERATED ALWAYS AS (IF(status = 'in_progress', user_id, NULL)) VIRTUAL,
  PRIMARY KEY (id),
  UNIQUE KEY uq_session_one_open (open_user_id),
  KEY ix_session_user_started (user_id, started_at),
  CONSTRAINT fk_session_user   FOREIGN KEY (user_id)   REFERENCES app_user (id) ON DELETE CASCADE,
  CONSTRAINT fk_session_resume FOREIGN KEY (resume_id) REFERENCES resume (id)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attempt (
  id               BIGINT NOT NULL AUTO_INCREMENT,
  session_id       BIGINT NOT NULL,
  question_id      BIGINT NOT NULL,
  position         TINYINT NOT NULL,
  max_marks        DECIMAL(4,1) NOT NULL,
  answered_via     ENUM('click','typed','voice') NULL,
  selected_options JSON NULL,
  blank_answers    JSON NULL,
  answer_text      TEXT NULL,
  transcript       TEXT NULL,
  voice_stats      JSON NULL,
  score            DECIMAL(4,1) NULL,
  checklist        JSON NULL,
  blank_results    JSON NULL,
  graded_by        ENUM('java','ai','fallback') NULL,
  feedback_text    TEXT NULL,
  feedback_status  ENUM('pending','ready','none') NOT NULL DEFAULT 'pending',
  answered_at      DATETIME(3) NULL,
  created_at       DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  UNIQUE KEY uq_attempt_slot (session_id, position),
  KEY ix_attempt_question (question_id),
  CONSTRAINT fk_attempt_session  FOREIGN KEY (session_id)  REFERENCES practice_session (id) ON DELETE CASCADE,
  CONSTRAINT fk_attempt_question FOREIGN KEY (question_id) REFERENCES question (id)         ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE story (
  id                BIGINT NOT NULL AUTO_INCREMENT,
  user_id           BIGINT NOT NULL,
  title             VARCHAR(200) NOT NULL,
  situation         TEXT NULL,
  task              TEXT NULL,
  action            TEXT NULL,
  result            TEXT NULL,
  lesson            TEXT NULL,
  source_attempt_id BIGINT NULL,
  created_at        DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  updated_at        DATETIME(3) NOT NULL DEFAULT (UTC_TIMESTAMP(3)),
  PRIMARY KEY (id),
  KEY ix_story_user (user_id, created_at),
  CONSTRAINT fk_story_user    FOREIGN KEY (user_id)           REFERENCES app_user (id) ON DELETE CASCADE,
  CONSTRAINT fk_story_attempt FOREIGN KEY (source_attempt_id) REFERENCES attempt (id)  ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE story_tag (
  story_id BIGINT NOT NULL,
  tag      VARCHAR(40) NOT NULL,
  PRIMARY KEY (story_id, tag),
  KEY ix_story_tag_tag (tag),
  CONSTRAINT fk_story_tag_story FOREIGN KEY (story_id) REFERENCES story (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE story_resume_point (
  story_id        BIGINT NOT NULL,
  resume_point_id BIGINT NOT NULL,
  PRIMARY KEY (story_id, resume_point_id),
  KEY ix_story_rp_point (resume_point_id),
  CONSTRAINT fk_story_rp_story FOREIGN KEY (story_id)        REFERENCES story (id)        ON DELETE CASCADE,
  CONSTRAINT fk_story_rp_point FOREIGN KEY (resume_point_id) REFERENCES resume_point (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
