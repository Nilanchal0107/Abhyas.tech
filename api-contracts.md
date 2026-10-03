# API contracts: Abhyas.tech

**Status:** Draft v1, from the grilling session on 3 October 2026. Items marked *(my decision)* were filled in by me and can be changed.
**Related:** `trd.md` (design, Section 8 lists the endpoints), `prd.md` (FR numbers), `wireframes.md` (screens W0 to W12), `database-schema.md` (tables), `UI-UX.md`.

This document fixes the exact request and response of every endpoint, so the frontend and the backend can be built separately.

---

## 1. Shared rules

| Rule | Detail |
|---|---|
| Base path | `/api`. No version prefix. |
| Format | JSON, UTF-8, `Content-Type: application/json`. Two endpoints use `multipart/form-data` (the PDF fallback and the voice upload). |
| Auth | The `rdt_session` cookie (HttpOnly, SameSite=Strict, 8 hours). Every endpoint needs it except `POST /auth/signup` and `POST /auth/login`. A missing or expired cookie returns `401 unauthorized`. |
| Field names | `camelCase`. |
| Enums | Exactly as in the database: `multiple_correct`, `fill_blank`, `brief`, `voice`, `in_progress`, `completed`, `abandoned`, `openresume`, `ai_fallback`, and so on. |
| IDs and marks | Numbers. Marks can end in `.5`. |
| Timestamps | ISO-8601 UTC with milliseconds, `2026-10-03T10:15:30.123Z`. Dates are `2026-10-03`. |
| Absent values | `null`, never a missing key and never an empty string. |
| Ownership | A resource that belongs to another user returns `404 not-found`, the same as one that does not exist. |
| Lists | No paging. Newest first. Only `GET /sessions` takes `status` and `limit`. |
| Unknown fields | A request body field the endpoint does not list is ignored. |
| Personal content | Error bodies and logs never contain resume text, answers or AI replies. |

### 1.1 Errors (RFC 7807 Problem Details)

```json
{
  "type": "validation",
  "title": "Check the form",
  "status": 400,
  "detail": "Some fields need attention.",
  "errors": [{ "field": "password", "message": "Use at least 8 characters." }]
}
```

`type` is a short stable code. The UI shows `detail` as the inline message under the failed action (`UI-UX.md` 7.1) and uses `type` for special cases. `errors` appears only for `validation`. Content type of an error is `application/problem+json`.

| `type` | Status | When | Extra fields |
|---|---|---|---|
| `validation` | 400 | a field is missing, too long or malformed | `errors[]` |
| `not-a-pdf` | 400 | the upload does not start with `%PDF` | |
| `wrong-password` | 400 | account deletion with the wrong password | |
| `unauthorized` | 401 | no valid cookie, or wrong login ID or password at login | |
| `not-found` | 404 | the resource does not exist or belongs to another user | |
| `login-taken` | 409 | signup with a login ID already used | |
| `role-exists` | 409 | adding a role whose name or alias matches an existing one | `existingRoleId`, `existingRoleName` |
| `session-open` | 409 | starting a session while one is unfinished, or deleting a resume used by an unfinished session *(my decision)* | `sessionId` |
| `session-closed` | 409 | saving an answer, uploading audio or abandoning for a session that is not `in_progress` | |
| `session-not-completed` | 409 | exporting a session that is not `completed` | |
| `file-too-large` | 413 | the PDF is over 1 MB, or the audio is over 5 MB | |
| `throttled` | 429 | too many failed logins | `retryAfterSeconds` |
| `ai-unavailable` | 503 | a call that has no fallback (voice transcription, the PDF fallback parse) cannot be served | |
| `server-error` | 500 | anything else | |

Login, signup and account deletion never use `401` for a form problem that the page should show inline, except login with wrong credentials (`401 unauthorized`, `detail`: "That user ID or password is not right."). The frontend treats a `401` from `POST /auth/login` as a form error, and a `401` from any other endpoint as an expired login (redirect to `/login` and back).

### 1.2 Validation limits *(checked on the server, shown as inline field errors)*

