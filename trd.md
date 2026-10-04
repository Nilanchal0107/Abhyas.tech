# TRD: Abhyas.tech

**Status:** Draft v1, from the grilling session on 3 October 2026. Items marked *(my decision)* were filled in by me and can be changed.
**Related:** `prd.md` (what and why, FR numbers), `UI-UX.md` and `wireframes.md` (screens), `database-schema.md` (tables and V1 SQL, starter roles), `api-contracts.md` (exact request and response of every endpoint), `CONTEXT.md` (glossary).

This document says **how** the app is built. It does not repeat the requirements.

---

## 1. Decisions that shape the build

| # | Decision | Source |
|---|---|---|
| 1 | The OpenResume parser is copied into the frontend and the **whole project is licensed AGPL-3.0**. | Q1 |
| 2 | One **monorepo** (`frontend/`, `backend/`, docs at the root). Java 21 (LTS), Maven, React with Vite in plain JavaScript. | Q2 |
| 3 | **MySQL 8 is installed natively on Windows** (not Docker). Backend runs with Maven, frontend with `npm run dev`. | Q3 |
| 4 | Login is a JWT in an `HttpOnly`, `SameSite=Strict` cookie. The Vite dev server proxies `/api`, so there is one origin. | Q4 |
| 5 | **Submit returns at once.** Grading and feedback run in a background task. The report page polls. | Q5 |
| 6 | A small hand-written **AI gate** per provider decides when AI is "unavailable" (timeout, retry, cooldown). | Q6 |
| 7 | AI output is validated by Java. The evidence rule is enforced by Java. **Java adds up all marks.** | Q7 |
| 8 | Voice: WebM/Opus, 3 minutes maximum, never stored. | Q8 |
| 9 | Tests: JUnit, Testcontainers (real MySQL 8), Vitest. No end-to-end tests in v1. **A manual check after every build step.** | Q9 |
| 10 | Backend packages **by feature**. Spring Data JPA, Flyway owns the schema (`ddl-auto=validate`). | Q10 |
| 11 | Three AI interfaces (`GraderService`, `FeedbackService`, `QuestionService`) each with an AI and a fallback implementation. One `AiClient` for every AI call, which also removes phone and email from outgoing text. | Q11 |
| 12 | Questions are generated at **session start**, only the missing slots, one Groq call per part. | Q12 |
| 13 | PDF upload: PDF only (checked by header), **1 MB maximum**. The parse fallback runs when the browser parse is incomplete or low quality (see 7.1). | Q13 |
| 14 | Resume skills are matched to existing skills only. **New skills are never created from resumes.** | Q14 |
| 15 | Errors use RFC 7807 Problem Details. Secrets come from environment variables or a gitignored local config. | Q15 |
| 16 | Passwords: at least 8 characters, BCrypt, and an in-memory throttle after repeated failures. | Q16 |
| 17 | Logs hold IDs, endpoints, providers, timings and error types only. Never resume text, answers, transcripts or AI prompts and replies. | Q17 |
| 18 | REST API under `/api` as listed in Section 8. Another user's resource is "not found", never "forbidden". | Q18 |
| 19 | Session building rules as in Section 7.2. | Q19 |
| 20 | Grading order and pacing as in Section 7.4. | Q20 |
| 21 | Frontend: React Router, a `fetch` wrapper, TanStack Query, no Redux. | Q21 |
| 22 | AGPL housekeeping: `LICENSE`, OpenResume notices kept, README note. | Q22 |
| 23 | Six build steps, each with tests and a manual check list. | Q23 |
| 24 | Endpoint shapes, error codes and limits are fixed in `api-contracts.md`. A session in progress never reveals correct answers. Signup logs the user in. The resume JSON shape is our own simplified one. | API session |

## 2. Technology

