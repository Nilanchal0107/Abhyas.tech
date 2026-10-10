# Database schema: Abhyas.tech

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
| 4 | A saved resume **never changes** (read-only after save). A changed resume is uploaded as a new resume with fresh tracking. Nothing on it can be edited (the interview date column was dropped by `V10`). | Q3, Q8 |
| 5 | The **target role is fixed per resume**. Tracking per user + role + resume is therefore tracking per resume. | Q8 |
| 6 | The original **PDF is not stored**, only the file name and the parsed result. | Q4 |
| 7 | **Tables** for anything queried, joined or deleted separately. **JSON columns** for blobs that are only read whole (parsed resume, question options, accepted answers, checklist, voice stats, gap result). | Q5 |
| 8 | `skill` is one shared table. `role.owner_user_id` is NULL for starter roles. Aliases are their own table. | Q6 |
| 9 | Account deletion is a **hard delete** by `ON DELETE CASCADE` from `app_user`. Shared tables have no link to a user. | Q7 |
| 10 | Mastery, last score and last asked date are **columns on `question`**, updated when an answer is graded. `attempt` keeps the history. | Q9 |
| 11 | One `attempt` row per question slot of a session. No separate join table. | Q10 |
| 12 | Key facts live on the question (`question.key_facts`, `bank_question.key_facts`). The per-point `key_fact` table and the `resume_point.key_facts_*` columns were dropped by `V6` (ticket 10). | Q11 |
| 13 | *(Removed 10 October 2026: the story tables were dropped by `V9`.)* | Q12 |
| 14 | The gap result is stored as one JSON snapshot per resume. The export is built on request and not stored. | Q13 |
| 15 | `bank_question.text_hash` is unique, so the database rejects duplicates. | Q14 |
| 16 | Login is `app_user` with a BCrypt hash. Login state is a signed token (JWT), so there is no auth table. | Q15 |

## 2. Overview

```
app_user ──< resume ──< resume_point
   │           │             │
   │           ├── gap_result (1:1)
   │           ├──< question ──< attempt >── practice_session
   │           └── target_role ── role ──< role_alias
   │                              role ──< role_skill >── skill
   ├──< practice_session ──< attempt
   └──< role (owner, user-added roles only)

bank_question >── skill          (shared, no user link)
question >── bank_question       (optional source)
app_user ──< bank_performance >── bank_question   (a user's result on a bank question, across resumes)
```

Shared tables (never deleted with a user): `skill`, starter rows of `role`, their `role_alias` and `role_skill`, `skill_alias`, and `bank_question`.

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
| owner_key | BIGINT generated VIRTUAL = IFNULL(owner_user_id, 0) | lets one unique index cover starter and private roles |
| created_at | DATETIME(3) | |

Unique `(owner_key, name)`. Role search returns roles where `owner_user_id IS NULL OR owner_user_id = :me`.

### 3.4 `role_alias` (FR-3)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| role_id | BIGINT NOT NULL FK `role` ON DELETE CASCADE | |
| alias | VARCHAR(100) NOT NULL | for example "SDE" |

Unique `(role_id, alias)`. Index on `alias` for search.

### 3.4a `skill_alias` (FR-16, skill-bank D2)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| skill_id | BIGINT NOT NULL FK `skill` ON DELETE CASCADE | |
| alias | VARCHAR(100) NOT NULL | another way a resume writes the skill, for example "RESTful basics" for REST APIs |

Unique `alias` (case-insensitive, so one alias belongs to one skill). Seeded by `V4__skill_alias.sql` for the 74 skills of the 11 roles (202 aliases). The role skills check matches the skill name and its aliases as whole words, ignoring case, in every resume point (`SkillMatcher`).

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

