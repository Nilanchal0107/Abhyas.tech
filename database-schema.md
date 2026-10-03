# Database schema: Resume Defense Trainer

**Status:** Draft v1, from the grilling session on 3 October 2026.
**Related:** `prd.md` (FR numbers), `UI-UX.md`, `wireframes.md`, `CONTEXT.md` (glossary).
**Database:** MySQL 8.0.16 or newer (needed for enforced `CHECK` constraints and JSON columns). Items marked *(my decision)* were filled in by me and can be changed.

---

## 1. Decisions that shape the schema

| # | Decision | Source |
|---|---|---|
| 1 | Keys are `BIGINT AUTO_INCREMENT`. Times are UTC `DATETIME(3)`, converted in the UI. | Q1 |
| 2 | Table names are singular `snake_case`. Character set `utf8mb4`. Every table has `created_at`. | Q1, my suggestion, not rejected |
| 3 | The schema is created by **Flyway** SQL migrations. This file mirrors them. | Q2 |
| 4 | A saved resume **never changes** (read-only after save). A changed resume is uploaded as a new resume with fresh tracking. Only `interview_date` can be edited. | Q3, Q8 |
| 5 | The **target role is fixed per resume**. Tracking per user + role + resume is therefore tracking per resume. | Q8 |
| 6 | The original **PDF is not stored**, only the file name and the parsed result. | Q4 |
| 7 | **Tables** for anything queried, joined or deleted separately. **JSON columns** for blobs that are only read whole (parsed resume, question options, accepted answers, checklist, voice stats, gap result). | Q5 |
| 8 | `skill` is one shared table. `role.owner_user_id` is NULL for starter roles. Aliases are their own table. | Q6 |
| 9 | Account deletion is a **hard delete** by `ON DELETE CASCADE` from `app_user`. Shared tables have no link to a user. | Q7 |
| 10 | Mastery, last score and last asked date are **columns on `question`**, updated when an answer is graded. `attempt` keeps the history. | Q9 |
| 11 | One `attempt` row per question slot of a session. No separate join table. | Q10 |
| 12 | `resume_point` has `key_facts_status`, `key_facts_tries` and `last_try_at`. Key facts are rows in `key_fact`. | Q11 |
| 13 | Stories have `story_tag` and `story_resume_point` tables. Tags are free text. | Q12 |
| 14 | The gap result is stored as one JSON snapshot per resume. The export is built on request and not stored. | Q13 |
| 15 | `bank_question.text_hash` is unique, so the database rejects duplicates. | Q14 |
| 16 | Login is `app_user` with a BCrypt hash. Login state is a signed token (JWT), so there is no auth table. | Q15 |

## 2. Overview

```
app_user ──< resume ──< resume_point ──< key_fact
   │           │             │
   │           │             └──< story_resume_point >── story >── story_tag
   │           ├── gap_result (1:1)
   │           ├──< question ──< attempt >── practice_session
   │           └── target_role ── role ──< role_alias
   │                              role ──< role_skill >── skill
   ├──< practice_session ──< attempt
   ├──< story
   └──< role (owner, user-added roles only)

bank_question >── skill          (shared, no user link)
question >── bank_question       (optional source)
```

Shared tables (never deleted with a user): `skill`, starter rows of `role`, their `role_alias` and `role_skill`, and `bank_question`.

## 3. Tables

### 3.1 `app_user` (FR-1)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| login_id | VARCHAR(50) NOT NULL | the user ID typed at login. Unique, case-insensitive (collation `utf8mb4_0900_ai_ci`) |
| password_hash | VARCHAR(100) NOT NULL | BCrypt, never plain text |
| created_at | DATETIME(3) | |

The table is called `app_user` because `user` is a reserved word in some SQL tools.

### 3.2 `skill` (FR-3, FR-16, FR-20)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| name | VARCHAR(100) NOT NULL | unique, case-insensitive (collation), for example "Spring Boot" |
| created_at | DATETIME(3) | |

Shared by all users. Skills carry no personal data, so they survive account deletion.

### 3.3 `role` (FR-3)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| name | VARCHAR(100) NOT NULL | |
| category | VARCHAR(60) NULL | the 9 starter categories, NULL for user-added roles |
| owner_user_id | BIGINT NULL FK `app_user` ON DELETE CASCADE | NULL means a starter role, visible to everyone. Set means private to that user |
| owner_key | BIGINT generated STORED = IFNULL(owner_user_id, 0) | lets one unique index cover starter and private roles |
| created_at | DATETIME(3) | |

