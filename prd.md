# PRD: Abhyas.tech (Interview Prep Platform)

**Owner:** Paras Panchal
**Status:** Draft v20 (API contracts added; see api-contracts.md)
**Last updated:** 3 October 2026

> **How to read this document**
> - Sections 1-12 contain only things you decided, plus a few items marked **(my decision)** that you asked me to decide. Tell me to change any of them.
> - Section 15 lists ideas that are still only proposals.
> - Section 16 lists what is still open. Nothing there is assumed.

---

## 1. Overview

A web app that helps people prepare for job interviews **based on their own resume**. It turns a resume into questions, asks technical and behavioral questions about it, grades the answers, and brings weak questions back until they are strong.

**Guiding rule:** useful first, scalable later. Scalability is out of scope for now.

**Mandatory tech:** React, JavaScript, Spring Boot, MySQL.

---

## 2. Problem and root cause

**What goes wrong with existing tools?**
They mostly ask generic questions, start from zero every session, and judge how you sound rather than whether your answer matches your resume. Their best features (unlimited practice, job-based questions) are often paid.

**Why do candidates actually get rejected?**
- Hiring managers often say candidates cannot explain the experience on their own resumes.
- Differences between the resume and the interview answers hurt credibility.
- Employers want examples of skills, not only a list of skills.

**Root cause:** the weak point is usually **defending your own resume**.

**So how do we solve this?**
Treat every resume line as a **claim to defend**. Ask about each claim, grade the answers, and repeat weak questions until they reach the mastery marks.

---

## 3. Goals and non-goals

### Goals
1. Upload a resume PDF and turn it into structured data.
2. Ask the user for their **target role** and (optionally) **interview date** when they upload the resume.
3. Generate technical and behavioral questions from the resume.
4. Accept answers by clicking (multiple correct), typing (fill-in-the-blank and brief answers), and voice.
5. Grade every answer and show feedback.
6. Store every question, answer, score and date, and repeat weak questions until mastered.
7. Support **multiple users**, each with separate data.
8. Start on free tools and free-tier AI services. If the free limits are not enough, the person running the app pays for the extra AI use.
9. Let the user **export one practice session as JSON** to use in other AI tools.
10. **Keep working when AI is unavailable** (quota used up, provider down): questions come from a local question bank, personalised from the resume and target role, and answers get basic grading without AI (FR-20).