| Field | Limit |
|---|---|
| Login ID | 3 to 50 characters: letters, digits, `_`, `.`, `-` |
| Password | 8 to **72** characters (BCrypt reads only 72 bytes) |
| Role name | 2 to 100. Alias or skill name 1 to 100. At most 30 skills and 10 aliases when adding a role |
| Resume | at most 50 bullets per entry, 200 resume points in total, each bullet up to 500 characters, up to 100 skills |
| PDF | `application/pdf`, header `%PDF`, 1 MB (1,048,576 bytes) |
| Brief answer or typed voice answer | up to 5,000 characters |
| Fill-in-the-blank answer | up to 200 characters per blank |
| Voice audio | `audio/webm`, up to 3 minutes (180 s) and 5 MB |
| Story | title 1 to 200, each STAR box and lesson up to 5,000, up to 10 tags of 1 to 40 characters, up to 20 linked resume points |
| Search query `q` | 1 to 100 characters |

## 2. Shared types

### 2.1 `ParsedResume`
The reviewed resume. Built in the browser from OpenResume's output, and also the response of the PDF fallback. The server stores only these fields and drops anything else.

```json
{
  "profile": {
    "name": "Asha Rao",
    "email": "asha@example.com",
    "phone": "+91 98765 43210",
    "location": "Pune",
    "links": [{ "label": "GitHub", "url": "https://github.com/asha" }],
    "summary": null
  },
  "education": [{ "school": "...", "degree": "...", "date": "2020 - 2024", "details": ["..."] }],
  "experience": [{ "company": "Acme Corp", "title": "Backend Developer", "date": "2024 - now", "bullets": ["..."] }],
  "projects": [{ "name": "Order Service", "date": null, "bullets": ["..."] }],
  "skills": ["Java", "Spring Boot", "MySQL"]
}
```

All string fields can be `null`. Arrays can be empty. Resume points are created from `experience[].bullets`, `projects[].bullets` and `skills[]`, in that order.

### 2.2 `ResumeSummary`
```json
{ "id": 7, "fileName": "Paras_Resume.pdf", "targetRole": { "id": 12, "name": "Backend Developer" },
  "interviewDate": "2026-11-20", "parseSource": "openresume", "pointCount": 38,
  "questionCount": 31, "masteredCount": 14,
  "createdAt": "2026-10-03T10:15:30.123Z", "lastUsedAt": null }
```

### 2.3 `Question` (as shown to the user)
```json
{ "id": 301, "type": "multiple_correct", "theme": null, "isBehavioral": false,
  "text": "In Order Service you used Spring Data JPA. Which statements about lazy loading are true?",
  "options": [{ "key": "A", "text": "..." }, { "key": "B", "text": "..." }, { "key": "C", "text": "..." }, { "key": "D", "text": "..." }],
  "blankCount": null }
```
- `options` is set only for `multiple_correct` (4 options). `blankCount` (1 or 2) only for `fill_blank`. For a fill-in-the-blank question the `text` contains `________` once per blank.
- `theme` is one of `teamwork`, `conflict`, `failure`, `leadership`, `deadline_pressure`, and is `null` for technical questions. `isBehavioral` is `theme != null`.

### 2.4 `Answer`
```json
{ "selectedOptions": ["A", "C"], "blankAnswers": null, "text": null,
  "transcript": null, "voiceStats": null, "answeredVia": "click" }
```
- `answeredVia` is `click` (multiple-correct), `typed` (fill-in-the-blank, brief, or a voice question answered by typing) or `voice`.
- `voiceStats` is `{ "wordsPerMinute": 142, "fillerCount": 5, "wordCount": 96, "durationSeconds": 40 }` or `null`.
- An unanswered slot has `answer: null`.

### 2.5 `Attempt` (one question slot)
During an `in_progress` session only the fields the user may see are present:

```json
{ "position": 3, "maxMarks": 2, "question": { "...": "Question" }, "answer": { "...": "Answer" } }
```

Once the session is `completed`, each attempt also has (all `null` while grading is still running):