Unique `(owner_key, name)`. Role search returns roles where `owner_user_id IS NULL OR owner_user_id = :me`.

### 3.4 `role_alias` (FR-3)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| role_id | BIGINT NOT NULL FK `role` ON DELETE CASCADE | |
| alias | VARCHAR(100) NOT NULL | for example "SDE" |

Unique `(role_id, alias)`. Index on `alias` for search.

### 3.5 `role_skill` (FR-3)
| Column | Type | Notes |
|---|---|---|
| role_id | BIGINT FK `role` ON DELETE CASCADE | |
| skill_id | BIGINT FK `skill` ON DELETE CASCADE | |

Primary key `(role_id, skill_id)`. Index on `skill_id`.

### 3.6 `resume` (FR-2, FR-4 to FR-6)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| user_id | BIGINT NOT NULL FK `app_user` ON DELETE CASCADE | |
| file_name | VARCHAR(255) NOT NULL | shown in lists. The PDF itself is not stored |
| target_role_id | BIGINT NOT NULL FK `role` ON DELETE CASCADE | fixed after save. Cascade is safe: only a user's own role can be deleted, and only together with that user |
| parsed_json | JSON NOT NULL | the final reviewed resume (see 4.1) |
| parse_source | ENUM('openresume','ai_fallback') NOT NULL | |
| interview_date | DATE NULL | optional, only stored for now. The only editable column |
| last_used_at | DATETIME(3) NULL | set when a session starts, so Home can default to the most recently used resume |
| created_at | DATETIME(3) | |

Index `(user_id, last_used_at)`. Phone and email stay in `parsed_json` (the Review form shows them) and are removed only when text goes to AI and in the export.

### 3.7 `resume_point` (FR-7)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| resume_id | BIGINT NOT NULL FK `resume` ON DELETE CASCADE | |
| kind | ENUM('project_bullet','experience_bullet','skill') NOT NULL | |
| source_ref | VARCHAR(120) NOT NULL | where it came from in `parsed_json`, for example `projects[0].bullets[2]` |
| text | TEXT NOT NULL | the bullet or skill text |
| position | INT NOT NULL | order within the resume |
| skill_id | BIGINT NULL FK `skill` ON DELETE SET NULL | the matched skill, used by the gap check and to pick practice bank questions |
| key_facts_status | ENUM('pending','ready') NOT NULL DEFAULT 'pending' | FR-8 |
| key_facts_tries | TINYINT NOT NULL DEFAULT 0 | failed drafting attempts |
| last_try_at | DATETIME(3) NULL | retried only after a delay, never in a tight loop |

Index `(resume_id, key_facts_status)`, index `skill_id`.

### 3.8 `key_fact` (FR-8)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| resume_point_id | BIGINT NOT NULL FK `resume_point` ON DELETE CASCADE | |
| position | INT NOT NULL | |
| fact_text | TEXT NOT NULL | one true fact, read-only for the user |

### 3.9 `gap_result` (FR-16)
| Column | Type | Notes |
|---|---|---|
| resume_id | BIGINT PK FK `resume` ON DELETE CASCADE | one row per resume |
| result | JSON NOT NULL | see 4.5 |
| computed_at | DATETIME(3) NOT NULL | |

Recomputed on demand (Role skills check) and stored.

### 3.10 `bank_question` (FR-20), shared
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| type | ENUM('multiple_correct','fill_blank','brief','voice') NOT NULL | |
| skill_id | BIGINT NULL FK `skill` | set for technical questions |
| theme | ENUM('teamwork','conflict','failure','leadership','deadline_pressure') NULL | set for behavioral questions |
| difficulty | ENUM('easy','medium','hard') NULL | |
| text_template | TEXT NOT NULL | may contain placeholders such as `{project}`, `{skill}`, `{company}`, `{bullet}` |
| options | JSON NULL | multiple-correct only |
| correct_options | JSON NULL | multiple-correct only |
| accepted_answers | JSON NULL | fill-blank only |
| key_facts | JSON NULL | brief and voice |
| keywords | JSON NULL | brief and voice, used by keyword grading |
| source | ENUM('seed','ai') NOT NULL | |
| provider | VARCHAR(30) NULL | for example `groq`, `gemma` |
| text_hash | CHAR(64) NOT NULL | SHA-256 of skill/theme + type + normalized text |
| created_at | DATETIME(3) | |