Index `skill_id`. (`key_facts_status`, `key_facts_tries`, `last_try_at` and their index were dropped by `V6`.)

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
| format | ENUM('standard','design','code') NOT NULL DEFAULT 'standard' | `V12`. `design` is an interview-only design question; `code` is a brief answered in a code box |
| track | ENUM('backend','frontend') NULL | `V12`. A role flavour; NULL suits every role |
| source | ENUM('seed','ai') NOT NULL | |
| provider | VARCHAR(30) NULL | for example `groq` |
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
| key_facts | JSON NULL | the answer key of a technical brief or voice question (bank rows and project questions). NULL for the other types and for behavioral ones |
| keywords | JSON NULL | for keyword grading |
| format | ENUM('standard','design','code') NOT NULL DEFAULT 'standard' | `V12`, copied from the bank row |
| source | ENUM('ai','bank') NOT NULL | |
| provider | VARCHAR(30) NULL | |
| text_hash | CHAR(64) NOT NULL | hash of the text, so the same text is not stored twice for a resume |
| mastered | BOOLEAN NOT NULL DEFAULT FALSE | |
| last_score | DECIMAL(4,1) NULL | score of the latest attempt that counts toward mastery. NULL if none yet |
| last_asked_at | DATETIME(3) NULL | updated every time the question is asked |
| times_asked | INT NOT NULL DEFAULT 0 | |
| created_at | DATETIME(3) | |

Unique `(resume_id, text_hash)`. Index `(resume_id, type, mastered, last_score)` for session building. Index `(resume_id, last_asked_at)` for the one-month rest rule. The number of mastered questions on the Resume list is `COUNT(*) WHERE mastered`.

**Bank questions use `bank_performance` instead (ticket 18).** For a row with `bank_question_id` set, session building reads mastered, last score, last asked and times asked from the user's `bank_performance` row (none means new). The columns above are still written for such a row but are only a per-resume copy (the Resume list count uses them). A project question (`bank_question_id` NULL) uses these columns alone.

### 3.11a `bank_performance` (FR-13, FR-14, ticket 18), per user
A user's result on a bank question, kept across all their resumes, so a question mastered on one resume does not come back as new on another.

| Column | Type | Notes |
|---|---|---|
| user_id | BIGINT NOT NULL FK `app_user` ON DELETE CASCADE | part of the primary key |
| bank_question_id | BIGINT NOT NULL FK `bank_question` ON DELETE CASCADE | part of the primary key |
| mastered | BOOLEAN NOT NULL DEFAULT FALSE | |
| last_score | DECIMAL(4,1) NULL | score of the latest attempt that counts toward mastery |
| last_asked_at | DATETIME(3) NULL | updated when a session is built |
| times_asked | INT NOT NULL DEFAULT 0 | |

Primary key `(user_id, bank_question_id)`. The row is created when the question is first asked and updated by grading. A mastered question rests one month from `last_asked_at`, and the practice bank does not copy it into another resume while it rests. V5 copies what `question` already held (best result per user and bank question).