| Area | Choice | Notes |
|---|---|---|
| Language and build | Java 21 (LTS), Maven | |
| Backend framework | Spring Boot (current stable release, pinned in `pom.xml` at project start) | Web, Validation, Security, Data JPA, Actuator off |
| Database | MySQL 8.0.16 or newer, native install | needed for enforced `CHECK`, JSON columns and generated columns |
| Migrations | Flyway (`V1__schema.sql`, `V2__starter_roles.sql`, `V3__starter_bank.sql` later) | content in `database-schema.md` |
| Data access | Spring Data JPA (Hibernate), a few native queries for session building | JSON columns mapped as JSON. Generated columns are read-only in entities |
| JWT | `nimbus-jose-jwt` (changed from `jjwt` in ticket 02: no Jackson 2 next to Boot 4) | HS256, secret from config |
| Passwords | BCrypt (Spring Security `BCryptPasswordEncoder`) | |
| PDF fallback | Apache PDFBox | text and hidden links (FR-5) |
| HTTP to AI | Spring `RestClient` | |
| JSON | Jackson, Bean Validation | |
| Frontend | React, JavaScript, Vite | |
| Frontend libraries | React Router, TanStack Query | plus the copied OpenResume parser (PDF.js) |
| Styling | Plain CSS with the tokens in `UI-UX.md` Section 4 | no UI library, no Tailwind |
| Backend tests | JUnit 5, Testcontainers (MySQL 8) | |
| Frontend tests | Vitest | |
| AI providers | Groq (chat and Whisper), Google AI Studio (Gemma) | check each provider's current model IDs and limits when coding (PRD Section 11) |

## 3. Architecture

```
 Browser (React, Vite)                              Spring Boot (localhost:8080)
 +-------------------------------+   /api (JSON)   +----------------------------------+
 | Screens W0 to W12             | --------------> | Controllers (per feature)        |
 | OpenResume parser (PDF.js)    |   cookie auth   | Services                         |
 | MediaRecorder (voice)         | <-------------- | Repositories (JPA)               |
 | TanStack Query (cache, poll)  |                 | AiClient + AiGate  ----> Groq chat, Groq Whisper, Gemma
 +-------------------------------+                 | Async executor (grading)         |
        Vite dev server :5173                      +----------------+-----------------+
        proxies /api to :8080                                       |
                                                                    v
                                                            MySQL 8 (native)
```

- The browser never calls an AI provider. All keys stay in the backend.
- The PDF is parsed in the browser. The PDF is sent to the backend only for the parse fallback, and it is discarded after the request.

## 4. Repository layout

```
/ (repo root)
  LICENSE                  AGPL-3.0
  README.md                setup, license note, OpenResume credit
  .env.example             names of required environment variables, no values
  prd.md  trd.md  UI-UX.md  wireframes.md  database-schema.md  CONTEXT.md
  backend/
    pom.xml
    src/main/java/.../
      auth/  role/  resume/  question/  session/  grading/  story/  export/  ai/  common/
    src/main/resources/
      application.yml
      db/migration/        V1__schema.sql, V2__starter_roles.sql
    src/test/java/...
  frontend/
    package.json  vite.config.js
    src/
      api/                 fetch wrapper and one file per feature
      pages/               one folder per screen (W1 to W12)
      components/          shared pieces (button, field, progress bar, accordion)
      lib/openresume/      copied parser with its license notice
      styles/              tokens.css and base.css
      main.jsx  App.jsx
```

## 5. Local setup (Windows) *(my decisions for the exact values)*

1. Install **MySQL 8.0.x** (MySQL Installer). Check `SELECT VERSION();` is 8.0.16 or newer.
2. Create the database and a user:
   ```sql
   CREATE DATABASE resume_trainer CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER 'trainer'@'localhost' IDENTIFIED BY '<password>';
   GRANT ALL PRIVILEGES ON resume_trainer.* TO 'trainer'@'localhost';
   ```
   If the `mysql` command is not on your PATH, use MySQL Shell (`\connect root@localhost`, then `\sql`) to run these statements.
3. Set environment variables (names in `.env.example`): `DB_URL` (with `serverTimezone=UTC`), `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` (at least 32 random bytes), `GROQ_API_KEY`, `GOOGLE_AI_KEY`. Alternatively put them in the gitignored `backend/src/main/resources/application-local.yml`.
4. Backend: `cd backend`, then `mvn spring-boot:run`. Flyway creates the tables on first start.
5. Frontend: `cd frontend`, `npm install`, `npm run dev`. Open `http://localhost:5173`.
6. The connection time zone must be UTC (the schema uses `UTC_TIMESTAMP(3)`): `serverTimezone=UTC` in the URL and `spring.jpa.properties.hibernate.jdbc.time_zone=UTC`.

## 6. Backend design

### 6.1 Packages (by feature)
| Package | Holds | FR |
|---|---|---|
| `auth` | signup, login, logout, `/me`, account deletion, JWT filter, login throttle | FR-1, FR-18 |
| `role` | role search, draft skills, add role, starter data access | FR-3 |
| `resume` | upload fallback parse, save, list, resume points, key facts, role skills check | FR-2, 4 to 8, 16 |
| `question` | question and bank access, question generation, session slot selection | FR-9, 13, 14, 20 |
| `session` | session lifecycle, attempts, answering, voice transcription endpoint, report data | FR-10 to 15 |
| `grading` | marking rules, fill-in-the-blank matching, checklist handling, mastery updates | Section 9, 10 |
| `story` | story CRUD, tags, links | FR-17 |
| `export` | build the export JSON, remove phone and email | FR-19 |
| `ai` | `AiClient`, `AiGate`, prompts, provider adapters, JSON validation, PII scrubber | FR-5, FR-20 |
| `common` | Problem Details handler, security config, current-user helper, time and hash helpers | |