```json
{
  "score": 1.5,
  "mastered": false,
  "correctOptions": ["A", "C"],
  "acceptedAnswers": null,
  "blankResults": null,
  "checklist": [{ "item": "explains_why", "label": "Explains why", "passed": true, "quote": "I chose Redis because ..." }],
  "keyFacts": ["..."],
  "feedback": { "status": "ready", "text": "...", "whatToFix": "...", "deliveryComment": null }
}
```
- `correctOptions` only for multiple-correct. `acceptedAnswers` (one list per blank) and `blankResults` (`[{ "correct": true }]`) only for fill-in-the-blank. `checklist` and `keyFacts` only for brief and voice.
- `feedback.status` is `pending`, `ready` or `none`. `none` means there is no AI text and the numbers are all that is shown. `deliveryComment` is set only for voice answers.
- **`gradedBy` is never in an in-app response** (silent fallback, FR-20). It appears only in the export.
- `maxMarks` is 2, 5 or 10. A voice question answered by typing has `maxMarks: 5`.
- The checklist `item` keys by question:
  - brief technical: `key_fact_1` to `key_fact_3`, `explains_why`, `consistent_with_resume`
  - brief behavioral: `situation_task`, `action`, `result`, `own_role`, `lesson_learned`
  - voice technical: `key_fact_1` to `key_fact_6` (up to), `explains_why`, `concrete_example`, `own_role`, `consistent_with_resume`
  - voice behavioral: `situation`, `task`, `action`, `result`, `own_role`, `concrete_detail`, `lesson_learned`, `consistent_with_resume`, `answers_question`, `clear_and_focused`
  - the number of items equals the marks available for that attempt (5 or 10).
- Feedback and report text are stored as JSON text in `attempt.feedback_text` and `practice_session.report_text` *(my decision, no schema change)*: `{"text":"...","whatToFix":"...","deliveryComment":null}` and `{"headline":"...","summary":"..."}`. The template fallback fills the same shape.

### 2.6 `SessionSummary` (History and Home)
```json
{ "id": 41, "status": "completed", "startedAt": "...", "completedAt": "...",
  "resume": { "id": 7, "fileName": "Paras_Resume.pdf" }, "role": { "id": 12, "name": "Backend Developer" },
  "voiceSkipped": false, "maxMarks": 80, "totalMarks": 61.5, "answeredCount": 19, "questionCount": 19 }
```
`totalMarks` is `null` unless the session is `completed` and graded.

### 2.7 `SessionDetail`
```json
{
  "id": 41, "status": "completed", "startedAt": "...", "completedAt": "...",
  "resume": { "id": 7, "fileName": "Paras_Resume.pdf" }, "role": { "id": 12, "name": "Backend Developer" },
  "voiceSkipped": false, "maxMarks": 80, "totalMarks": 61.5,
  "gradingComplete": true,
  "report": { "status": "ready", "headline": "Nice start. Three resume points still need work.", "summary": "..." },
  "marksByPart": [{ "type": "multiple_correct", "marks": 8, "maxMarks": 10 }],
  "masteredCount": 9,
  "weakestPoints": [{ "id": 55, "text": "Designed REST APIs for ..." }],
  "attempts": [ "Attempt", "..." ]
}
```
- While `in_progress`: `totalMarks`, `report`, `marksByPart`, `masteredCount`, `weakestPoints` are `null`, `gradingComplete` is `false`, and `attempts` carry only the in-progress fields.
- `report.status` is `pending`, `ready` or `none`. `gradingComplete` is `true` when the session is `completed`, `totalMarks` is set, `report.status` is not `pending`, and no attempt has `feedback.status = "pending"`.
- `marksByPart` has no `voice` row when `voiceSkipped` is true. `maxMarks` is 80, or 50 when the voice part was skipped.
- `weakestPoints`: up to 3 resume points behind the lowest-scoring unmastered questions *(my decision)*.

## 3. Auth

### `POST /auth/signup` (W1)
Request `{ "loginId": "paras24", "password": "..." }`. Creates the account **and logs in** (sets the cookie).
Response `201`: `{ "user": { "id": 3, "loginId": "paras24" } }`.
Errors: `validation` (loginId or password rule), `login-taken`.

### `POST /auth/login` (W1)
Request `{ "loginId": "...", "password": "..." }`. Response `200`: `{ "user": { "id": 3, "loginId": "paras24" } }` and the cookie.
Errors: `unauthorized` (same message for unknown user and wrong password), `throttled` (`retryAfterSeconds`, 5 failures blocks 5 minutes).