### 3.12 `practice_session` (Section 9)
| Column | Type | Notes |
|---|---|---|
| id | BIGINT PK AI | |
| user_id | BIGINT NOT NULL FK `app_user` ON DELETE CASCADE | |
| resume_id | BIGINT NOT NULL FK `resume` ON DELETE CASCADE | fixed for the session |
| status | ENUM('in_progress','completed','abandoned') NOT NULL | |
| mode | ENUM('quiz','interview') NOT NULL DEFAULT 'quiz' | added in V7 (ticket 13). A quiz has 19 questions, an interview 6 voice questions |
| voice_skipped | BOOLEAN NOT NULL DEFAULT FALSE | the voice part was left out (16 questions, 50 marks). Always false for sessions made since ticket 19; kept for old ones |
| max_marks | DECIMAL(5,1) NOT NULL | 80 or 50 |
| total_marks | DECIMAL(5,1) NULL | set when completed |
| report_text | TEXT NULL | the session report, stored as JSON text `{"headline":"...","summary":"..."}` |
| report_status | ENUM('pending','ready','none') NOT NULL DEFAULT 'pending' | `none` when no AI text exists (fallback) |
| started_at | DATETIME(3) NOT NULL | |
| completed_at | DATETIME(3) NULL | |
| open_user_id | BIGINT generated VIRTUAL = IF(status='in_progress', user_id, NULL) | |

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
| follow_up_text | TEXT NULL | interview only (V7): the AI follow-up, written once. Not an answer key, so it is shown during the session |
| follow_up_answer | TEXT NULL | interview only (V7): typed text or transcript. Graded together with the main answer |
| score | DECIMAL(4,1) NULL | NULL until graded |
| checklist | JSON NULL | see 4.3, with quoted proof |
| blank_results | JSON NULL | per-blank result for fill-blank, including the Groq re-check |
| graded_by | ENUM('java','ai','fallback') NULL | `java` for multiple-correct and fill-blank, `ai` for Groq checklists, `fallback` for keyword grading. Exported (FR-19). In the app only as `gradedWithoutAi` (report label and regrade) |
| feedback_text | TEXT NULL | Groq's feedback, or the template text in fallback, stored as JSON text `{"text":"...","whatToFix":"...","deliveryComment":null}` |
| feedback_status | ENUM('pending','ready','none') NOT NULL DEFAULT 'pending' | supports feedback that fills in gradually in the report |
| answered_at | DATETIME(3) NULL | |
| created_at | DATETIME(3) | |

Unique `(session_id, position)`. Index `question_id`.

**Mastery rule (Section 10 and FR-20)**, applied when an attempt is graded and the session completes:
- Multiple-correct and fill-blank always count. Brief and voice count only when `graded_by = 'ai'`.
- Mastered when `score >= mastery mark`: multiple-correct 2 of 2, fill-blank 2 of 2, brief 4 of 5, voice 8 of 10, and a typed voice answer 4 of 5. For brief and voice that is 80% of `max_marks`, for the objective types 100%.
- A counting attempt sets `question.last_score` and `question.mastered`. Every asked attempt updates `last_asked_at` and `times_asked`.

### 3.14 and 3.15 `story`, `story_tag`, `story_resume_point` (removed)
The story bank was removed on 10 October 2026. `V9__drop_story.sql` drops the three tables.

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
Counted by Java. The AI's comment on these numbers is part of `feedback_text`. Not stored for typed voice answers.

### 4.5 `gap_result.result`
```json
{"withEvidence":[{"skillId":12,"skill":"Java","resumePointIds":[3,7]}],
 "withoutEvidence":[{"skillId":31,"skill":"Docker"}]}
```

## 5. Delete behavior

| Deleted | What happens |
|---|---|
| `app_user` (FR-18) | Cascades to: user-added `role` (and its aliases and skills links), `resume` (and `resume_point`, `gap_result`, `question`), `practice_session` (and `attempt`), `bank_performance`. Nothing else is needed. |
| `resume` | Cascades to its resume points, gap result, questions, sessions and, through them, attempts. |
| `practice_session` | Cascades to its attempts. |
| Shared tables | `skill`, starter `role` rows, `role_alias`, `role_skill`, `skill_alias` and `bank_question` have no user link and are never touched. A bank question's `bank_question_id` on a user's question becomes NULL only if the bank row is removed. |

## 6. Main queries and the indexes behind them