Constraints: `CHECK (skill_id IS NOT NULL OR theme IS NOT NULL)`, unique `text_hash`. Index `(type, skill_id)`, index `(type, theme)`. No user column, on purpose: the bank must not hold personal data (FR-20).

### 3.11 `question` (FR-9, FR-13, FR-14), per resume
A question belongs to one resume, so tracking is per user + role + resume (decision 5). The same row is reused across sessions, so the **same question text is repeated** (Section 10, rule 3).

| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| resume_id | BIGINT NOT NULL FK `resume` ON DELETE CASCADE | |
| resume_point_id | BIGINT NULL FK `resume_point` ON DELETE CASCADE | the point the question is about, if any |
| bank_question_id | BIGINT NULL FK `bank_question` ON DELETE SET NULL | set when the question came from the bank |
| type | ENUM('multiple_correct','fill_blank','brief','voice') NOT NULL | |
| theme | ENUM('teamwork','conflict','failure','leadership','deadline_pressure') NULL | set means behavioral, NULL means technical |
| text | TEXT NOT NULL | with placeholders already filled |
| options | JSON NULL | multiple-correct, see 4.2 |
| correct_options | JSON NULL | multiple-correct |
| accepted_answers | JSON NULL | fill-blank, one list per blank, see 4.2 |
| key_facts | JSON NULL | for bank-sourced brief and voice questions. NULL means use the rows in `key_fact` of `resume_point_id` |
| keywords | JSON NULL | for keyword grading |
| source | ENUM('ai','bank') NOT NULL | |
| provider | VARCHAR(30) NULL | |
| text_hash | CHAR(64) NOT NULL | hash of the text, so the same text is not stored twice for a resume |
| mastered | BOOLEAN NOT NULL DEFAULT FALSE | |
| last_score | DECIMAL(4,1) NULL | score of the latest attempt that counts toward mastery. NULL if none yet |
| last_asked_at | DATETIME(3) NULL | updated every time the question is asked |
| times_asked | INT NOT NULL DEFAULT 0 | |
| created_at | DATETIME(3) | |

Unique `(resume_id, text_hash)`. Index `(resume_id, type, mastered, last_score)` for session building. Index `(resume_id, last_asked_at)` for the one-month rest rule. The number of mastered questions on the Resume list is `COUNT(*) WHERE mastered`.

### 3.12 `practice_session` (Section 9)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| user_id | BIGINT NOT NULL FK `app_user` ON DELETE CASCADE | |
| resume_id | BIGINT NOT NULL FK `resume` ON DELETE CASCADE | fixed for the session |
| status | ENUM('in_progress','completed','abandoned') NOT NULL | |
| voice_skipped | BOOLEAN NOT NULL DEFAULT FALSE | the voice part was left out (16 questions, 50 marks) |
| max_marks | DECIMAL(5,1) NOT NULL | 80 or 50 |
| total_marks | DECIMAL(5,1) NULL | set when completed |
| report_text | TEXT NULL | Gemma's session report, stored as JSON text `{"headline":"...","summary":"..."}` |
| report_status | ENUM('pending','ready','none') NOT NULL DEFAULT 'pending' | `none` when no AI text exists (fallback) |
| started_at | DATETIME(3) NOT NULL | |
| completed_at | DATETIME(3) NULL | |
| open_user_id | BIGINT generated = IF(status='in_progress', user_id, NULL) | |

Unique `open_user_id`, so a user has **at most one unfinished session** *(my decision, matches the single "Continue your practice session" card on Home)*. Index `(user_id, started_at)` for History.

### 3.13 `attempt` (FR-10 to FR-15)
One row per question slot, created when the session starts and filled in when the user moves on. A skipped question stays unanswered with score 0.

| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| session_id | BIGINT NOT NULL FK `practice_session` ON DELETE CASCADE | |
| question_id | BIGINT NOT NULL FK `question` ON DELETE CASCADE | |
| position | TINYINT NOT NULL | order in the session (1 to 19) |
| max_marks | DECIMAL(4,1) NOT NULL | 2, 5 or 10. A typed answer to a voice question is 5 |
| answered_via | ENUM('click','typed','voice') NULL | NULL until answered. Fallback typed voice answers are `typed` |
| selected_options | JSON NULL | multiple-correct |
| blank_answers | JSON NULL | fill-blank |
| answer_text | TEXT NULL | brief, or a typed voice answer |
| transcript | TEXT NULL | voice (Whisper) |
| voice_stats | JSON NULL | see 4.4 |
| score | DECIMAL(4,1) NULL | NULL until graded |
| checklist | JSON NULL | see 4.3, with quoted proof |
| blank_results | JSON NULL | per-blank result for fill-blank, including the Groq re-check |
| graded_by | ENUM('java','ai','fallback') NULL | `java` for multiple-correct and fill-blank, `ai` for Groq checklists, `fallback` for keyword grading. Exported (FR-19), never shown in the app |
| feedback_text | TEXT NULL | Gemma's feedback, or the template text in fallback, stored as JSON text `{"text":"...","whatToFix":"...","deliveryComment":null}` |
| feedback_status | ENUM('pending','ready','none') NOT NULL DEFAULT 'pending' | supports feedback that fills in gradually in the report |
| answered_at | DATETIME(3) NULL | |
| created_at | DATETIME(3) | |

Unique `(session_id, position)`. Index `question_id`.

**Mastery rule (Section 10 and FR-20)**, applied when an attempt is graded and the session completes:
- Multiple-correct and fill-blank always count. Brief and voice count only when `graded_by = 'ai'`.
- Mastered when `score >= mastery mark`: multiple-correct 2 of 2, fill-blank 2 of 2, brief 4 of 5, voice 8 of 10, and a typed voice answer 4 of 5. For brief and voice that is 80% of `max_marks`, for the objective types 100%.
- A counting attempt sets `question.last_score` and `question.mastered`. Every asked attempt updates `last_asked_at` and `times_asked`.

### 3.14 `story` (FR-17)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| user_id | BIGINT NOT NULL FK `app_user` ON DELETE CASCADE | |
| title | VARCHAR(200) NOT NULL | |
| situation | TEXT NULL | |
| task | TEXT NULL | |
| action | TEXT NULL | pre-filled from the answer by "Save as story" |
| result | TEXT NULL | |
| lesson | TEXT NULL | lesson learned |
| source_attempt_id | BIGINT NULL FK `attempt` ON DELETE SET NULL | the answer it was saved from, if any *(my decision)* |
| created_at | DATETIME(3) | |
| updated_at | DATETIME(3) | |

Only the title is required (matches the story form). Index `(user_id, created_at)`.

### 3.15 `story_tag` and `story_resume_point` (FR-17)
- `story_tag(story_id FK story ON DELETE CASCADE, tag VARCHAR(40))`, primary key `(story_id, tag)`, index on `tag`. Tags are free text per story, with no shared tag table.
- `story_resume_point(story_id FK story ON DELETE CASCADE, resume_point_id FK resume_point ON DELETE CASCADE)`, primary key `(story_id, resume_point_id)`.

## 4. JSON column shapes

### 4.1 `resume.parsed_json`
The structured resume as the Review form saves it, in our own simplified shape (defined in `api-contracts.md` 2.1): `profile` (name, email, phone, location, links, summary), `education`, `experience` (with `bullets`), `projects` (with `bullets`) and `skills` (a list of names). `resume_point.source_ref` uses these paths, for example `experience[0].bullets[2]`. Phone and email live here and are removed by the AI-bound text builder and by the export (FR-5, FR-19).

### 4.2 `question.options`, `correct_options`, `accepted_answers`
```json
"options": [{"key":"A","text":"..."},{"key":"B","text":"..."},{"key":"C","text":"..."},{"key":"D","text":"..."}]
"correct_options": ["A","C"]
"accepted_answers": [["@RestController","RestController"], ["JSON"]]
```
`accepted_answers` holds one list of accepted wordings per blank. Its length is the blank count (1 or 2).

### 4.3 `attempt.checklist`
```json
[{"item":"explains_why","passed":true,"quote":"I chose Redis because ..."},
 {"item":"consistent_with_resume","passed":false,"quote":null}]
```
Every `passed: true` has a non-empty `quote` (evidence rule, Section 9). 5 items for brief, 10 for voice.

### 4.4 `attempt.voice_stats`
```json
{"wordsPerMinute":142,"fillerCount":5,"wordCount":96,"durationSeconds":40}
```
Counted by Java. Gemma's comment on these numbers is part of `feedback_text`. Not stored for typed voice answers.