### `POST /auth/logout`
No body. Response `204`. Clears the cookie. Repeating it is fine.

### `GET /me` (W0)
Response `200`: `{ "id": 3, "loginId": "paras24", "createdAt": "..." }`. Used on app load to know whether the user is logged in.

### `DELETE /me` (W12b)
Request `{ "password": "..." }`. Response `204`, cookie cleared, all data deleted by cascade.
Errors: `wrong-password` (400, never 401, so the page does not mistake it for an expired login).

## 4. Roles (FR-3, W4, W5)

### `GET /roles?q=sde` (W4)
Starter roles plus the user's own roles whose name or alias matches `q` (case-insensitive, contains), at most 20, own roles marked.
Response `200`:
```json
{ "roles": [
  { "id": 4, "name": "Software Engineer", "category": "Software Development",
    "matchedOn": "alias", "matchedAlias": "SDE", "isOwn": false }
] }
```
`matchedOn` is `name` or `alias`. An empty list means nothing matched, and the UI then offers "Add a missing role". Errors: `validation` (`q` missing or longer than 100).

### `POST /roles/draft-skills` (W5)
Request `{ "name": "Prompt engineer" }`. Response `200`: `{ "skills": ["Python", "LLM APIs", "Evaluation"] }`. When AI is unavailable the list is empty and the response is still `200` (the user types the skills, FR-3).