### 6.2 Persistence rules
- Entities mirror `database-schema.md` exactly. `ddl-auto=validate`, so the app will not start if an entity and the migrations disagree.
- `role.owner_key` and `practice_session.open_user_id` are generated columns: mapped `insertable = false, updatable = false`.
- `story.updated_at` is set by the service on every edit.
- Every repository method that reads user data takes the user ID (directly or through `resume.user_id`). A helper `requireOwnedResume(userId, id)` and `requireOwnedSession(userId, id)` throws "not found" for someone else's data. Controllers never load a resume or session by ID alone.

### 6.3 Security configuration
- Stateless. An interceptor (built in ticket 02, public paths: signup, login, logout) reads the `rdt_session` cookie *(my decision for the name)*, validates the JWT, and sets the current user.
- Cookie: `HttpOnly`, `SameSite=Strict`, `Path=/`, lifetime 8 hours, `Secure` set from config (off on `http://localhost`, on if it is ever served over HTTPS). No refresh token. An expired token returns 401, and the frontend sends the user to login and back (`UI-UX.md` 7.1).
- No CORS (one origin through the Vite proxy). `SameSite=Strict` plus same origin is the CSRF protection, so Spring's CSRF token is turned off *(my decision)*.
- Passwords: at least 8 characters, BCrypt. **Login throttle:** an in-memory counter per login ID, blocking login for 5 minutes after 5 failures. The error message is the same for "unknown user" and "wrong password". The counter resets on restart (accepted for a laptop-only app).
- Account deletion: `DELETE /api/me` with the password. Verifies the hash, then deletes the `app_user` row. The cascade removes everything else (`database-schema.md` Section 5).

### 6.4 Error handling
- Spring Problem Details (RFC 7807): `type`, `title`, `status`, `detail`. `detail` is plain language the UI can show under the failed action (`UI-UX.md` 7.1).
- `type` is a short stable code the UI can branch on (`validation`, `unauthorized`, `not-found`, `login-taken`, `role-exists`, `session-open`, `session-closed`, `session-not-completed`, `file-too-large`, `not-a-pdf`, `wrong-password`, `throttled`, `ai-unavailable`, `server-error`). The full table with statuses is in `api-contracts.md` 1.1. A hard AI failure with no fallback returns 503 `ai-unavailable` with a plain `detail` (for example "Voice transcription is not available right now. Type your answer instead.").
- Nothing in an error body contains resume text, answers or AI replies.

### 6.5 Logging
- Allowed in logs: user ID, resume ID, session ID, endpoint, HTTP status, provider name, duration, token counts, error type.
- Not allowed in logs, at any level: resume text, answers, transcripts, prompts, AI replies, passwords, tokens, API keys. AI request and response logging stays off, including in debug.
- A unit test checks that the exception handler and the `AiClient` do not put the request body in a log message.

### 6.6 Configuration keys
| Key | Purpose |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | MySQL connection |
| `JWT_SECRET` | signs the session token |
| `GROQ_API_KEY` | Groq chat and Whisper |
| `GOOGLE_AI_KEY` | Gemma on Google AI Studio |
| `app.ai.timeout-seconds` | default 20 *(Q6)*. Whisper uses 40 *(my decision, audio upload is slower)* |
| `app.ai.gemma-spacing-seconds` | default 7 *(Q20)* |
| `app.ai.models.*` | model IDs for each use. Check current IDs before coding |
| `app.upload.max-bytes` | default 1,048,576 (1 MB, Q13) |
| `app.voice.max-seconds` | default 180 (Q8) |
| `app.cookie.secure` | default false on localhost |

## 7. Pipelines