### 4.5 `gap_result.result`
```json
{"withEvidence":[{"skillId":12,"skill":"Java","resumePointIds":[3,7]}],
 "withoutEvidence":[{"skillId":31,"skill":"Docker"}]}
```

## 5. Delete behavior

| Deleted | What happens |
|---|---|
| `app_user` (FR-18) | Cascades to: user-added `role` (and its aliases and skills links), `resume` (and `resume_point`, `key_fact`, `gap_result`, `question`), `practice_session` (and `attempt`), `story` (and tags and links). Nothing else is needed. |
| `resume` | Cascades to its resume points, key facts, gap result, questions, sessions and, through them, attempts. |
| `practice_session` | Cascades to its attempts. A story's `source_attempt_id` becomes NULL. |
| Shared tables | `skill`, starter `role` rows, `role_alias`, `role_skill` and `bank_question` have no user link and are never touched. A bank question's `bank_question_id` on a user's question becomes NULL only if the bank row is removed. |

## 6. Main queries and the indexes behind them

| Need | Query shape | Index |
|---|---|---|
| Login | `app_user` by `login_id` | unique `login_id` |
| Role search (FR-3) | roles and aliases matching the text, own and starter roles | `role_alias(alias)`, `role(owner_key, name)` |
| Resume list | resumes of a user, with the mastered count | `resume(user_id, last_used_at)`, `question(resume_id, type, mastered, last_score)` |
| Build a session (Section 10) | per part: unmastered by lowest `last_score` first, then random from new and from mastered questions with `last_asked_at` older than one month | `question(resume_id, type, mastered, last_score)`, `question(resume_id, last_asked_at)` |
| Continue session (Home) | the user's `in_progress` session | unique `practice_session(open_user_id)` |
| History | sessions of a user, newest first | `practice_session(user_id, started_at)` |
| Retry key facts (FR-8) | resume points `pending` for a resume, oldest `last_try_at` first | `resume_point(resume_id, key_facts_status)` |
| Story tag filter | stories of a user with a tag | `story_tag(tag)`, `story(user_id, created_at)` |
| Pick practice bank questions (FR-20) | by type and skill, or type and theme | `bank_question(type, skill_id)`, `bank_question(type, theme)` |

Every query on user data filters by the signed-in user, directly or through `resume.user_id` (`question` has no `user_id` column of its own). The backend must never accept a `resume_id` or `session_id` without checking that it belongs to the user (data isolation, Section 12).

## 7. Not stored on purpose

- The original PDF (privacy).
- The export JSON (built from the tables on request, with phone and email removed).
- Audio (only transcripts, FR-19).
- Login sessions (signed token instead).

## 8. Flyway migrations

| Migration | Content |
|---|---|
| `V1__schema.sql` | all tables in 9 below |
| `V2__starter_roles.sql` | the 42 starter roles, aliases, skills and links, from the data in Section 12 (`roles_starter.csv`) |
| `V3__starter_bank.sql` | the software development practice bank from `bank_starter.csv`. Built later, on request (FR-20) |

## 9. V1 SQL

```sql
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
  owner_key     BIGINT GENERATED ALWAYS AS (IFNULL(owner_user_id, 0)) STORED,
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
  open_user_id  BIGINT GENERATED ALWAYS AS (IF(status = 'in_progress', user_id, NULL)) STORED,
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
```

## 10. Things to check when implementing