### `POST /roles` (W5)
Request `{ "name": "Prompt engineer", "aliases": ["PE"], "skills": ["Python", "LLM APIs"] }`. Creates a role owned by the user. Skills are matched to existing `skill` rows by case-insensitive name and new ones are created (this is the only place a skill is created from user input, and only for the user's own roles *(my decision)*).
Response `201`: `{ "role": { "id": 61, "name": "Prompt engineer", "category": null, "aliases": ["PE"], "skills": ["Python", "LLM APIs"], "isOwn": true } }`.
Errors: `validation`, `role-exists` (409, with `existingRoleId` and `existingRoleName`, so the UI can offer "Use it instead").

## 5. Resumes (FR-2 to FR-8, FR-16, W3, W4, W6, W7)

### `POST /resumes/parse-fallback` (W4)
`multipart/form-data` with one field `file` (the PDF). The PDF is discarded after the request. Response `200`: `{ "resume": { "...": "ParsedResume" } }` (missing fields are `null`).
Errors: `not-a-pdf`, `file-too-large` (over 1 MB), `ai-unavailable` (the UI shows the empty Review form with an inline message).

### `POST /resumes` (W6 Save and continue)
```json
{ "fileName": "Paras_Resume.pdf", "targetRoleId": 12, "interviewDate": "2026-11-20",
  "parseSource": "openresume", "parsed": { "...": "ParsedResume" } }
```
Creates the resume, then its resume points in one transaction, then starts key-fact drafting (a `pending` point is retried later). The resume is read-only afterwards.
Response `201`: `{ "resume": { "...": "ResumeSummary" } }`.
Errors: `validation` (limits in 1.2), `not-found` (the role does not exist or is another user's private role).

### `GET /resumes` (W3, W2)
Response `200`: `{ "resumes": [ ResumeSummary, ... ] }`, the most recently used first (`lastUsedAt` descending, never-used ones by `createdAt`). Also triggers a retry of `pending` key facts for the user's resumes, one point at a time (FR-8).

### `GET /resumes/{id}` (W3 Open)
Response `200`:
```json
{ "resume": { "...": "ResumeSummary",
    "parsed": { "...": "ParsedResume" },
    "points": [{ "id": 55, "kind": "project_bullet", "text": "Designed REST APIs for ...", "position": 12, "skillId": 8 }] } }
```
Read-only. `kind` is `project_bullet`, `experience_bullet` or `skill`. The key-fact status is not exposed. Errors: `not-found`.

### `PATCH /resumes/{id}`
Request `{ "interviewDate": "2026-12-01" }` or `{ "interviewDate": null }`. The only editable field. Response `200`: `{ "resume": { "...": "ResumeSummary" } }`. Errors: `validation`, `not-found`.

### `DELETE /resumes/{id}` (W3)
Response `204`. Deletes the resume with its points, key facts, role skills result, questions, sessions and attempts. Errors: `not-found`, `session-open` (an unfinished session uses this resume, `sessionId` is returned).

### `GET /resumes/{id}/role-skills-check` (W7)
Computes the check, stores it in `gap_result`, and returns it.
```json
{ "resumeId": 7, "role": { "id": 12, "name": "Backend Developer" }, "computedAt": "...",
  "withEvidence": [{ "skillId": 3, "skill": "Java", "resumePoints": [{ "id": 55, "text": "Designed REST APIs for ..." }] }],
  "withoutEvidence": [{ "skillId": 31, "skill": "Docker" }] }
```
Both lists can be empty (if the resume has no matched skills, the UI shows the empty state in W7). Errors: `not-found`.

## 6. Practice sessions (FR-9 to FR-15, W2, W8, W9, W10)

### `POST /sessions` (W2, W3 "Practice with this resume")
Request `{ "resumeId": 7 }`. Builds the 19 slots (or 16 without the voice part) with the rules in `trd.md` 7.2, generating missing questions first. This call can take several seconds ("Preparing your questions..."). Response `201`: `{ "session": SessionDetail }` with all `attempts` (every slot's question and no answers yet).
Errors: `not-found` (resume), `session-open` (`sessionId`).

### `GET /sessions?status=completed&limit=1` (W2, W10)
Query `status` is `in_progress`, `completed` or `abandoned` (optional), `limit` 1 to 50 (optional, default all). Response `200`: `{ "sessions": [ SessionSummary, ... ] }`, newest first. Errors: `validation` (bad `status` or `limit`).

### `GET /sessions/{id}` (W8 resume, W9 report, polling)
Response `200`: `{ "session": SessionDetail }`. While `in_progress` it never contains correct answers, key facts, checklists or marks (Q5). Errors: `not-found`.

**Polling (W9):** after submit, call this endpoint every 2.5 seconds while `session.gradingComplete` is `false`, then stop. A completed attempt whose `score` is `null` is still being graded; its `feedback.status` stays `pending` until Gemma's text arrives. If the app restarts during grading, the backend finishes it on startup and the same polling works.

### `PUT /sessions/{id}/attempts/{position}` (W8, on Next)
Request `{ "answer": Answer-like }`. Fields by question type:

| Question type | Body |
|---|---|
| `multiple_correct` | `{ "answer": { "selectedOptions": ["A","C"] } }` (keys A to D) |
| `fill_blank` | `{ "answer": { "blankAnswers": ["RestController"] } }` (length equals `blankCount`) |
| `brief` | `{ "answer": { "text": "..." } }` |
| `voice` (recorded) | `{ "answer": { "text": null, "transcript": "...", "voiceStats": { "wordsPerMinute": 142, "fillerCount": 5, "wordCount": 96, "durationSeconds": 40 }, "answeredVia": "voice" } }` |
| `voice` (typed instead) | `{ "answer": { "text": "...", "answeredVia": "typed" } }`. The attempt's `maxMarks` becomes 5 |
| any, to clear it | `{ "answer": null }` |

`answeredVia` is set by the server for the other types. Saving the same body twice gives the same result. Response `200`: `{ "attempt": Attempt }` (in-progress fields only).
The transcript and stats come from the client (the transcribe response). A user could change them, which only affects their own practice grade and is accepted *(my decision)*.
Errors: `validation` (wrong shape for the question type, limits in 1.2, an option key not in the question), `not-found` (no such position), `session-closed`.

### `POST /sessions/{id}/attempts/{position}/transcribe` (W8 voice)
`multipart/form-data` with one field `audio` (WebM/Opus, up to 3 minutes and 5 MB). The audio is held only for the request and deleted. Nothing is saved.
Response `200`:
```json
{ "transcript": "At Acme we had two weeks to ...",
  "stats": { "wordsPerMinute": 142, "fillerCount": 5, "wordCount": 96, "durationSeconds": 40 } }
```
Errors: `validation` (not `audio/webm`, or over 180 seconds, or the question at that position is not a voice question), `file-too-large`, `session-closed`, `ai-unavailable` (the UI switches to the typed answer box, `wireframes.md` 8e).

### `POST /sessions/{id}/submit` (W8 last question)
No body. Marks unanswered slots as zero, completes the session and starts the background grading. Response `202`: `{ "session": SessionDetail }` (`status: "completed"`, `totalMarks: null`, `gradingComplete: false`). Repeating it after submit returns `200` with the current session and **does not start grading again**. Errors: `not-found`, `session-closed` (the session was abandoned).

### `POST /sessions/{id}/abandon` (W8 Leave session, W2)
No body. Response `200`: `{ "session": SessionSummary }` with `status: "abandoned"`. Repeating it returns the same. Errors: `not-found`, `session-closed` (already completed).

### `GET /sessions/{id}/export` (W9, W10)
Response `200`, `Content-Type: application/json; charset=utf-8`, `Content-Disposition: attachment; filename="practice-session-41-2026-10-03.json"`. Body in Section 8. Errors: `not-found`, `session-not-completed`. Export is allowed once grading is complete; before that the file would lack grades, so it returns `session-not-completed` too *(my decision)*.

## 7. Stories (FR-17, W11)

`Story`:
```json
{ "id": 9, "title": "Fixing the payment outage",
  "situation": "...", "task": "...", "action": "...", "result": "...", "lesson": "...",
  "tags": ["teamwork", "deadline pressure"],
  "resumePoints": [{ "id": 55, "text": "Designed REST APIs for ...", "resumeId": 7 }],
  "sourceAttemptId": 812, "createdAt": "...", "updatedAt": "..." }
```
Only `title` is required. The STAR boxes can be `null`.

### `GET /stories?tag=teamwork` (W11a)
`tag` is optional. Response `200`:
```json
{ "stories": [{ "id": 9, "title": "Fixing the payment outage", "tags": ["teamwork"], "preview": "Our payment service went down ...", "createdAt": "...", "updatedAt": "..." }],
  "tags": ["conflict", "teamwork"] }
```
`preview` is the first 140 characters of the first non-empty of `situation`, `action`. `tags` lists all of the user's distinct tags (for the filter chips).

### `GET /stories/{id}` (W11b)
Response `200`: `{ "story": Story }`. Errors: `not-found`.

### `POST /stories` (W11b, and "Save as story")
```json
{ "title": "...", "situation": null, "task": null, "action": "...", "result": null, "lesson": null,
  "tags": ["teamwork"], "resumePointIds": [55], "sourceAttemptId": 812 }
```
Response `201`: `{ "story": Story }`. `resumePointIds` and `sourceAttemptId` must belong to the user, otherwise `not-found`. `sourceAttemptId` is optional and set by "Save as story" from a behavioral answer. The "Save as story" form is pre-filled in the browser (the answer text in `action`); there is no endpoint for the prefill.
Errors: `validation` (limits in 1.2), `not-found`.

### `PUT /stories/{id}`
Same body as `POST` (full replace of the fields). `sourceAttemptId` is ignored on update. Response `200`: `{ "story": Story }`. Errors: `validation`, `not-found`.

### `DELETE /stories/{id}`
Response `204`. Errors: `not-found`.

## 8. Export JSON (FR-19)

Built on request, never stored. UTF-8. **Phone and email are removed** (the keys are not present), the name, links and answers stay. No audio.

```json
{
  "description": "Practice session export from Abhyas.tech. Sections: resume (the structured resume used), gapCheck (role skills with and without evidence), questionsAndAnswers (every question with the user's answer and the correct answer), grades (marks, checklists with quoted proof, mastered or not, and gradedBy: ai, fallback or java), feedback (per-answer feedback, voice stats and the session report), storyBank (the user's saved STAR stories).",
  "metadata": { "exportedAt": "...", "sessionDate": "2026-10-03", "roleName": "Backend Developer", "voicePartSkipped": false },
  "resume": { "profile": { "name": "Asha Rao", "location": "Pune", "links": [], "summary": null }, "education": [], "experience": [], "projects": [], "skills": [] },
  "gapCheck": { "withEvidence": [], "withoutEvidence": [] },
  "questionsAndAnswers": [
    { "position": 3, "type": "multiple_correct", "theme": null, "question": "...",
      "options": [{ "key": "A", "text": "..." }],
      "userAnswer": { "selectedOptions": ["A"], "blankAnswers": null, "text": null, "transcript": null, "answeredVia": "click" },
      "correctOptions": ["A", "C"], "acceptedAnswers": null }
  ],
  "grades": {
    "sessionTotal": 61.5, "maxMarks": 80,
    "marksByPart": [{ "type": "multiple_correct", "marks": 8, "maxMarks": 10 }],
    "perQuestion": [{ "position": 3, "score": 1, "maxMarks": 2, "mastered": false, "gradedBy": "java",
                      "checklist": null, "blankResults": null }]
  },
  "feedback": {
    "perAnswer": [{ "position": 11, "text": "...", "whatToFix": "...", "deliveryComment": null, "voiceStats": null }],
    "sessionReport": { "headline": "...", "summary": "..." }
  },
  "storyBank": [ "Story, without ids of other tables" ]
}
```
- Sections appear in this order (the PRD order). `storyBank` holds all of the user's stories, not only ones linked to this session.
- `gradedBy` is `java` (multiple-correct, fill-in-the-blank), `ai` or `fallback`. This is the only place it is exposed.
- In `storyBank`, each story is `title`, the STAR boxes, `lesson`, `tags`, and the linked resume point texts, without database IDs.

## 9. Endpoint index (for review)

| Endpoint | Success | Screen |
|---|---|---|
| `POST /auth/signup` | 201 | W1 |
| `POST /auth/login` | 200 | W1 |
| `POST /auth/logout` | 204 | W0 |
| `GET /me` | 200 | W0 |
| `DELETE /me` | 204 | W12b |
| `GET /roles?q=` | 200 | W4 |
| `POST /roles/draft-skills` | 200 | W5 |
| `POST /roles` | 201 | W5 |
| `POST /resumes/parse-fallback` | 200 | W4 |
| `POST /resumes` | 201 | W6 |
| `GET /resumes` | 200 | W2, W3 |
| `GET /resumes/{id}` | 200 | W3 |
| `PATCH /resumes/{id}` | 200 | W3 |
| `DELETE /resumes/{id}` | 204 | W3 |
| `GET /resumes/{id}/role-skills-check` | 200 | W7 |
| `POST /sessions` | 201 | W2, W3 |
| `GET /sessions` | 200 | W2, W10 |
| `GET /sessions/{id}` | 200 | W8, W9 |
| `PUT /sessions/{id}/attempts/{position}` | 200 | W8 |
| `POST /sessions/{id}/attempts/{position}/transcribe` | 200 | W8 |
| `POST /sessions/{id}/submit` | 202 (200 on repeat) | W8 |
| `POST /sessions/{id}/abandon` | 200 | W8, W2 |
| `GET /sessions/{id}/export` | 200 | W9, W10 |
| `GET /stories` | 200 | W11a |
| `GET /stories/{id}` | 200 | W11b |
| `POST /stories` | 201 | W11b, W9 |
| `PUT /stories/{id}` | 200 | W11b |
| `DELETE /stories/{id}` | 204 | W11a |

## 10. Notes for implementation

- **Contract tests:** each backend step (`trd.md` Section 13) adds MockMvc tests that check the status and the JSON keys in this file for its endpoints, plus the 404 for another user's resource and the 401 without a cookie.
- **Frontend wrapper:** `src/api/client.js` maps `type` to behavior: `unauthorized` (not from login) redirects to `/login`, `ai-unavailable` on transcribe switches to the typed answer, `session-open` offers "Continue your practice session", and `validation.errors` fill the field messages.
- **Secrets:** no endpoint returns a provider name, an API key or a model ID.
- **Gaps to watch:** the transcribe step trusts the client to send the transcript and stats back in `PUT` (Section 6). If self-grading integrity ever matters (for example a shared leaderboard), move the transcript into server state.

## 11. Change log

**v1** (3 October 2026): created from the API grilling session (Q1 to Q10).