### Non-goals (version 1)
- Running on a public server (the app runs on the owner's laptop, localhost only).
- Scaling to large numbers of users.
- Running AI models on the laptop.
- OAuth or social login.
- Editing the answer key.
- Daily reminder emails.
- Job descriptions (the user does not paste one).

---

## 4. Users

- **Multiple users.** Each user has their own account, resumes, questions, answers and scores.
- **Login:** unique **userId + password**. No OAuth.
- **Success check:** the owner will use the app personally and give suggestions. No formal metrics are defined.

---

## 5. Confirmed decisions

| Topic | Decision |
|---|---|
| Interview types | Both technical and behavioral |
| Answer input | Multiple-correct clicks, fill-in-the-blank and brief typed answers, and voice answers |
| Fill-in-the-blank | 2 marks each, one or two blanks per question. **Java checks first, and Groq re-checks only the wrong ones** |
| Resume input | PDF upload, parsed with the **xitanggg/open-resume** parser |
| If parsing misses parts | React review form **plus** a Groq fallback (PDFBox text to JSON) |
| Where it runs | Owner's laptop only (localhost) |
| Extra features in v1 | Role gap check, weak-spot memory, story bank |
| Answer key | AI drafts it from the resume. **Editing is not enabled** (this replaces the earlier "I edit it" decision). It is shown **read-only in the feedback** after the user answers |
| Interview date | Optional, only stored for now |
| Login | Unique userId + password, no OAuth |
| Users | Multiple users |
| AI providers | **Groq gives the marks. Gemma on Google AI Studio writes the feedback and the report.** The two AIs do not both mark |
| Stack | React, JavaScript, Spring Boot, MySQL |
| Voice stats | Show pace and filler-word stats. **Java counts them, and the AI adds a short comment on the numbers** |
| Roles | A database of **all tech roles with their skills**, filled at the start from a starter list prepared by Claude. Users search it to pick their target role. A new role is added **only if no existing role matches**. For a missing role, **AI drafts the skills and the user can edit them before saving**. A role added this way is **private to the user who added it** |
| Account deletion | Users can delete their own account and all their data |
| AI cost | The person running the app on localhost pays for any AI use beyond the free tiers |
| Export JSON | One practice session the user chooses, downloaded as JSON, with phone number and email removed |
| AI fallback *(my decision, v14)* | A **question bank in MySQL** (no external question API). When AI fails or the quota is out, Java picks bank questions matching the resume skills and role, and fills placeholders with the user's own projects, companies and technologies. Brief and voice answers get **basic keyword grading** from stored key facts. See FR-20 |
| Approved from research | Yes/no checklist grading with quoted proof; remove phone and email before sending text to AI; temperature 0 and "null instead of guessing" in the parse fallback; a swappable `GraderService`; read hidden PDF links in the fallback |

---

## 6. Build order

1. **Login**
2. **Resume parser** (roles database, upload with role and interview date, OpenResume parse, fallback, review form, **gap check**)
3. **Questions** (generation, session, answering)
4. **Feedback** (grading, feedback, weak-spot repetition)
5. **Story bank**
6. **Export JSON** *(my decision: it needs the data from all earlier steps)*

---

## 7. User flow

1. **Sign up / log in** with userId and password.
2. **Upload resume PDF.** While uploading, the app asks for:
   - **Target role** (search the roles database and select one)
   - **Interview date** (optional)
3. **Parse.** The OpenResume parser runs in the browser. If the result looks incomplete, the fallback runs.
4. **Review form.** The user corrects anything wrong and saves.
5. **Claims and answer key.** Each resume bullet and skill becomes a claim. AI drafts the answer key for each claim. The key is not editable.
6. **Practice session** (see Section 9). The user **picks which resume to practice with** at the start of each session (default: the most recently used one). There is no "active resume", and the resume cannot change during a session.
7. **Feedback** after the session.
8. **Next session.** Weak questions come back; mastered questions rest for at least one month.
9. **Export.** The user picks a past practice session and downloads it as JSON.

---

## 8. Functional requirements

### FR-1 Accounts
- Signup and login with a unique userId and password.
- No OAuth.
- All data is separated per user.
- Passwords are stored hashed, never as plain text. *(my decision, a basic security rule)*

### FR-2 Resume upload
- Upload a PDF (PDF only, checked by file header, **1 MB maximum**).
- At upload, ask for **target role** (from the roles database) and **interview date** (optional, only stored for now).

### FR-3 Roles database
- A database of **all tech roles, each with its skills**.
- It is used to ask the user which role they are aiming for. The user searches it and selects a role.
- It starts filled from a **starter list prepared by Claude** (`roles_starter.csv`, 42 roles in 9 categories). *(file format is my decision)*
- Each role also has **aliases** (for example "SDE" for Software Engineer) so search finds it under common names.
- A **new role is added only if no existing role matches** what the user needs. Search must check role names and aliases first (the starter roles plus the user's own added roles).
- **Missing role:** the AI drafts its skills, and the user can edit them before saving. If AI is unavailable the list starts empty and the user types the skills. Near-duplicate names and aliases are checked before saving.
- A newly added role is **private to the user who added it**. The starter roles are visible to everyone.

### FR-4 Parsing
- Parse the PDF with the OpenResume parser (pdf.js, in the browser).
- Send only the structured result to the backend.

### FR-5 Parse fallback
- If the parse looks incomplete, extract text with PDFBox in Spring Boot and ask Groq to convert it into the same structure.
- Remove phone number and email from the text before it is sent to any AI service.
- Run the AI at temperature 0.
- Missing fields come back as `null`, never guessed.
- Read hidden PDF links (GitHub, LinkedIn) with PDFBox and include them.

### FR-6 Review and edit
- Show the parsed resume in an editable React form.
- Saving writes the final version to MySQL. If AI is unavailable the resume is still saved, and the key facts (FR-8) are prepared later, so a failure there never loses the user's edits.

### FR-7 Claims
- Every project bullet, experience bullet and skill becomes one claim.

### FR-8 Answer key
- AI drafts true facts for each claim from the resume.
- The user **cannot edit** the key.
- The key is shown **read-only in the feedback** after the user answers.
- The grader checks answers against it.
- **Deferred drafting** *(my decisions, v17)*: if AI is unavailable when the resume is saved (FR-6), the resume points are created with no key facts yet (status `pending`).
  - Java retries the drafting when the user opens the Resume list or starts a practice session, and whenever AI is available again. One resume point at a time, so the free limits are respected.
  - Questions for a resume point are generated only after its key facts exist. Until then that resume point is covered by practice bank questions for the same skill (FR-20).
  - The user is told nothing (silent fallback). The only visible effect is that those resume points appear in questions a little later.
  - A resume point whose drafting keeps failing stays `pending` and is skipped, not retried in a tight loop.

### FR-9 Question generation
- All technical questions must be related to topics on the user's resume.
- All multiple-correct and fill-in-the-blank questions are technical.
- Behavioral questions follow the STAR style.
- Questions are stored in the database and reused (see FR-13).

### FR-10 Answering
- **Multiple-correct questions:** the user selects one or more of 4 options.
- **Fill-in-the-blank questions:** the user types the missing word or phrase (one or two blanks per question).
- **Brief answers:** typed text.
- **Voice answers:** record audio, transcribe with Groq Whisper, grade the transcript.

### FR-11 Grading
- Described in Section 9.

### FR-12 Feedback
- Described in Section 9.

### FR-13 Repetition and weak-spot memory
- Described in Section 10.

### FR-14 Storage
- Store **every question and every answer with its response** (answer, transcript, score, feedback).
- Store the **date and time each question was last asked**, always updated to the latest.

### FR-15 Voice delivery stats
- For each voice answer, show speaking pace, filler-word count, word count and duration.
- **Java counts them** from the Whisper transcript and its timestamps.
- **Gemma adds a short comment** on the numbers as part of its feedback. *(my reading of "Gemma for feedback")*
- They are shown next to the feedback and do **not** change the marks. *(my decision)*
- The numbers are sent to Gemma in the same call that writes the feedback for that answer. *(my decision)*

### FR-16 Role gap check
- Compare the skills on the user's resume with the skills of the selected role in the roles database.
- Show which role skills have evidence on the resume and which do not.

### FR-17 Story bank *(fields are my decision)*
- Save STAR stories with: title, Situation, Task, Action, Result, lesson learned, tags (for example teamwork, conflict, failure, leadership, deadline pressure), and optional links to resume claims.
- A button on a behavioral answer lets the user save it as a story.
- Stories can be reused while practicing behavioral questions.

### FR-18 Account deletion
- A user can **delete their own account and all their data**: resumes, claims, answer keys, questions, answers, transcripts, scores, stories, gap results, and any roles they added.
- The shared starter roles are not affected.
- The app asks for the password again before deleting. *(my decision, a basic safety step)*

### FR-19 Export JSON
- The app shows the user's past practice sessions (date, role, total marks). *(my decision, needed so the user can pick one)*
- The user picks **one session** and clicks **Export JSON** to download a single JSON file for use in other AI tools.
- **Phone number and email are removed** from the resume part of the export.
- The file contains these sections, in your order:
  1. **Resume:** the structured resume used for that session.
  2. **Gap check:** the result.
  3. **Questions and answers** of that session. Each item has its type, question text, options (multiple-correct), the user's answer (the transcript for voice answers), and the correct options or accepted answers.
  4. **Grades:** marks per question, the session total, the checklist ticks and crosses with quoted proof, mastered or not, and `graded_by` (AI or fallback).
  5. **Feedback:** Gemma's per-answer feedback, the session report, and the voice stats with their comment.
  6. **Story bank:** the user's saved STAR stories.
- *Confirmed:* the story bank part holds all of the user's stories, only completed sessions can be exported, and the correct answers are included.
- *My decisions:*
  - The file is UTF-8 JSON with readable key names.
  - A short `description` at the top explains each section so other AI tools understand it.
  - A small metadata part holds the export date, the session date and the role name.
  - Audio is not included, only transcripts.

### FR-20 AI fallback: question bank and basic grading *(my decisions, change anything you like)*
**When it runs:** automatically when an AI call fails or a free limit is reached. Nothing crashes. *(Confirmed v15:)* the switch is **silent**: no banner, no dialog, and no label. Bank questions and fallback grades look the same as AI ones. Only a hard failure with no fallback (for example voice with no Whisper) shows an inline error message.

**Question bank (MySQL, shared by all users)**
- Table `bank_question`: type (multiple-correct, fill-blank, brief, voice), difficulty, linked **skill** (from the roles database), text with placeholders, options and correct options (multiple-correct), accepted answers (fill-blank), key facts and keywords (brief/voice).
- Behavioral questions are linked to a theme (teamwork, conflict, failure, leadership, deadline pressure), not to a skill.
- **Seed:** a starter bank prepared by Claude in `bank_starter.csv`, **for the software development category only** (about 2 questions per skill, multiple-correct and fill-in-the-blank only). It is not created now; it is built later, when you ask. Other categories start empty and rely on generic placeholder templates and the growth below. The seed is general knowledge and needs review.
- **Updatable by the AI (confirmed):** whenever an AI (Groq or Gemma) generates questions for a logged-in user, the backend also writes them into `bank_question`, so the bank grows while AI is available.
  - *My decisions:* Java validates each question first (4 options, at least one correct, accepted answers present, skill tag set) and skips duplicates (same skill, type and normalised text).
  - Only technical, skill-based questions (multiple-correct, fill-in-the-blank, and brief/voice technical) are saved. Anything built from the user's own resume text (project names, companies, bullets, behavioral questions) is **not** saved as is; at most it is saved as a template with placeholders. The bank is shared by all users, so it must not hold one user's personal details.
  - Each row stores `source` (seed or AI), the provider name and the creation date, so bad rows can be found and removed.

**Personalisation without AI (all done in Java)**
1. Take the skills found on the resume that match the target role's skills. Role-gap skills are used too, as "claims you cannot yet defend".
2. Pick bank questions for those skills, following the normal slot rules (Section 9) and repetition rules (Section 10).
3. Fill placeholders from the parsed resume. Examples: "In **{project}** you used **{skill}**. Why did you choose it over an alternative?", "At **{company}** you wrote: '**{bullet}**'. How did you measure that result?", "Tell me about a time you faced **{theme}** while working on **{project}**."
4. If a skill has no bank question, use a generic template for that question type with the skill name and the matching resume bullet.

**Basic grading without AI**
- Multiple-correct and fill-in-the-blank: unchanged (Java). Blanks marked wrong skip the Groq re-check and stay wrong.
- Brief and voice: Java ticks the checklist items by **keyword match** of the answer against the stored key facts and keywords, and by simple pattern checks (for example "I" versus "we", and Situation/Action/Result cue words for behavioral). The quoted-proof rule still applies, so the matching sentence is shown for each tick.
- Feedback is a **template** (ticks and crosses, key facts, "what to fix" from the missed items). No Gemma text and no session report text, only the numbers.
- Voice answers still need transcription. *(Confirmed v15:)*
  - **Whisper unavailable when the session starts:** the voice part is left out. The session has 16 questions and **50 marks**, the report shows the total out of 50 with a percentage, and History marks the session "voice part skipped" so it is not compared with full sessions.
  - **Microphone denied (or Whisper fails during a voice question):** the user types the answer instead. It is graded like a brief answer (5 marks), with no delivery stats, and the question loses 5 of its 10 marks. Mastery uses the brief mark (4 of 5). The question stays a voice question and is offered again in a later session.

**Marking and tracking**
- Each attempt stores `graded_by` (AI or fallback). It is **not shown in the app** (silent fallback), but it is included in the JSON export (FR-19), so a user can see which answers were keyword-graded.
- *My decision:* fallback grades do not count toward mastery for brief and voice answers (keyword matching is too rough), so those questions come back later for AI grading. Multiple-correct and fill-in-the-blank mastery is unaffected.

**Limits (honest)**
- The questions are only as personal as the placeholders: the user's projects, companies, skills and bullets. They cannot follow up on the exact meaning of a sentence.
- Keyword grading misses correct answers worded differently and can accept answers that only name the right words.

---

## 9. Session structure and grading

### Session structure (total 80 marks)

| Part | Count | Marks each | Total |
|---|---|---|---|
| Multiple-correct questions (4 options each, all technical) | 5 | 2 | 10 |
| Fill-in-the-blank questions (all technical, 1 or 2 blanks) | 5 | 2 | 10 |
| Brief answers (4 technical + 2 behavioral) | 6 | 5 | 30 |
| Voice answers (1 technical + 2 behavioral) | 3 | 10 | 30 |
| **Total** | **19** | | **80** |

If voice transcription is unavailable at the start, the voice part is left out: 16 questions, 50 marks (see FR-20).

### How each part is graded *(my decision, change anything you like)*

**Multiple-correct (2 marks)**
- Graded by Java, no AI call. The correct options are stored when the question is created.
- Marks = 2 x (correct picked minus wrong picked) / total correct options, never below 0, rounded to the nearest 0.5.
- Full marks need an exact match.
- Each question has 4 options, with one or more correct.

**Fill-in-the-blank (2 marks, confirmed)**
- **Step 1, Java:** the AI lists the expected answer and common accepted wordings when the question is created. Java trims the user's answer, ignores upper or lower case, and compares it with that list.
- **Step 2, Groq:** only the blanks Java marked wrong are sent to Groq, which decides whether the answer is correct in meaning (for example a synonym or a spelling variation). If Groq says yes, the blank gets its marks.
- **Marks per blank** *(my decision)*: one blank is worth 2 marks. With two blanks, each is worth 1 mark.
- *My decision:* all wrong blanks of a session are re-checked in one Groq call, to save quota.

**Brief answers (5 marks)**
- AI fills a **5-item yes/no checklist**, 1 mark each.
- Technical: 3 key-fact checks from the answer key, "explains why", "consistent with my resume".
- Behavioral: Situation and Task, Action, Result, own role ("I", not "we"), lesson learned.

**Voice answers (10 marks)**
- AI fills a **10-item yes/no checklist** on the Whisper transcript, 1 mark each.
- Technical: up to 6 key-fact checks from the answer key, plus "explains why", "gives a concrete example from my project", "own role", "consistent with my resume".
- Behavioral: Situation, Task, Action, Result, own role, measurable or concrete detail, lesson learned, consistent with my resume, answers the actual question, clear and focused.

**Evidence rule (approved):** every "yes" needs a quoted sentence from the user's answer. No quote means "no".

### Who does what (confirmed)
- **Groq gives the marks** for brief and voice answers, using the yes/no checklist and quoted proof. It also re-checks fill-in-the-blank answers that Java marked wrong.
- **Java** marks the multiple-correct questions, does the first check of fill-in-the-blank answers, adds up the totals, and counts the voice stats.
- **Gemma writes the feedback and the session report.** It does **not** change any marks.
- *My decision:* Gemma receives the question, the user's answer, Groq's checklist result and the answer key facts, so its feedback matches the marks.
- Gemma's free limit is 16k input tokens per minute, so its calls are sent one at a time, not all at once.

### Feedback format *(my decision)*
- **Multiple-correct and fill-in-the-blank:** marks and the correct options or accepted answers. *(my decision)*
- **Brief and voice answers:** marks (from Groq), the checklist with a tick or cross and the quoted proof for each tick, the answer key facts (read-only), and Gemma's short written feedback with a "what to fix" line.
- **Per session:** total out of 80, marks per part, how many questions are mastered, and the weakest claims, with a short report written by Gemma.

---

## 10. Repetition rules

**Mastery marks (confirmed):**

| Part | Mastered when |
|---|---|
| Multiple-correct | 2 of 2 |
| Fill-in-the-blank | 2 of 2, which means all blanks are correct (confirmed) |
| Brief answer | 4 of 5 or more |
| Voice answer | 8 of 10 or more |

*My reading:* "4/5" and "8/10" mean that mark **or higher**. The voice mark was "above 8" earlier and is now 8/10, so I use your latest answer.

**Rules (confirmed):**
1. Questions are tracked per **user + role + resume**. A new resume starts fresh tracking (confirmed).
2. Each session fills its slots in this order: **unmastered questions first** (if a part has more unmastered questions than slots, the **lowest score goes first**), then the remaining slots are filled **at random** from new questions and from mastered questions that have rested for one month.
3. A question **below** the mastery marks keeps coming back in later sessions until mastered. The **same question text** is repeated, not a reworded version.
4. A **mastered** question may come back only if it has not been asked for **one month**.
5. The latest asked date is stored every time a question is asked.
6. Every question, answer and score is kept in the database.

---

## 11. Technical overview

| Layer | Choice |
|---|---|
| Frontend | React, JavaScript |
| Backend | Spring Boot |
| Database | MySQL |
| Resume parsing (main) | xitanggg/open-resume parser (pdf.js, in the browser) |
| Resume parsing (fallback) | Apache PDFBox in Spring Boot, then Groq |
| Main AI | Groq |
| Voice to text | Groq Whisper |
| Second-opinion AI | Gemma on Google AI Studio |
| Grader design | A swappable `GraderService` interface so AI providers can be changed by configuration |
| Roles starter data | `roles_starter.csv`, prepared by Claude from general knowledge (not a live job-market dataset). The exact content is in `database-schema.md` Section 12 |
| License | AGPL-3.0 (because the OpenResume parser is copied in) |
| Database setup | MySQL 8 installed natively on Windows, schema created by Flyway migrations |
| How it is built | See `trd.md` (architecture, API, pipelines, build steps with manual checks) |

### Free-tier limits found in research
These change often. Check each provider's limits page before relying on them.

| Service | Free limit found |
|---|---|
| Groq Llama 3.3 70B | 1,000 requests/day, 100K tokens/day |
| Groq Llama 3.1 8B | 14,400 requests/day, 500K tokens/day |
| Groq Whisper | 20 requests/min, 2,000 requests/day, 8 hours of audio/day, 25 MB per file |
| Google AI Studio, Gemma 4 | 30 requests/min, 14.4k requests/day, 16k input tokens/min |

**Important with multiple users:** Groq limits apply per organization, not per key or per user. All users share one free budget. If the free limits are not enough, the person running the app on localhost pays for the extra AI use.

**Rough estimate (to be measured, not a promise):** one session needs about 9 Groq gradings (plus one re-check call when fill-in-the-blank answers were marked wrong) and 9 to 10 Gemma feedback calls (one per brief or voice answer, plus a session report), plus question and answer-key generation. That is roughly a few full sessions per day on the free 70B budget, shared by all users.

### Draft entities
Tables and columns are defined in `database-schema.md`: `app_user`, `skill`, `role` (starter roles are shared, added roles belong to one user), `role_alias`, `role_skill`, `resume`, `resume_point` (the claim), `key_fact` (the answer key), `gap_result`, `bank_question` (fallback bank, FR-20), `question` (per resume, with last-asked date and mastered flag), `practice_session`, `attempt` (answer, transcript, score, feedback, date), `story`, `story_tag`, `story_resume_point`.

Data decisions *(from the database-schema session, v18)*: a saved resume is read-only and its target role is fixed (a changed resume is a new upload with fresh tracking), only `interview_date` can be edited; the original PDF is not stored; a user has at most one unfinished practice session; account deletion is a cascading hard delete.

---

## 12. Non-functional requirements

- **Cost:** everything starts on free tiers. If they are not enough, the person running the app pays for the extra AI use.
- **Environment:** laptop only, no local AI models.
- **Privacy:** resumes hold personal data. Phone and email are removed before text goes to AI services, and also from the JSON export.
- **Data isolation:** one user must never see another user's data, including roles they added.
- **Deletion:** deleting an account removes all of that user's data.
- **Resilience:** if a free daily limit runs out, fall back silently (FR-20). If nothing can serve the request, show a clear inline message instead of crashing.
- **Explainability:** every grade comes with feedback and quoted proof.

---

## 13. Risks

| Risk | Why it matters |
|---|---|
| **Answer key is AI-drafted and cannot be edited** | A wrong fact in the key will cause unfair grading. The user can see the key in the feedback and spot mistakes, but cannot fix them. |
| **Multiple-correct options and fill-in-the-blank answers are AI-generated** | A wrong "correct option" or accepted answer gives wrong marks, and Java trusts it. |
| Fill-in-the-blank wording | Java may mark a correct answer with different wording as wrong. Groq re-checks it, but Groq could also accept a wrong answer. |
| Shared AI quota across users | Several users can use up the daily free limits quickly. Any use beyond the free tiers is paid by the person running the app. |
| OpenResume works best on single-column English resumes | Two-column resumes may be read in the wrong order. The fallback and review form exist for this. |
| OpenResume is TypeScript, the stack says JavaScript | Vite builds the copied TypeScript parser files as they are, so the app code stays JavaScript (see `trd.md`). |
| OpenResume license (AGPL-3.0) | **Decided:** the parser is copied and the whole project is licensed AGPL-3.0, with OpenResume's notice kept and a README note. Check the terms again before sharing or hosting the app for other people. This is not legal advice. |
| AI graders can be too generous | The checklist and quoted-proof rules reduce this. With one grader (Groq) there is no cross-check, so the hand-grading proposal in Section 15 matters more. |
| Free tiers change | A provider may cut limits or disappear. |
| Gemma free tier has a small per-minute input limit | Writing feedback for nine answers can hit the limit if calls are sent together. Send them one at a time. |
| Google AI Studio free-tier data handling | Check its terms before sending personal data. |
| Whisper may remove "um" and "uh" from transcripts | Filler-word counts could be lower than reality. |
| Starter roles list comes from Claude's general knowledge | Some skills may be outdated or missing and need review. |
| Users adding roles | Near-duplicate roles can appear in a user's own list if the alias search misses a match. |
| Fallback bank is generic and keyword grading is rough | Questions feel less personal and brief/voice grades may be unfair. The user is not told when it happens (silent fallback), so a strict grade cannot be explained in the app. Mitigated by not counting those grades toward mastery and by `graded_by` in the export. |
| Bank questions from AI may contain errors | Saved AI questions are trusted by Java later, the same risk as FR-9. |
| Export still contains personal details | Phone and email are removed, but the name, links and answers stay in the file. The user decides which AI tool receives it. |

---

## 14. Success check

The owner will use the app personally and give suggestions. No formal metrics are defined.

---

## 15. Still proposed (not approved)

1. **Hand-grade about 20 answers** and compare with the AI's grades before trusting it.

---

## 16. Open questions

None. All questions are answered. Items marked "my decision" or "my reading" in this document are places where I chose something for you, and you can still change them.

---

## 17. Change log

**v1 to v2**
- Added multiple users and userId + password login.
- Added target role (from a roles database) and interview date at resume upload.
- Defined session structure (80 marks), grading, mastery marks and repetition rules.
- Answer key editing removed (was "AI drafts, I edit").
- Approved research items 1, 4, 5, 6 and 8 became requirements. Items 2, 3 and 7 remain proposals.
- Added build order: Login, Resume parser, Questions, Feedback.
- Added story bank fields and feedback format (my decisions).

**v2 to v3**
- Roles database defined: all tech roles with skills, a starter list prepared by Claude, new roles added only when no existing role matches.
- Voice stats decided: Java counts, the AI adds a short comment.
- Role gap check now uses the role's skills from the database (my reading, to confirm).
- Added aliases to roles and the starter file `roles_starter.csv`.

**v3 to v4**
- Missing roles: the AI drafts the skills and the user can edit them before saving.
- Role gap check confirmed: compares the resume with the role's skills, no pasted job description.
- Gemma now gives a second opinion on every brief and voice answer ("borderline only" proposal removed).
- Added a rule to send Gemma's calls one at a time because of its per-minute limit.

**v4 to v5**
- Grading roles changed: Groq gives the marks, Gemma writes the feedback and the session report. The "Gemma second opinion on marks" idea is removed.
- Interview date is optional and only stored for now.
- The answer key is shown read-only in the feedback after answering (still not editable).
- Gemma's calls are one per brief or voice answer plus one session report, sent one at a time.

**v5 to v6**
- Mastery marks confirmed: multiple-correct 2/2, brief 4/5, voice 8/10 (voice was "above 8" before).
- Repetition confirmed: unmastered questions first, the rest random, same question text repeated.
- Build order confirmed: Login, Resume parser (with the gap check), Questions, Feedback, then Story bank.

**v6 to v7**
- A newly added role is private to the user who added it.
- Added account deletion (own account and all data), with a password check.

**v7 to v8**
- Objective questions changed to 5 multiple-correct (4 options each) and 5 fill-in-the-blank, all technical. Total stays 80 marks.
- Repetition: when there are too many unmastered questions, the lowest score goes first.
- A new resume starts fresh tracking (confirmed).
- AI cost: the person running the app on localhost pays for use beyond the free tiers.

**v8 to v9**
- Fill-in-the-blank grading confirmed: Java checks first, Groq re-checks only the wrong ones.
- Fill-in-the-blank is 2 marks each, with one or two blanks per question (two blanks are 1 mark each). The session stays 80 marks.

**v9 to v10**
- Added a job description feature (the user pastes one). This reverses the earlier "no pasted job description" decision.
- Added Export JSON: one practice session, phone and email removed, sections in your order (resume, job description, gap check, questions and answers, grades, feedback, story bank).
- Added Export JSON as build step 6.

**v10 to v11**
- Job description is optional and pasted at resume upload, together with role and interview date.
- Gap check compares the resume with both the role's skills and the pasted job description.
- Question generation also uses the job description.

**v11 to v12**
- Fill-in-the-blank mastery confirmed (2 of 2, all blanks correct).
- Export details confirmed (all stories, completed sessions only, correct answers included).
- Job description wording is waiting for one clarification (Section 16).

**v12 to v13**
- Job description removed completely: no pasting at upload, no use in the gap check, questions, tracking or export.
- Gap check compares the resume with the role's skills only.
- Export JSON now has these sections: resume, gap check, questions and answers, grades, feedback, story bank.
- Export JSON is now FR-19.

**v13 to v14**
- Added AI fallback (FR-20): local question bank personalised from resume and role with placeholders, basic keyword grading, template feedback. No external question API.
- Fallback brief/voice grades do not count toward mastery (my decision).
- Starter bank limited to software development only, built later on request.
- Bank is updatable: AI-generated questions for a logged-in user are validated and saved into it, without personal resume details.

**v14 to v15** (from the UI/UX discussion)
- Fallback is silent: no notice or label in the app. `graded_by` is stored and exported (FR-20, FR-19).
- Voice fallback: Whisper down at start leaves out the voice part (16 questions, 50 marks, "voice part skipped" in History). Mic denied means a typed answer graded like a brief answer (5 marks), mastery 4 of 5.
- The user picks the resume at the start of each practice session. No "active resume".
- Details of screens, states and terms live in `UI-UX.md`.

**v15 to v16** (from the wireframes session)
- Wireframes for all screens are in `wireframes.md`; `UI-UX.md` was updated with the layout decisions.
- FR-3: if AI is unavailable when adding a missing role, the user types the skills.
- FR-6: the resume is saved even if AI cannot prepare the key facts yet; they are prepared later.
- Report feedback may fill in gradually, because Gemma calls are sent one at a time.

**v16 to v17**
- FR-8: key facts can be drafted later when AI was unavailable at save time (status `pending`, retried when the user opens the Resume list or starts a session, practice bank covers the gap, no notice to the user).

**v17 to v18** (from the database-schema session)
- Added `database-schema.md` (tables, columns, keys, indexes, delete behavior, V1 SQL).
- A saved resume is read-only and its target role is fixed; only the interview date can be edited. The PDF file is not stored.
- At most one unfinished practice session per user (my decision).
- Replaced the draft entity list in Section 11 with the table list.

**v18 to v19** (from the TRD session)
- Added `trd.md`.
- PDF upload limited to 1 MB (FR-2).
- OpenResume license risk resolved: AGPL-3.0, so the whole project is AGPL-3.0.
- MySQL is installed natively on Windows.
- Each build step now ends with a manual check list that you run before the next step.

**v19 to v20** (from the API contracts session)
- Added `api-contracts.md` (every endpoint, error codes, validation limits, export JSON shape).
- Signup logs the user in. Passwords are 8 to 72 characters. A session in progress never sends correct answers or key facts to the browser.
- A resume cannot be deleted while an unfinished session uses it.

**Ticket 01 (3 October 2026)**
- `V1__schema.sql` run on MySQL 8.0.42 for the first time. `role.owner_key` and `practice_session.open_user_id` changed from `STORED` to `VIRTUAL` generated columns, because MySQL refuses a `STORED` column over a base column that has a foreign key (error 1215). The unique indexes work the same. Updated in `database-schema.md` and the migration.

**Ticket 02 (3 October 2026)**
- Sign up, log in, log out and `/me` built. The JWT library is `nimbus-jose-jwt` instead of `jjwt` (no Jackson 2 dependency next to Spring Boot 4's Jackson 3). `JWT_SECRET` is required at start (at least 32 bytes). Logout is also open without a cookie, so it can be repeated after the login expired. The cookie check is a Spring MVC interceptor, not Spring Security.

**v14 to v15**
- Project renamed from "Resume Defense Trainer" to "Abhyas.tech". Tagline: "Defend your resume". Maven artifact and Java package stay `tech.abhyas` / `trainer`.