### 7.1 Resume (FR-2 to FR-8)
1. **Browser parse.** The copied OpenResume parser reads the chosen PDF in the browser. The file must be a PDF (header `%PDF`) and at most 1 MB, checked in the browser first and again on the server.
2. **Tidy, then looks incomplete?** The app first tidies the browser result: skill lines such as "Languages: Java, C" are split into single skills, link URLs are read from the PDF's link annotations, a duplicated degree/date is split, a project title swapped with its technology line is put back, and technology lines are not kept as resume points. The fallback then runs if the result has no name, or has neither experience nor projects, or yields fewer than 3 resume points in total (Q13), **or is low quality**: an education entry without a school, or a project without a name.
3. **Fallback.** `POST /api/resumes/parse-fallback` sends the PDF. The backend extracts text and hidden links with PDFBox, removes phone and email, and asks Groq (temperature 0) for the same JSON structure, with `null` for anything missing. The file is discarded after the request. If AI is unavailable the user gets the empty Review form (an inline message says so, `UI-UX.md` 7.1).
4. **Review form.** The user edits and saves. `POST /api/resumes` sends the parsed JSON, the target role ID and the optional interview date.
5. **Save.** In one transaction: insert `resume`, then `resume_point` rows (one per project bullet, experience bullet and skill, in order). Skills are matched to `skill` by case-insensitive name; `skill_id` stays empty when there is no match, and **no skill is ever created from a resume** (Q14). The resume is now **read-only** except `interview_date`.
6. **Key facts.** After saving, the backend asks Groq for key facts, one resume point at a time. A success sets `key_facts_status = 'ready'`. A failure increments `key_facts_tries` and sets `last_try_at`, and the point stays `pending` (FR-8). `pending` points are retried when the user opens the Resume list or starts a session, at most one at a time, and not before 10 minutes after `last_try_at` *(my decision)*.
7. **Role skills check.** `GET /api/resumes/{id}/role-skills-check` compares resume points that have a `skill_id` with the target role's skills, stores the JSON in `gap_result` and returns it.

### 7.2 Building a session (FR-9, FR-13, Section 10)
1. `POST /api/sessions` with a resume ID. If the user already has an `in_progress` session the request returns 409 (the unique index also enforces it).
2. **Voice available?** If Whisper is on cooldown (see 7.5) or unconfigured, the voice part is left out: `voice_skipped = true`, 16 slots, `max_marks = 50`.
3. **Per part, pick candidates** from `question` of this resume:
   - **Unmastered:** `mastered = false AND times_asked > 0`. Order by `COALESCE(last_score, 0)` ascending, then `last_asked_at` ascending. An unscored question (a brief or voice answer graded only by the fallback) counts as 0, so it comes back first for AI grading.
   - **New:** `times_asked = 0`. **Rested:** `mastered = true AND last_asked_at < now - 1 month`.
   - Slots left after the unmastered ones are filled at random from new plus rested.
   - Quotas inside a part use the theme: brief is 4 technical (theme NULL) plus 2 behavioral, voice is 1 plus 2.