- The SQL above has not been run against a MySQL server. Run `V1__schema.sql` on MySQL 8.0.16+ first and fix any syntax or generated-column error.
- Set the connection time zone to UTC (for example `serverTimezone=UTC` and Hibernate `jdbc.time_zone=UTC`), or the `UTC_TIMESTAMP(3)` defaults and application times will disagree.
- `story.updated_at` is set by the application on every edit (MySQL's `ON UPDATE` only accepts `CURRENT_TIMESTAMP`, which would follow the session time zone instead of UTC).
- Starter data: the roles, aliases and skills in Section 12 go in `V2`. The file `roles_starter.csv` itself is not created yet; Section 12 holds its exact content.

## 11. Open items

None blocking. Possible later changes: a `deleted_at` column if soft delete is ever wanted, a shared tag table if tags need curating, and a `login_session` table if tokens must be revocable.

---

## 12. Starter roles data (`roles_starter.csv`)

This section is the **exact starter data** for `V2__starter_roles.sql` (FR-3): 42 roles in 9 categories, each with aliases and skills. It was written from general knowledge, not from a live job-market dataset, so the skills need your review before release (PRD risk "Starter roles list comes from Claude's general knowledge").

**Format**
- Columns: `category,role,aliases,skills`. Fields are double-quoted. Inside `aliases` and `skills`, items are separated by `|`.
- Load rules for V2:
  - Each row becomes one `role` row with `owner_user_id = NULL` and the `category`.
  - Each alias becomes a `role_alias` row.
  - Each skill is inserted into `skill` if its name is not there yet, then linked with `role_skill`. Skill names are written the same way in every row, so shared skills (for example `Git`, `SQL`, `Agile`) become one row each.
- Save the block below as `roles_starter.csv` (UTF-8, with the header line) if the migration is generated from a file. This section and the file must stay identical.

```csv
category,role,aliases,skills
"Software Development","Software Engineer","SDE|SWE|Software Developer|Programmer","Data Structures|Algorithms|Object-Oriented Programming|System Design|SQL|Git|REST APIs|Unit Testing|Design Patterns|Debugging|Agile"
"Software Development","Backend Developer","Backend Engineer|Server-Side Developer|API Developer","Java|Spring Boot|REST APIs|SQL|MySQL|Microservices|Redis|Message Queues|Authentication|Unit Testing|Docker|Git"
"Software Development","Frontend Developer","Frontend Engineer|UI Developer|Web Developer","JavaScript|TypeScript|React|HTML|CSS|Responsive Design|Web Accessibility|REST APIs|State Management|Unit Testing|Git|Web Performance"
"Software Development","Full Stack Developer","Full Stack Engineer|Full-Stack Developer|MERN Developer","JavaScript|React|Node.js|REST APIs|SQL|MongoDB|HTML|CSS|Git|Docker|Authentication|Unit Testing"
"Software Development","Mobile App Developer","Android Developer|iOS Developer|Mobile Engineer|React Native Developer","Kotlin|Swift|React Native|Mobile UI Design|REST APIs|Local Storage|App Lifecycle|Push Notifications|Git|Unit Testing|App Store Publishing"
"Software Development","Game Developer","Game Programmer|Unity Developer|Gameplay Programmer","C#|C++|Unity|Game Physics|3D Math|Game Loop|Data Structures|Algorithms|Graphics Programming|Performance Optimization|Git"
"Software Development","Embedded Systems Engineer","Firmware Engineer|Embedded Software Engineer|Embedded Developer","C|C++|Microcontrollers|RTOS|Communication Protocols|Debugging|Hardware Interfacing|Memory Management|Linux|Git|Unit Testing"
"Data and AI","Data Analyst","Business Data Analyst|Reporting Analyst|Analytics Analyst","SQL|Excel|Data Visualization|Statistics|Python|Data Cleaning|Dashboards|Power BI|Tableau|Critical Thinking|Communication"
"Data and AI","Data Scientist","DS|Applied Scientist|Research Data Scientist","Python|Statistics|Machine Learning|SQL|Data Cleaning|Feature Engineering|Model Evaluation|Data Visualization|Pandas|Scikit-learn|Communication"
"Data and AI","Data Engineer","DE|ETL Developer|Big Data Engineer|Data Pipeline Engineer","SQL|Python|ETL|Data Pipelines|Apache Spark|Kafka|Data Warehousing|Airflow|Cloud Storage|Data Modeling|Git"
"Data and AI","Machine Learning Engineer","ML Engineer|MLE|Applied ML Engineer","Python|Machine Learning|Deep Learning|PyTorch|Model Deployment|Feature Engineering|Model Evaluation|Docker|MLOps|SQL|Git"
"Data and AI","AI Engineer","LLM Engineer|Generative AI Engineer|GenAI Developer|AI Developer","Python|LLM APIs|Prompt Engineering|Retrieval-Augmented Generation|Vector Databases|Model Evaluation|REST APIs|Docker|Machine Learning|Git|Cost Management"
"Data and AI","BI Developer","Business Intelligence Developer|BI Engineer|Reporting Developer","SQL|Data Warehousing|ETL|Power BI|Tableau|Data Modeling|Dashboards|Data Visualization|DAX|Performance Tuning|Communication"
"Cloud and DevOps","DevOps Engineer","DevOps|DevOps Developer|CI/CD Engineer","CI/CD|Docker|Kubernetes|Linux|Git|Infrastructure as Code|Terraform|AWS|Monitoring|Shell Scripting|Jenkins|Networking Basics"
"Cloud and DevOps","Cloud Engineer","Cloud Developer|AWS Engineer|Azure Engineer|Cloud Infrastructure Engineer","AWS|Azure|Linux|Networking Basics|Infrastructure as Code|Terraform|Cloud Security|Docker|Monitoring|Cost Management|Shell Scripting"
"Cloud and DevOps","Site Reliability Engineer","SRE|Reliability Engineer|Production Engineer","Linux|Monitoring|Incident Response|Kubernetes|Python|Infrastructure as Code|Networking Basics|CI/CD|Capacity Planning|Observability|Shell Scripting"
"Cloud and DevOps","Platform Engineer","Internal Platform Engineer|Developer Platform Engineer|Infrastructure Engineer","Kubernetes|Docker|Terraform|CI/CD|Linux|AWS|Observability|Developer Experience|Go|Python|Git"
"Cloud and DevOps","Build and Release Engineer","Release Engineer|Build Engineer|Release Manager","CI/CD|Git|Jenkins|Build Tools|Shell Scripting|Docker|Release Management|Linux|Version Control Branching|Artifact Repositories|Automation"
"Infrastructure and Databases","Database Administrator","DBA|Database Engineer|MySQL DBA|SQL DBA","SQL|MySQL|PostgreSQL|Database Backup and Recovery|Query Optimization|Indexing|Database Security|Replication|Linux|Performance Tuning|Shell Scripting"
"Infrastructure and Databases","Network Engineer","Network Administrator|Network Specialist|Network Support Engineer","Networking Basics|TCP/IP|Routing and Switching|Firewalls|VPN|DNS|Network Monitoring|Network Security|Linux|Troubleshooting|Cisco"
"Infrastructure and Databases","Systems Administrator","SysAdmin|System Administrator|Linux Administrator|Windows Administrator","Linux|Windows Server|Shell Scripting|Networking Basics|Active Directory|Backup and Recovery|Monitoring|Troubleshooting|Virtualization|Security Basics|Automation"
"Infrastructure and Databases","IT Support Engineer","Technical Support Engineer|Desktop Support Engineer|Help Desk Engineer|L2 Support","Troubleshooting|Windows Server|Networking Basics|Active Directory|Ticketing Systems|Hardware Support|Customer Communication|Linux|Remote Support|Security Basics|Documentation"
"Quality and Testing","QA Engineer","Quality Assurance Engineer|Software Tester|Test Engineer|QA Analyst","Test Case Design|Manual Testing|Bug Reporting|Test Plans|SQL|API Testing|Regression Testing|Agile|Git|Exploratory Testing|Communication"
"Quality and Testing","Test Automation Engineer","SDET|Automation Tester|Software Development Engineer in Test|QA Automation Engineer","Selenium|Test Automation Frameworks|Java|Python|API Testing|CI/CD|Git|SQL|Unit Testing|Regression Testing|Test Case Design"
"Quality and Testing","Performance Test Engineer","Performance Tester|Load Test Engineer|Performance Engineer","JMeter|Load Testing|Performance Monitoring|SQL|Linux|Bottleneck Analysis|API Testing|CI/CD|Scripting|Test Plans|Communication"
"Quality and Testing","QA Lead","Test Lead|QA Manager|Test Manager","Test Plans|Test Strategy|Team Leadership|Agile|Risk Assessment|Test Automation Frameworks|Defect Management|Communication|Stakeholder Management|Metrics and Reporting|CI/CD"
"Security","Security Engineer","Cybersecurity Engineer|InfoSec Engineer|Information Security Engineer","Security Basics|Network Security|Cloud Security|Authentication|Encryption|Vulnerability Management|Linux|Incident Response|Scripting|Firewalls|Threat Modeling"
"Security","Application Security Engineer","AppSec Engineer|Product Security Engineer|Secure Code Reviewer","OWASP Top 10|Secure Code Review|Threat Modeling|Authentication|Encryption|Static Analysis|Dynamic Analysis|REST APIs|Git|CI/CD|Vulnerability Management"
"Security","Penetration Tester","Pentester|Ethical Hacker|Red Team Engineer|Offensive Security Engineer","Penetration Testing|OWASP Top 10|Networking Basics|Linux|Burp Suite|Metasploit|Vulnerability Assessment|Scripting|Report Writing|Web Security|Social Engineering"
"Security","SOC Analyst","Security Analyst|Cybersecurity Analyst|Security Operations Analyst|Blue Team Analyst","SIEM|Incident Response|Log Analysis|Threat Detection|Networking Basics|Malware Analysis Basics|Linux|Security Basics|Vulnerability Management|Documentation|Communication"
"Architecture and Leadership","Software Architect","Technical Architect|Application Architect|Principal Engineer","System Design|Design Patterns|Microservices|Scalability|API Design|Cloud Architecture|Trade-off Analysis|Security Basics|Database Design|Technical Documentation|Mentoring"
"Architecture and Leadership","Solutions Architect","Cloud Solutions Architect|Enterprise Architect|Pre-Sales Architect","Cloud Architecture|AWS|System Design|Microservices|Networking Basics|Security Basics|Cost Management|Stakeholder Management|Technical Documentation|Integration Patterns|Communication"
"Architecture and Leadership","Technical Lead","Tech Lead|Team Lead|Lead Developer|Lead Engineer","System Design|Code Review|Mentoring|Agile|Design Patterns|Estimation|Stakeholder Management|Git|Unit Testing|Technical Documentation|Conflict Resolution"
"Architecture and Leadership","Engineering Manager","EM|Development Manager|Software Engineering Manager","Team Leadership|Hiring|Performance Reviews|Agile|Project Planning|Stakeholder Management|Mentoring|Conflict Resolution|Roadmapping|Budgeting|Communication"
"Product and Design","Product Manager","PM|Product Owner|Associate Product Manager|APM","Product Roadmapping|User Research|Prioritization|Stakeholder Management|Agile|Metrics and Reporting|Requirements Gathering|Communication|Market Analysis|A/B Testing|Wireframing"
"Product and Design","Business Analyst","BA|IT Business Analyst|Systems Analyst|Functional Analyst","Requirements Gathering|User Stories|Process Modeling|SQL|Stakeholder Management|Documentation|Wireframing|Agile|Data Analysis|Communication|UML"
"Product and Design","UI/UX Designer","UX Designer|UI Designer|Product Designer|Interaction Designer","Wireframing|Prototyping|Figma|User Research|Usability Testing|Design Systems|Web Accessibility|Responsive Design|Information Architecture|Visual Design|Communication"
"Product and Design","Technical Program Manager","TPM|Program Manager|Technical Project Manager|Project Manager","Project Planning|Roadmapping|Risk Assessment|Stakeholder Management|Agile|Metrics and Reporting|Cross-Team Coordination|Technical Documentation|Budgeting|Communication|Estimation"
"Emerging and Specialized","Blockchain Developer","Smart Contract Developer|Web3 Developer|Solidity Developer","Solidity|Smart Contracts|Ethereum|Cryptography Basics|JavaScript|Web3 Libraries|Smart Contract Security|Git|Unit Testing|Data Structures|Distributed Systems"
"Emerging and Specialized","Salesforce Developer","SFDC Developer|Salesforce Engineer|Apex Developer","Apex|Salesforce Lightning|SOQL|Salesforce Flows|REST APIs|Salesforce Data Model|JavaScript|Unit Testing|Salesforce Integration|Git|Agile"
"Emerging and Specialized","SAP Consultant","SAP Developer|SAP ABAP Developer|SAP Functional Consultant","SAP ABAP|SAP Modules|SQL|Business Process Knowledge|Requirements Gathering|SAP Fiori|Data Migration|Documentation|Communication|Unit Testing|Integration Patterns"
"Emerging and Specialized","RPA Developer","Robotic Process Automation Developer|UiPath Developer|Automation Developer","UiPath|Process Automation|Workflow Design|Exception Handling|Excel|SQL|REST APIs|Requirements Gathering|Debugging|Orchestrator|Documentation"
```

**Counts and checks**
- 42 roles in 9 categories: Software Development (7), Data and AI (6), Cloud and DevOps (5), Infrastructure and Databases (4), Quality and Testing (4), Security (4), Architecture and Leadership (4), Product and Design (4), Emerging and Specialized (4).
- 143 aliases and 228 distinct skills (466 role-skill links). Checked by script: role names are unique, aliases are unique across all roles (so an alias search finds one role), no alias equals another role's name, and no role lists a skill twice.
- Category names use "and" instead of "&", to keep the CSV and search simple.