| Need | Query shape | Index |
|---|---|---|
| Login | `app_user` by `login_id` | unique `login_id` |
| Role search (FR-3) | roles and aliases matching the text, own and starter roles | `role_alias(alias)`, `role(owner_key, name)` |
| Resume list | resumes of a user, with the mastered count | `resume(user_id, last_used_at)`, `question(resume_id, type, mastered, last_score)` |
| Build a session (Section 10) | per part: unmastered by lowest `last_score` first, then random from new and from mastered questions with `last_asked_at` older than one month. Bank questions read these from the user's `bank_performance` row | `question(resume_id, type, mastered, last_score)`, `question(resume_id, last_asked_at)`, `bank_performance` primary key |
| Continue session (Home) | the user's `in_progress` session | unique `practice_session(open_user_id)` |
| History | sessions of a user, newest first | `practice_session(user_id, started_at)` |
| Retry project questions (FR-8) | resumes of a user with no AI `question` row without `resume_point_id` and `theme` | `question(resume_id, ...)` |
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
| `V2__starter_roles.sql` | the original 42 starter roles, aliases, skills and links (already applied, so left unchanged) |
| `V3__narrow_starter_roles.sql` | deletes the starter roles not in Section 12, leaving the 11. A role that a resume targets is kept (`resume.target_role_id` cascades). Skills are kept |
| `V4__skill_alias.sql` | the `skill_alias` table and its seed aliases (ticket 04) |
| `V5__bank_performance.sql` | the per-user `bank_performance` table and the copy of existing bank results into it (ticket 18) |
| `V6__drop_key_fact.sql` | drops the `key_fact` table and the `resume_point.key_facts_status`, `key_facts_tries`, `last_try_at` columns and their index (ticket 10) |
| `V7__starter_bank.sql` (drafted as `V4__starter_bank.sql`, renumber when moved; the wave 2 draft becomes `V8`) | the reviewed practice bank, generated by `gen_bank.py` from the `bank_*.py` data files: all 74 skills of the 11 roles (per skill 10 multiple-correct, 10 fill-in-the-blank, 5 technical briefs with `key_facts` and `keywords`; Communication and Critical Thinking briefs only), 1,810 questions, plus 20 behavioral questions by theme. Drafted in `backend/src/main/resources/db/seed-draft/` (review sheet `review.md`), not loaded until reviewed and moved to `db/migration/`. `source = 'seed'` marks a reviewed row (FR-20, skill-bank D3, D5) |

## 9. V1 SQL

*This is the V1 file as first run. The story tables below were dropped later by `V9__drop_story.sql` (10 October 2026).*

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
```

## 10. Things to check when implementing

- `V1` was first run on MySQL 8.0.42 on 3 October 2026 (ticket 01). Generated columns whose base column has a foreign key must be `VIRTUAL`: MySQL refuses `STORED` there (error 1215). Before that, run `V1__schema.sql` on MySQL 8.0.16+ first and fix any syntax or generated-column error.
- Set the connection time zone to UTC (for example `serverTimezone=UTC` and Hibernate `jdbc.time_zone=UTC`), or the `UTC_TIMESTAMP(3)` defaults and application times will disagree.
- Starter data: the roles, aliases and skills in Section 12 go in `V2`. The file `roles_starter.csv` itself is not created yet; Section 12 holds its exact content.

## 11. Open items

None blocking. Possible later changes: a `deleted_at` column if soft delete is ever wanted, a shared tag table if tags need curating, and a `login_session` table if tokens must be revocable.

---

## 12. Starter roles data (`roles_starter.csv`)

This section is the **exact starter data** after `V3__narrow_starter_roles.sql` (FR-3): 11 roles in 3 categories, each with aliases and skills. It was written from general knowledge, not from a live job-market dataset, so the skills need your review before release (PRD risk "Starter roles list comes from Claude's general knowledge").

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
```

**Counts and checks**
- 4 roles in 1 category, Software Development (since 10 October 2026, `V11`; it was 11 roles in 3 categories). The `skill` table keeps all 228 skills from V2, so skills of removed roles still match on a resume, but the practice bank keeps rows only for skills a remaining role lists. Checked by script: role names are unique, aliases are unique across all roles (so an alias search finds one role), no alias equals another role's name, and no role lists a skill twice.
- Category names use "and" instead of "&", to keep the CSV and search simple.

- 5 October 2026 (ticket 13, `V7__interview_mode.sql`): `practice_session.mode`, `attempt.follow_up_text`, `attempt.follow_up_answer`. The V1 block above is unchanged; V7 is a separate migration.