4. **Missing slots** (the pools are too small): generate them (Q12). One Groq call per part, given the resume points with their key facts, returning questions each linked to a `resume_point_id`. Java validates them (right number of options, 1 to 3 correct with an option's own "A)" label removed, accepted answers present, 1 or 2 blanks, linked point exists), saves them to `question` and, if technical, also to `bank_question` without personal text (FR-20). If a call fails or its JSON is invalid after one retry, the slots come from the practice bank, and if the bank has nothing, from the generic templates (FR-20). Points still `pending` for key facts are covered by bank questions for their skill (FR-8).
5. Insert the `practice_session` and its `attempt` rows (slots), set `resume.last_used_at`, set each chosen question's `last_asked_at` and `times_asked`. Return the session with the first question.

### 7.3 Answering (FR-10, FR-15)
- `PUT /api/sessions/{id}/attempts/{position}` saves one answer when the user presses Next: selected options, blank answers, or text. The session must be `in_progress`.
- **Voice:** the browser records WebM/Opus up to 180 seconds and stops automatically. `POST .../transcribe` uploads it (multipart, at most 5 MB). The backend sends it to Groq Whisper (the file is held in memory or a temp file and deleted right after), counts words, duration, words per minute and filler words from the transcript and timestamps, and returns the transcript and stats. **Nothing is saved yet.** When the user presses Submit on the transcript, the normal `PUT` saves the transcript and stats.
- If Whisper fails or the microphone is denied, the user types the answer. The attempt is saved with `answered_via = 'typed'` and `max_marks = 5` (FR-20).
- `POST .../abandon` marks the session `abandoned`. `POST .../submit` marks every unanswered slot as unanswered (score 0) and starts grading.

### 7.4 Grading and feedback (Section 9, Q20)
`POST /api/sessions/{id}/submit` sets `completed`, returns at once, and hands the session to a background task on a **single-thread executor**. Order:

1. **Java** marks multiple-correct: `marks = max(0, 2 × (correct picked − wrong picked) / correct options)`, rounded to the nearest 0.5. Examples: both correct picked gives 2, one correct and one wrong gives 0, one correct of two gives 1.
2. **Java** matches fill-in-the-blank answers: trim and ignore case against the accepted wordings for each blank. One blank is worth 2 marks, two blanks 1 mark each.
3. **One Groq call** re-checks all blanks Java marked wrong, for meaning. A "yes" gives the blank its marks. If Groq is unavailable they stay wrong.
4. **Groq grades each brief and voice answer, one at a time** (a 5 or 10 item yes/no checklist with quoted proof). Then Java:
   - flips any "yes" whose quote is not found in the user's answer (after trimming and lowercasing) to "no" (evidence rule),
   - counts the ticks and sets the score. **Marks are always summed by Java, never taken from the AI.**
   If Groq is unavailable or its JSON is invalid after one retry, `KeywordGrader` ticks items by keyword match against the key facts and keywords, with the matching sentence as the quote, and `graded_by = 'fallback'`.
5. **Mastery update** on each graded attempt (the rule in `database-schema.md` 3.13): multiple-correct and fill-in-the-blank always count, brief and voice count only when `graded_by = 'ai'`. A counting attempt sets `question.last_score` and `mastered` (2 of 2, 2 of 2, 4 of 5, 8 of 10, and 4 of 5 for a typed voice answer).
6. Set `total_marks` (sum of `score`, with unanswered as 0).
7. **Gemma writes feedback**, one brief or voice answer at a time, with a fixed 7-second gap between calls. The prompt carries the question, the answer, the checklist result, the key facts and (for voice) the stats numbers. A 429 reply honors `Retry-After` once, then that answer gets `TemplateFeedback`. Each attempt moves `feedback_status` from `pending` to `ready` (or `none` for template text that needs no AI).
8. **Session report:** one Gemma call for the report text. `report_status` becomes `ready` (or `none` for a numbers-only report).

The report page polls `GET /api/sessions/{id}` every 2 to 3 seconds while `report_status` or any `feedback_status` is `pending`, so rows fill in gradually (`wireframes.md` W9). **Recovery:** on startup, any completed session with a `pending` status is resumed from where it stopped.

### 7.5 AI gate and fallback (FR-20, Q6, Q11)
- `AiClient` is the only code that calls a provider. For every call it: removes phone numbers and emails from outgoing text, sets temperature 0, asks for JSON only, applies the timeout, parses and validates the reply (Q7), and retries once on a 5xx, a timeout or invalid JSON.
- `AiGate` keeps in-memory state per provider (`groq-chat`, `groq-whisper`, `gemma`). When a provider returns HTTP 429 or a daily-limit error it is marked unavailable: for the reply's `Retry-After` seconds for a per-minute limit (**15 minutes** when there is no `Retry-After`), **until the next UTC midnight** for a daily limit. While unavailable the gate answers "unavailable" at once, with no call and no wait. State resets on restart.
- The three services pick an implementation per call:

| Interface | AI implementation | Fallback implementation |
|---|---|---|
| `QuestionService` (questions, key facts, role skill drafts) | Groq | `BankQuestionService` (practice bank, then generic templates). Key facts and role skill drafts have no fallback: they stay `pending` or empty |
| `GraderService` (checklists, blank re-check) | Groq | `KeywordGrader` (blank re-check has none: blanks stay wrong) |
| `FeedbackService` (feedback, report) | Gemma | `TemplateFeedback` (ticks, key facts, "what to fix" from missed items, numbers only) |

- The switch is **silent** to the user. `attempt.graded_by` records `ai` or `fallback` for exports. A hard failure with no fallback (voice with no Whisper) shows the inline error (Problem Details 503).

### 7.6 Export and deletion
- **Export:** `GET /api/sessions/{id}/export`. Only a completed session of the signed-in user. Built on request from the tables, returned as UTF-8 JSON with a `Content-Disposition` download header. Sections in the PRD order (resume, gap check, questions and answers, grades, feedback, story bank), plus a `description` and metadata block. Phone and email are removed from the resume part (the same scrubber as the AI client). No audio. Nothing is stored.
- **Deletion:** see 6.3. After the delete the cookie is cleared.

## 8. API

All under `/api`, JSON, cookie auth except signup and login. Errors are Problem Details. Resources of another user return 404. **The exact request and response of each endpoint, the error codes and the validation limits are in `api-contracts.md`.**

| Method and path | Purpose | Notes |
|---|---|---|
| `POST /auth/signup` | create account and log in | login ID unique (409), password 8 to 72 characters |
| `POST /auth/login` | log in | sets the cookie. Throttled |
| `POST /auth/logout` | log out | clears the cookie |
| `GET /me` | current user | |
| `DELETE /me` | delete account | body: password |
| `GET /roles?q=` | search roles | names and aliases, starter plus own roles |
| `POST /roles/draft-skills` | AI skill draft for a role name | empty list if AI unavailable |
| `POST /roles` | add a private role | near-duplicate check |
| `POST /resumes/parse-fallback` | parse a PDF with PDFBox and Groq | multipart, 1 MB, returns parsed JSON |
| `POST /resumes` | save reviewed resume | parsed JSON, role ID, optional interview date |
| `GET /resumes` | list with mastered counts | triggers retry of `pending` key facts |
| `GET /resumes/{id}` | one resume (read-only) | |
| `PATCH /resumes/{id}` | change interview date | the only editable field |
| `DELETE /resumes/{id}` | delete resume and its data | cascade |
| `GET /resumes/{id}/role-skills-check` | role skills check | stores `gap_result` |
| `POST /sessions` | start a session | body: resume ID. 409 if one is unfinished |
| `GET /sessions` | history | |
| `GET /sessions/{id}` | session, questions, answers, report | used for polling |
| `PUT /sessions/{id}/attempts/{position}` | save one answer | |
| `POST /sessions/{id}/attempts/{position}/transcribe` | audio to transcript and stats | multipart, not saved |
| `POST /sessions/{id}/submit` | finish and grade | returns at once |
| `POST /sessions/{id}/abandon` | leave the session | |
| `GET /sessions/{id}/export` | export JSON | completed only |
| `GET/POST /stories`, `GET/PUT/DELETE /stories/{id}` | story bank | tags and resume point links in the body |

## 9. Frontend design

- **Routes** follow `wireframes.md`: `/` (W13 landing page, public), `/login`, `/practice` (W2 Home), `/resumes` (W3), `/resumes/new` (W4), `/resumes/new/review` (W6, not a route yet: the review form needs the picked file in memory, which a refresh would lose), `/resumes/:id` (open), `/resumes/:id/role-skills` (W7), `/practice/:id` (W8, no top bar), `/sessions/:id/report` (W9), `/history` (W10), `/stories`, `/stories/new`, `/stories/:id/edit` (W11), `/account`, `/account/delete` (W12).
- **API layer:** `src/api/client.js` wraps `fetch` (same origin, cookie sent), turns Problem Details into an error with `detail`, and on 401 stores the current route and sends the user to `/login`, then back.
- **Data:** TanStack Query for reads, polling (`refetchInterval` on the report while anything is `pending`) and refreshing lists after a save. Form state is plain React state. No Redux.
- **Parser:** `src/lib/openresume/` holds the copied parser with its license notice. A thin function turns its result into the JSON shape in `database-schema.md` 4.1 and applies the "looks incomplete" test (7.1).
- **Voice:** a `useRecorder` hook around `MediaRecorder` (WebM/Opus) with a 180-second countdown, the stop button, and the typed fallback when the permission is denied or transcription fails (`wireframes.md` 8e).
- **Styling:** `tokens.css` holds the CSS custom properties from `UI-UX.md` Section 4. No gradients (translucent materials allowed, see `UI-UX.md` Section 4). Icon plus word for every status.
- **Accessibility:** WCAG 2.1 AA (`UI-UX.md` Section 8): visible focus, labels on every input, `aria-expanded` on report rows, `role="progressbar"` on the session progress, focus moved into and restored from panels and dialogs.

## 10. Security and privacy

- Keys and secrets live only in the backend environment. Nothing secret reaches the frontend or the repository (`.env.example` lists names only).
- Data isolation on every request (6.2). Tested with two users (Section 12).
- Phone and email are removed from every outgoing AI text (`AiClient`) and from the export. They stay in the stored resume.
- The PDF and the audio are never stored.
- Hashed passwords, HttpOnly cookie, Strict same-site, login throttle.
- Logs hold no personal content (6.5).
- Google AI Studio's free-tier data handling must be checked before sending real resumes (PRD risk).

## 11. Licensing

- The project is **AGPL-3.0** (Q1). `LICENSE` at the repo root names Paras Panchal as copyright holder *(my decision, from the PRD owner)*.
- OpenResume's own license and copyright notice stay in `frontend/src/lib/openresume/`. The README says the parser comes from OpenResume (link) and that the app is meant for localhost use.
- Third-party libraries keep their own licenses. PDF.js and the Spring and React libraries are compatible with AGPL-3.0 use. Re-check the license of any library added later.
- This is not legal advice. Check the terms again before you share or host the app for other people.

## 12. Testing

| Layer | Tool | What it covers |
|---|---|---|
| Marking and rules | JUnit 5 | the multiple-correct formula, fill-in-the-blank matching and marks per blank, the evidence rule (a "yes" with a missing quote flips to "no"), mastery rules (including "fallback grades do not count"), session slot selection and quotas, the typed voice answer (5 marks, mastery 4 of 5), voice-skipped sessions (50 marks) |
| Database | Testcontainers, MySQL 8 | runs `V1__schema.sql` and `V2`, the generated columns, the unique open-session index, cascades on account deletion, the session-building queries |
| AI layer | JUnit 5 with a fake provider | invalid JSON then retry then fallback, 429 sets the cooldown, the PII scrubber, no personal text in logs |
| Security | JUnit 5 and MockMvc | another user's resume or session returns 404, logged-out returns 401, login throttle, the export only for completed sessions |
| Frontend | Vitest | the "looks incomplete" test, parse-to-JSON mapping, the API wrapper's error handling, the recorder countdown logic |
| Manual | you | the checklist after every build step (Section 13). Plus the PRD's proposal to hand-grade about 20 answers and compare them with the AI |

No end-to-end browser tests in v1.

## 13. Build steps and manual checks

Order as in PRD Section 6. After each step **I remind you to run its manual checks before the next step starts.** Every step also ends with a check of a second user's data isolation and a look at the new tables in MySQL.

### Step 1: Login (FR-1, FR-18)
- **Build:** Flyway `V1` and the Spring Boot project, `app_user`, signup, login, logout, `/me`, account deletion, the cookie filter, the login throttle, the Problem Details handler, the page shell (W0), W1, and the Account screens (W12).
- **Automated:** the security tests in Section 12, `V1` runs on MySQL 8.
- **Manual checks:**
  1. Sign up, log out, log in. The top bar shows.
  2. Sign up with the same login ID in a different letter case: it is refused.
  3. Log in with a wrong password 5 times: the sixth is blocked for 5 minutes. The message does not say which part was wrong.
  4. Open a page while logged out: you go to login and come back after logging in.
  5. In MySQL: `SELECT login_id, password_hash FROM app_user;` shows a BCrypt hash, never the password.
  6. Delete the account with the wrong password (refused), then the right one: the row is gone and you are logged out.
- **Done when:** a user can sign up, log in, log out and delete their account, and the database shows only a hash.

### Step 2: Resume parser (FR-2 to FR-8, FR-16)
- **Build:** `V2` starter roles, role search, add a missing role with AI draft, upload (W4), the OpenResume copy, fallback parse, Review form (W6), save with resume points, key facts with `pending` retry, role skills check (W7), Resume list (W3).
- **Automated:** the resume tests (incomplete test, points creation, no new skills from resumes), the Testcontainers cascade test.
- **Manual checks:**
  1. Search "SDE", "Backend" and a misspelling: results come from names and aliases.
  2. Add a missing role: AI skills appear and can be edited. Remove the Groq key and try again: the list starts empty and you can type skills.
  3. Upload a file over 1 MB and a non-PDF: each shows an inline message.
  4. Upload a clear single-column resume: it fills the Review form with nothing missing. Upload a two-column resume: the fallback runs and the "please check" banner shows.
  5. Edit and save. In MySQL check `resume`, `resume_point` (one row per bullet and skill) and `key_fact`. No row appears in `skill` that was not there before.
  6. Remove the Groq key and save another resume: it is saved, key facts are `pending`, and they fill in after the key is back and you open the Resume list.
  7. Open the role skills check and compare it by eye with the resume.
  8. Delete the resume: its points, key facts and results are gone.
  9. Log in as a second user and open the first user's resume URL: "not found".
- **Done when:** a PDF becomes a saved resume with resume points and key facts, and the role skills check matches your own reading.

### Step 3: Questions (FR-9, FR-10, FR-13, FR-20 generation)
- **Build:** session building and generation, the practice bank tables in use (the seed is built later), the practice session screens (W8 a to f), voice recording and transcription, autosave, abandon, Home (W2), History list (W10).
- **Automated:** the session-selection and quota tests, the unique open-session index, the voice-skipped test, the fake-provider tests.
- **Manual checks:**
  1. Start a session: 19 slots in four parts, 5 multiple-correct, 5 fill-in-the-blank, 6 brief (4 technical, 2 behavioral), 3 voice.
  2. Answer a few, close the tab, open Home: "Continue your practice session" shows and your answers are still there.
  3. Start a second session while one is open: it is refused.
  4. Leave the session: it shows as abandoned in History.
  5. Record a voice answer: the transcript shows, Re-record and Submit work, and the recording stops at 3 minutes.
  6. Deny the microphone: the typed answer box appears.
  7. Stop the Whisper key: a new session has 16 questions and 50 marks, and History says "voice part skipped".
  8. Stop the Groq key: a session still starts using bank or generic questions, with no message.
  9. In MySQL check `question` (text, links) and `attempt` (one row per slot).
- **Done when:** a session can be started, answered, saved and resumed, with and without AI.

### Step 4: Feedback (FR-11 to FR-15, Section 9 and 10)
- **Build:** the grading pipeline, `GraderService`, `FeedbackService`, `KeywordGrader`, `TemplateFeedback`, the AI gate, the report (W9), voice stats, mastery updates, repetition.
- **Automated:** the marking, evidence-rule and mastery tests, the AI-layer tests.
- **Manual checks:**
  1. Submit a session: the report opens at once and fills row by row.
  2. Check a multiple-correct mark by hand with the formula (one correct of two gives 1). Check a fill-in-the-blank with a different case and a synonym.
  3. Open a brief answer: every tick has a quote that is really in your answer. Write an answer with no facts and check that nothing is ticked.
  4. Check the voice stats against your own recording (duration, word count).
  5. Run a session with the Groq key removed: the report still shows marks, ticks and template feedback, with nothing telling you AI was off.
  6. Check `attempt.graded_by` in MySQL (`java`, `ai`, `fallback`).
  7. Start a second session: unmastered questions come back first with the same text, mastered ones are not repeated this soon.
  8. Hand-grade about 20 answers yourself and compare them with the AI marks (PRD Section 15).
- **Done when:** the report matches your own marking and weak questions return.

### Step 5: Story bank (FR-17)
- **Build:** stories, tags, links, the Story bank screens (W11), "Save as story" from a behavioral answer.
- **Automated:** the cascade test for stories and links, the tag filter query.
- **Manual checks:**
  1. Save a behavioral answer as a story: the form opens with the answer in the Action box.
  2. Create, edit and delete a story. Filter by a tag.
  3. Link a story to resume points, then delete the resume: the links are gone and the story stays.
  4. A second user cannot see or open your story.
- **Done when:** stories can be saved from answers and reused.

### Step 6: Export JSON (FR-19)
- **Build:** the export builder, the History actions (W10).
- **Automated:** the export tests (completed only, phone and email absent, `graded_by` present, no audio field).
- **Manual checks:**
  1. Export a completed session: the file has all six sections in order, the description and the metadata.
  2. Search the file for your phone number and email: neither is there.
  3. Try to export an abandoned or unfinished session: it is refused.
  4. Open the file in another AI tool and check that the description makes sense to it.
  5. Final check: delete the account and run a row count on every table for that user ID: all zero. The shared tables (`skill`, starter `role`, `bank_question`) are untouched.
- **Done when:** a session exports cleanly and an account deletion leaves nothing behind.

## 14. Risks and open items

| Item | Notes |
|---|---|
| `V1__schema.sql` is not yet run against a real MySQL | Step 1 starts by running it on MySQL 8.0.16+ and fixing any error |
| Spring Boot version and AI model IDs | Pin the current stable Spring Boot and check each provider's current model IDs and free limits at project start (PRD Section 11 says they change often) |
| Gemma pacing may need tuning | 7 seconds is an estimate. Measure real token use per call |
| In-memory throttle and AI gate reset on restart | Accepted for a laptop-only app |
| AGPL | Applies as long as the parser code is in the app (Section 11). Not legal advice |
| `bank_starter.csv` | Drafted in `backend/src/main/resources/db/seed-draft/` (software development only), needs your review before `V3__starter_bank.sql` is moved to `db/migration/`. Until then fallback questions come from AI-saved bank rows and the generic templates |
| `roles_starter.csv` | The exact content is in `database-schema.md` Section 12. Needs your review |

## 15. Change log

**v1** (3 October 2026): created from the TRD grilling session (Q1 to Q23).

**v1.1** (3 October 2026): added `api-contracts.md`. Signup now logs the user in, password limit 72, voice upload limit 5 MB, error codes defined, and a session in progress hides correct answers and key facts.
