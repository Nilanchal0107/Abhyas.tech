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
2. Ask the user for their **target role** when they upload the resume.
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
| Extra features in v1 | Role gap check, weak-spot memory |
| Answer key | AI drafts it from the resume. **Editing is not enabled** (this replaces the earlier "I edit it" decision). It is shown **read-only in the feedback** after the user answers |
| Login | Unique userId + password, no OAuth |
| Users | Multiple users |
| AI providers | **Groq gives the marks and writes the feedback and the report** (Gemma was removed on 10 October 2026) |
| Stack | React, JavaScript, Spring Boot, MySQL |
| Voice stats | Show pace and filler-word stats. **Java counts them, and the AI adds a short comment on the numbers** |
| Roles | A database of **all tech roles with their skills**, filled at the start from a starter list prepared by Claude. Users search it to pick their target role. **The list is fixed at 4 software development roles (since 10 October 2026; it was 11, and 42 before that); users cannot add roles** (since 4 October 2026; earlier a missing role could be added with AI-drafted skills, private to its user) |
| Account deletion | Users can delete their own account and all their data |
| AI cost | The person running the app on localhost pays for any AI use beyond the free tiers |
| Export JSON | One practice session the user chooses, downloaded as JSON, with phone number and email removed |
| AI fallback *(my decision, v14)* | A **question bank in MySQL** (no external question API). When AI fails or the quota is out, Java picks bank questions matching the resume skills and role, and fills placeholders with the user's own projects, companies and technologies. Brief and voice answers get **basic keyword grading** from stored key facts. See FR-20 |
| Approved from research | Yes/no checklist grading with quoted proof; remove phone and email before sending text to AI; temperature 0 and "null instead of guessing" in the parse fallback; a swappable `GraderService`; read hidden PDF links in the fallback |

---

## 6. Build order

1. **Login**
2. **Resume parser** (roles database, upload with role, OpenResume parse, fallback, review form, **gap check**)
3. **Questions** (generation, session, answering)
4. **Feedback** (grading, feedback, weak-spot repetition)
5. **Export JSON** *(my decision: it needs the data from all earlier steps)*

---

## 7. User flow

1. **Sign up / log in** with userId and password.
2. **Upload resume PDF.** While uploading, the app asks for:
   - **Target role** (search the roles database and select one)
3. **Parse.** The OpenResume parser runs in the browser. If the result looks incomplete or low quality, the fallback runs.
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
- At upload, ask for **target role** (from the roles database). The interview date was removed on 10 October 2026.

### FR-3 Roles database
- A database of **all tech roles, each with its skills**.
- It is used to ask the user which role they are aiming for. The user searches it and selects a role.
- It starts filled from a **starter list prepared by Claude** (`roles_starter.csv`, 4 roles in 1 category since 10 October 2026; it was 11 in 3, and 42 in 9). *(file format is my decision)*
- Each role also has **aliases** (for example "SDE" for Software Engineer) so search finds it under common names.
- The list is **fixed**: users cannot add roles (removed 4 October 2026, with the AI skill draft). Roles a user added before then stay private to that user and still show in their search.

### FR-4 Parsing
- Parse the PDF with the OpenResume parser (pdf.js, in the browser).
- Send only the structured result to the backend.

### FR-5 Parse fallback
- If the parse looks incomplete or low quality (an education entry without a school, or a project without a name), extract text with PDFBox in Spring Boot and ask Groq to convert it into the same structure.
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
- For bank questions the key comes with the reviewed bank row. For the user's own projects, **one AI call per resume** writes about 8 open questions about its projects and experience, each with 3 to 6 true key facts stored on the question (ticket 08). It replaces the per-point key-facts drafting.
- The user **cannot edit** the key.
- The key is shown **read-only in the feedback** after the user answers.
- The grader checks answers against it.
- **Deferred drafting:** the call runs in the background after the save (FR-6). If AI is unavailable the resume is still saved with no project questions. Java retries when the user opens the Resume list or starts a session, at most once per resume every 10 minutes.
  - Until they exist, the slots are filled from the practice bank and the generic templates (FR-20). The user is told nothing.
  - Only projects, experience and skills are sent to the AI, never the profile.

### FR-9 Question generation
- All technical questions must be related to topics on the user's resume.
- All multiple-correct and fill-in-the-blank questions are technical.
- Behavioral questions follow the STAR style.
- Technical questions test the technology, concept or choice behind a resume point (how it works, why it was used), not recall of what the resume says. Questions never assume anything the resume does not state (teams, deadlines, tools, results).
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
- For a **bank question**, mastery and the one-month rest follow the **user** across all their resumes (`bank_performance`). A **project question** stays per resume.

### FR-14 Storage
- Store **every question and every answer with its response** (answer, transcript, score, feedback).
- Store the **date and time each question was last asked**, always updated to the latest. For a bank question this is kept per user, not per resume (FR-13).

### FR-15 Voice delivery stats
- **Live words (since 10 October 2026):** while a voice answer is being recorded, the browser's speech recognition shows the words as they are spoken, with a live pace and filler count. It is a preview only: when the recording stops, Whisper's transcript replaces it and is what is graded and counted. Browsers without speech recognition show nothing live.
- For each voice answer, show speaking pace, filler-word count, word count and duration.
- **Java counts them** from the Whisper transcript and its timestamps. A Web Speech answer has no timestamps: Java counts the words and uses the recording length the browser measured.
- **Groq adds a short comment** on the numbers as part of its feedback.
- They are shown next to the feedback and do **not** change the marks. *(my decision)*
- The numbers are sent to Groq in the same call that marks the answer and writes its feedback. *(my decision)*

### FR-16 Role gap check
- Compare the skills on the user's resume with the skills of the selected role in the roles database.
- Show which role skills have evidence on the resume and which do not.

### FR-17 Story bank *(removed 10 October 2026)*
- The story bank was removed: nothing in the practice flow read it. FR-17 stays as a number so the others keep theirs.

### FR-18 Account deletion
- A user can **delete their own account and all their data**: resumes, claims, answer keys, questions, answers, transcripts, scores, gap results, and any roles they added.
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
  5. **Feedback:** Groq's per-answer feedback, the session report, and the voice stats with their comment.
  (The sixth section, the story bank, was removed 10 October 2026.)
- *Confirmed:* only completed sessions can be exported, and the correct answers are included.
- *My decisions:*
  - The file is UTF-8 JSON with readable key names.
  - A short `description` at the top explains each section so other AI tools understand it.
  - A small metadata part holds the export date, the session date and the role name.
  - Audio is not included, only transcripts.

### FR-20 AI fallback: question bank and basic grading *(my decisions, change anything you like)*
**When it runs:** automatically when an AI call fails or a free limit is reached. Nothing crashes. There is no banner or dialog, and bank questions look the same as AI ones. **A keyword-graded answer is labelled** in the report ("Graded without AI") with a **Regrade with AI** button *(changed 4 October 2026; it was fully silent since v15)*. Only a hard failure with no fallback (for example voice with no Whisper) shows an inline error message.

**Question bank (MySQL, shared by all users)**
- Table `bank_question`: type (multiple-correct, fill-blank, brief, voice), difficulty, linked **skill** (from the roles database), text with placeholders, options and correct options (multiple-correct), accepted answers (fill-blank), key facts and keywords (brief/voice).
- Behavioral questions are linked to a theme (teamwork, conflict, failure, leadership, deadline pressure), not to a skill.
- **Seed:** a starter bank prepared by Claude in `bank_starter.csv`, **for the software development category only** (about 2 questions per skill, multiple-correct and fill-in-the-blank only). It is not created now; it is built later, when you ask. Other categories start empty and rely on generic placeholder templates and the growth below. The seed is general knowledge and needs review.
- **Bank is reviewed, not AI-fed (changed, ticket 10):** the bank holds only reviewed rows (`source = 'seed'`). AI-written questions are no longer saved into it.
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
- Feedback is a **template** (ticks and crosses, key facts, "what to fix" from the missed items). No AI text and no session report text, only the numbers.
- Voice answers still need transcription. *(Confirmed v15:)*
  - **Whisper unavailable:** the voice part stays (a session always has 19 questions and 80 marks, ticket 19). The user speaks the answer again through the browser's **Web Speech API** (`SpeechRecognition`, Chrome and Edge); its transcript is sent like a Whisper transcript and graded as a voice answer (10 marks, mastery 8). Chrome sends that audio to the browser vendor's servers; the app stores no audio. The browser gives no word timing, so the stats use the recording length measured in the browser and Java counts the words. No Web Speech or no microphone: the typed answer below.
  - **Microphone denied (or Whisper fails during a voice question):** the user types the answer instead. It is graded like a brief answer (5 marks), with no delivery stats, and the question loses 5 of its 10 marks. Mastery uses the brief mark (4 of 5). The question stays a voice question and is offered again in a later session.

**Marking and tracking**
- Each attempt stores `graded_by` (AI or fallback). The report labels keyword-graded answers and offers a regrade once AI is back, which marks the answer with Groq, rewrites its feedback and the report, and lets the mark count toward mastery. `graded_by` is also in the JSON export (FR-19).
- *My decision:* fallback grades do not count toward mastery for brief and voice answers (keyword matching is too rough), so those questions come back later for AI grading. Multiple-correct and fill-in-the-blank mastery is unaffected.

**Limits (honest)**
- The questions are only as personal as the placeholders: the user's projects, companies, skills and bullets. They cannot follow up on the exact meaning of a sentence.
- Keyword grading misses correct answers worded differently and can accept answers that only name the right words.

---

### FR-21 Interview mode *(owner decisions, 5 October 2026, ticket 13)*
- Home lets the user choose the mode before starting: **Quiz** (the 19-question session above) or **Interview**. The one-unfinished-session rule covers both.
- An interview has **6 open questions**: 4 technical (3 about the user's projects, then **1 design question**, see below) and 2 behavioral from the bank. Each is **spoken or typed**, worth 10 marks (5 when typed, as for a voice answer in the quiz), so 60 marks at most. The user moves forward only.
- **Timer:** 3 minutes per answer, shown on screen. When it runs out the answer is sent as it stands (a recording in progress finishes first). The limit is enforced in the browser, as is the 3-minute recording limit.
- **One follow-up per answered question.** Groq writes it from the question, the key facts and what the user said, and must not assume details the resume or the answer does not state. The follow-up is written once and kept, so a reload shows the same one. If Groq is unavailable a generic follow-up is used. A skipped question has no follow-up and scores zero.
- The follow-up answer is graded **together with** the main answer (one checklist, one mark). Marks, feedback per answer and the coaching report reuse the quiz grading and the report screen, which also shows the follow-up and the reply.
- **Mastery:** the questions count toward mastery, exactly like quiz questions (FR-13, same rules, including no mastery from keyword grading).
- History shows the mode of each session. The export gets `metadata.mode` and each answer's `followUp`.

### FR-21a Design question in interview mode *(owner decision, 10 October 2026)*
- One of the 4 technical interview questions is a **design question** (system design or low-level design for Backend, Software Engineer and Full Stack; front end design for Frontend and Full Stack), spoken or typed, 10 marks (5 typed).
- It is graded on a **5-item checklist**: requirements and assumptions, API or main operations, data model, scale, trade-offs, each worth 2 marks (1 typed), with quoted proof. The key facts of the question are reference points for the grader and are shown read-only in the report.
- Without AI, keyword cues tick the same 5 items, and that mark does not count toward mastery (FR-20). Mastery is 8 of 10 like any voice answer.
- The interviewer also **reads each question and follow-up aloud** (browser speech synthesis, mute and replay, remembered mute). Recording waits until the reading ends, and the answer timer is paused while it speaks.

### FR-22 Role flavours *(owner decision, 10 October 2026)*
- A question can carry a **track** (`backend` or `frontend`). Backend gets write-a-SQL-query and design-an-endpoint questions, Frontend gets JavaScript quirks, CSS layout and React hook pitfalls, Full Stack gets both, Software Engineer gets the backend flavour. Questions with no track suit every role.
- SQL and endpoint questions are **brief answers typed in a code box** (`format = code`), graded by a Groq checklist of 5 key facts about what the answer does. **Nothing is executed**, so a correct query written differently can still earn the ticks, and the quoted proof rule applies.
- Code in questions, options and answers shows in monospace (`` `inline` `` and fenced blocks).

## 9. Session structure and grading

### Session structure (total 80 marks)

| Part | Count | Marks each | Total |
|---|---|---|---|
| Multiple-correct questions (4 options each, all technical) | 5 | 2 | 10 |
| Fill-in-the-blank questions (all technical, 1 or 2 blanks) | 5 | 2 | 10 |
| Brief answers (3 technical + 2 behavioral from the bank, 1 about the user's projects) | 6 | 5 | 30 |
| Voice answers (1 technical + 2 behavioral) | 3 | 10 | 30 |
| **Total** | **19** | | **80** |

The voice part always stays: with Whisper down the browser's Web Speech API gives the transcript (see FR-20).

### How each part is graded *(my decision, change anything you like)*

**Multiple-correct (2 marks)**
- Graded by Java, no AI call. The correct options are stored when the question is created.
- Marks = 2 x (correct picked minus wrong picked) / total correct options, never below 0, rounded to the nearest 0.5.
- Full marks need an exact match.
- Each question has 4 options, with one to three correct (never all four).

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
- **Groq writes the feedback and the session report**, in the same call as the checklist for each answer and one call for the report. The feedback does **not** change any marks (the ticks and the evidence rule do).
- *My decision:* the feedback is written in the same reply as the ticks, so it matches the marks.
- Groq's calls are sent one at a time by a single grading worker.

### Feedback format *(my decision)*
- **Multiple-correct and fill-in-the-blank:** marks and the correct options or accepted answers. *(my decision)*
- **Brief and voice answers:** marks (from Groq), the checklist with a tick or cross and the quoted proof for each tick, the answer key facts (read-only), and Groq's short written feedback with a "what to fix" line.
- **Per session:** total out of 80, marks per part, how many questions are mastered, and the weakest claims, with a short report written by Groq.

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

**Important with multiple users:** Groq limits apply per organization, not per key or per user. All users share one free budget. If the free limits are not enough, the person running the app on localhost pays for the extra AI use.

**Rough estimate (to be measured, not a promise):** one session needs about 9 Groq gradings (plus one re-check call when fill-in-the-blank answers were marked wrong) and one report call (the feedback comes in the grading replies), plus question and answer-key generation. That is roughly a few full sessions per day on the free 70B budget, shared by all users.

### Draft entities
Tables and columns are defined in `database-schema.md`: `app_user`, `skill`, `role` (starter roles are shared, added roles belong to one user), `role_alias`, `role_skill`, `resume`, `resume_point` (the claim), `key_fact` (the answer key), `gap_result`, `bank_question` (fallback bank, FR-20), `question` (per resume, with last-asked date and mastered flag), `practice_session`, `attempt` (answer, transcript, score, feedback, date).

Data decisions *(from the database-schema session, v18)*: a saved resume is read-only and its target role is fixed (a changed resume is a new upload with fresh tracking), nothing on it can be edited; the original PDF is not stored; a user has at most one unfinished practice session; account deletion is a cascading hard delete.

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
| Whisper may remove "um" and "uh" from transcripts | Filler-word counts could be lower than reality. |
| Starter roles list comes from Claude's general knowledge | Some skills may be outdated or missing and need review. |
| Users adding roles | Near-duplicate roles can appear in a user's own list if the alias search misses a match. |
| Fallback bank is generic and keyword grading is rough | Questions feel less personal and brief/voice grades may be unfair. Mitigated by labelling keyword-graded answers with a Regrade button, by not counting those grades toward mastery, and by `graded_by` in the export. |
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
- Bank is a reviewed set; the AI no longer adds to it (ticket 10).

**v14 to v15** (from the UI/UX discussion)
- Fallback is silent: no notice or label in the app. `graded_by` is stored and exported (FR-20, FR-19).
- Voice fallback *(replaced in ticket 19: Web Speech instead of dropping the voice part)*: Mic denied means a typed answer graded like a brief answer (5 marks), mastery 4 of 5.
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

**Ticket 07 (3 October 2026)**
- The browser parse is tidied before the "looks incomplete" test: skill lines are split into single skills, link URLs are read from the PDF's link annotations, and the common OpenResume mix-ups in education and projects are corrected (`frontend/src/lib/resume.js`). A **low-quality** result (an education entry without a school, or an unnamed project) now also triggers the fallback, not only an incomplete one (`trd.md` 7.1). A PDF with no email or phone and no experience, projects, education or skills is refused as "not a resume" in the browser. Location is still not recovered by the browser parse; the fallback and the Review form cover it.

**Ticket 08 (3 October 2026)**
- PDF fallback parse built (`POST /resumes/parse-fallback`, PDFBox + Groq). The preview always shows a "Scan with AI" button, so the user can run the fallback even when the browser parse looked complete. Multipart limit raised to 6 MB so the server answers `file-too-large` itself.

**Tickets 17 to 23 (3 October 2026)**
- Practice bank (FR-20): when AI cannot write a part, `PracticeBank` picks bank rows for the resume's and role's skills and fills `{project}`, `{skill}`, `{company}`, `{bullet}`, `{theme}` from the resume, then falls back to generic templates built from the resume itself (blank out the one skill a bullet names; "which technologies are in this bullet" with distractors; project, bullet and theme questions), so the answer key is always known. AI questions are saved into `bank_question` only when technical, tied to a skill named in the text, and free of the user's project, company, school or name and of any 5-word run from a bullet. The unique text hash plus `INSERT IGNORE` decides races.
- Grading (`POST /sessions/{id}/submit`, 202 first time, 200 on repeat): Java marks multiple-correct and blanks, one Groq call re-checks wrong blanks, Groq ticks brief and voice checklists and Java enforces the evidence rule and sums the marks. Checklists always have 5 or 10 items: a point with fewer key facts than needed gets generic "makes a correct point" checks. A skipped question scores 0 and counts toward mastery as 0.
- Fallback (`KeywordGrader`, `TemplateFeedback`): keyword and cue-word ticks with the matching sentence as quote. Such a mark never sets `mastered` or `last_score`. `graded_by` stays out of every in-app response.
- Gemma feedback and report run one call at a time with a 7-second gap (`app.grading.gemma-gap-millis`). A rate limit with Retry-After is waited out once (at most 60 s), then the answer gets template text with `feedback_status = 'none'` (the text is still shown). Template reports also use `report_status = 'none'`. On startup, completed sessions with open work are resumed.
- Repetition (ticket 23): the rested-mastered test is now "asked at least one month ago" (was "more than"). The rest of the rules were already in session building and are covered by new tests.
- The report row's `mastered` is computed from the attempt's score for display, so a fallback-graded 4 of 5 shows as mastered although the stored flag stays unchanged.

**Tickets 24 to 29 (4 October 2026)**
- Home (W2) now shows the unfinished session with Continue and Leave session, the resume selector with Start, and the latest completed session (total, mastered, up to three weakest points). History (W10) is a table with status as icon plus word, Export JSON on completed rows and Continue on unfinished ones. All five menu items are live.
- Story bank (`/stories`, W11): list with tag chips, form on its own page, tags, links to resume points (checked to belong to the user), `sourceAttemptId` (checked, cleared when the answer is deleted). "Save as story" in the report pre-fills the Action box in the browser. **Decision:** the `Attempt` type now carries its database `id` (api-contracts 2.5), because the browser has to send `sourceAttemptId`.
- Export (`GET /sessions/{id}/export`): built on request from the stored rows, six sections in order, `phone` and `email` keys removed from `resume.profile`, `gradedBy` only in `grades`, gap check with skill names and resume point texts instead of IDs. Only a completed and fully graded session of the user can be exported (`session-not-completed` otherwise).
- Starter practice bank (ticket 29, FR-20): a draft of 106 questions (one multiple-correct and one fill-in-the-blank for each of the 53 skills of the 7 software development roles) is in `backend/src/main/resources/db/seed-draft/` with its generator. It is **not loaded**: after the review, move `V3__starter_bank.sql` into `db/migration/`.
- Final checks (ticket 28): a test now checks that all 25 protected endpoints answer 401 without a cookie, and the account-deletion test covers every user table. Not run here: the MySQL tests (no Docker on this machine), the keyboard pass, the 20 hand-graded answers and the Step 6 manual checks.

**Landing page and routing (3 October 2026)**
- Added a public landing page at `/` (wireframe W13): shown before login and to signed-in users who click "Abhyas.tech". It explains the project and links to a YouTube tutorial and the GitHub repository.
- Home (W2) moved from `/` to `/practice`. After login the user lands on Home.
- UI pass: shared button variants (primary, secondary, danger), error boxes, card sections, a stronger input and button border (`--color-border-strong`) and a heading scale. Home, Resumes, Practice, Report and Account were updated to use them.
- More routes: `/resumes/:id`, `/resumes/:id/role-skills`, `/stories/new`, `/stories/:id/edit` and `/account/delete`. `/resumes/new/review` stays inside Upload.

**Landing page redesign (3 October 2026)**
- The landing page (W13) was rebuilt with the `landing-page-design` skill: hero with a marked example answer, problem, five benefits, a tagline section whose words light up on scroll, three steps, privacy note, eight FAQ items and a final call to action. **Decision (owner):** the landing page is the one exception to "light theme only, no gradients, system font": it uses a dark theme from the skill's palette, the Geist font, a gradient on the hero heading text only, and Phosphor icons. All other screens keep the tokens in `UI-UX.md` Section 4.
- Not built: testimonials and logos (the app has none), FAQ schema and `noindex` (localhost only), privacy policy and terms pages, a custom 404, a branded favicon and `og:image`. Phone width is covered by a hamburger menu, though phones stay unsupported in v1.

**Apple-style redesign (3 October 2026)**
- **Decision (owner):** the whole frontend was restyled with the `apple-design` skill, same screens, routes and flows, no backend change. Light theme only; flat rule relaxed to translucent materials (blurred top bar, dialogs, role list), soft shadows, 12 px radius, pill buttons, Geist font app-wide, tighter heading tracking. Motion is CSS only (no new dependency): instant press feedback, short screen rise, dialog arrives scaled and blurred. The landing page is now light too, with a solid heading (the dark theme and hero gradient from the earlier entry are gone). Reduced motion, reduced transparency and more contrast are supported. Phones stay unsupported in v1.

**Question quality and the skills-check start button (4 October 2026)**
- A test with a real resume showed AI questions that only checked recall of the resume ("Which facts are mentioned in the resume?"), multiple-correct questions with all four options correct, options shown as "A. A)", and technical and behavioral questions that invented details (Java REST services, a tight deadline, a cross-functional team). The Groq prompts now ask about the technology behind a point and forbid assumed details (FR-9). Java drops a question with all four options correct and removes an option's own letter label.
- The behavioral template now reads "Tell me about a time you took the lead while working on X." (was "Tell me, out loud, about a time you faced a chance to lead…").
- The "Start your first practice session" button on the role skills check (W7) was disabled as "Coming soon". It now starts a session, or continues an unfinished one, like "Practice with this resume".
- Questions already stored for a resume are not rewritten. They stay in the pool until mastered.
- Resume parsing failed with "We could not read this resume automatically" after any Groq 429. Groq's `openai/gpt-oss-120b` allows only 8,000 tokens per minute, so a burst of key-fact or question calls hit it, and the gate then blocked every Groq call for a fixed 15 minutes. A per-minute limit now cools down only for the reply's `Retry-After` (15 minutes when there is none). Daily limits still last until UTC midnight (trd 7.5).
- AI fill-in-the-blank questions were almost always rejected, so sessions fell back to the "Your resume says … Which word completes it?" template (only 3 for a test resume). The model writes blanks of 7 or 10 underscores, and for one blank sends a flat list of wordings. Java now accepts both and stores them in the usual `________` form with one list per blank.

**Starter roles narrowed to 11 (4 October 2026, Step 02)**
- Reduced the roles database from 42 to 11 roles across 3 categories: 4 Software Development (Software Engineer, Backend Developer, Frontend Developer, Full Stack Developer), 5 Data and AI (Data Analyst, Data Scientist, Data Engineer, Machine Learning Engineer, AI Engineer), and 2 Cloud and DevOps (DevOps Engineer, Cloud Engineer). This focuses the app on the core tech roles and simplifies role selection. The aliases and skills for these roles remain the same.
- `V2__starter_roles.sql` was already applied, so it is left as is: the new `V3__narrow_starter_roles.sql` deletes the other 31 starter roles. A role that a resume targets is kept, because deleting a role deletes its resumes (cascade). All 228 skills stay, so skills of removed roles still match on a resume. The unreviewed bank draft moved from `V3__starter_bank.sql` to `V4__starter_bank.sql`.
- `AiClient` (skill-bank ticket 01, kept): a cooldown or Retry-After of up to 15 seconds is waited out once; a longer one now fails at once instead of waiting 15 seconds and failing anyway. The Gemma path follows the same rule with its 60-second cap.

**Review and new direction (4 October 2026, owner decisions)** — details in `.scratch/skill-bank-redesign/spec.md`
- **Hybrid questions:** multiple-correct and fill-in-the-blank will come only from a reviewed practice bank (Java trusts their keys). Technical brief and voice questions will be written by AI about the user's projects, about 8 per resume in one call, each with its own key facts; this replaces the per-point key-facts drafting. Behavioral questions come from the bank. Replaces FR-9's per-resume generation and FR-20's AI-fed bank once built.
- **Interview mode** (new): about 6 open questions, spoken or typed, with one AI follow-up per answer, then marks, feedback and a coaching report. The quiz session stays.
- **Fallback is no longer silent** (reverses v15): keyword-graded answers show "Graded without AI" and a Regrade button.
- **Unchanged:** Groq gives the marks and Gemma writes the feedback and the report.
- **Bug fix, parsing without AI:** when the browser parse looked incomplete or low quality and the AI fallback was unavailable, the Review form opened **empty**, discarding the OpenResume result (and "Scan with AI" did the same). The browser parse is now kept, with the message "Parts of this resume may be missing or in the wrong box. Please check the form and fix them."
- **Keyword-graded label and regrade built** (skill-bank ticket 12): `Attempt.gradedWithoutAi`, `POST /sessions/{id}/attempts/{position}/regrade` (202, `regrade-unavailable`, `ai-unavailable`), and a "Graded without AI" note with a "Regrade with AI" button in the report.
- **"Add a missing role" removed** with its AI skill draft (`POST /roles`, `POST /roles/draft-skills`, the W5 panel). Roles added earlier stay in the database, private to their user.
- The reviewed bank starts with the **Full Stack Developer** skills. Drafted in `seed-draft/` (ticket 06), not loaded until reviewed. Extended by the owner to **every skill of the 11 roles** (74 skills): per skill 10 multiple-correct, 10 fill-in-the-blank (3 easy, 4 medium, 3 hard) and 5 technical briefs with 3 key facts each; Communication and Critical Thinking get 5 scenario briefs only. 1,810 skill questions plus 20 behavioral, all drafted. In a session, 2 of the 4 technical brief slots will come from these bank briefs and 2 from AI questions about the user's projects. It replaces the ticket 29 draft for the old 7 roles. No schema change was needed (ticket 05): the existing `difficulty` column holds the level, and `source = 'seed'` marks a reviewed row.
- Progress view across sessions (weakest skills, "study next"): approved 5 October 2026, built as ticket 14 (see below).

**Question mix and bank order (5 October 2026, owner decisions)** — `.scratch/skill-bank-redesign/spec.md` D3, D11, D12
- AI writes only **1 brief and 2 voice questions** per session, about the user's projects. The other 16 of the 19 questions come from the bank: 5 multiple-correct, 5 fill-in-the-blank, 3 technical brief, 2 behavioral brief and 1 behavioral voice. The session stays 19 questions and 80 marks. This replaces the "2 AI briefs + 1 technical voice" line above; sections 8 and 9 are rewritten when the build (tickets 07 to 10) lands.
- Mastered questions still rest one month and can then return (FR-13 unchanged).
- Bank questions are grouped by skill and shared by all roles. The bank is built first for the Software Development and AI roles; Data and Cloud/DevOps roles follow.
- **Mastery of bank questions follows the user** across resumes; project questions stay per resume (reverses "a new resume starts fresh" for bank questions only; ticket 18).
- **A session always has 19 questions.** When Whisper is down, voice answers use the browser's **Web Speech API** instead of dropping the voice part (replaces the 16-question, 50-mark rule in FR-20; ticket 19). Chrome sends that audio to its vendor's servers; the app still stores no audio.
- **Interview mode** (ticket 13) and the **progress view** (ticket 14) are approved. Interview mode's details are still to settle.
- **Objective and behavioral parts from the bank built** (ticket 07): multiple-correct, fill-in-the-blank and behavioral slots now come from reviewed bank rows (`source = 'seed'`) with no AI call; about 80% from skills on the resume and 20% from the role's gap skills (D4), the latter labelled "Likely to be asked for this role" (`Question.isGapSkill`, api-contracts 2.3, worked out from the bank row's skill, no new column). A skill with no reviewed rows falls back to the generic templates. Technical brief and voice questions still use AI until ticket 08; sections 8 and 9 are rewritten with ticket 17.
- **Skill aliases and bullet matching built** (ticket 04): the role skills check (FR-16) finds a skill by name or alias (`skill_alias`, `V4`) in the Skills line and in bullets, and shows the bullet as the evidence instead of repeating the skill name. The bank migration drafts shift to `V5` and `V6` when they move to `db/migration/`.
- **Per-user bank performance built** (ticket 18): new table `bank_performance` (`V5`), keyed by user and bank question. Session start, grading and the practice bank read and write it; a bank question mastered on one resume rests one month on every resume of that user. Project questions are unchanged. Export JSON needed no change (it reads attempts). The bank migration drafts shift to `V6` and `V7` (they were `V5` and `V6`).
- The requirement sections above are rewritten as each part is built, so they still describe the current app.

**Project questions built (ticket 08, 5 October 2026)**
- `ProjectQuestionService` replaces `KeyFactService`: one Groq call per resume after the save writes about 8 open questions about its projects and experience, each with 3 to 6 key facts in `question.key_facts` (3 stored as `brief`, 5 as `voice`; `source = 'ai'`, no point, no theme). A reply is kept only for questions with text and at least 3 key facts; nothing is stored when none pass. Retried on the Resume list and at session start, once per resume every 10 minutes (kept in memory, so a restart retries at once).
- Session start no longer calls AI. Brief and voice slots take stored project questions first, then bank rows, then templates. The exact 1 brief and 2 voice split is ticket 17, so until then up to 3 project questions can fill the technical brief slots.
- Per-point key-facts drafting and the `app.key-facts.gap-millis` setting are removed. `resume_point.key_facts_*` and `key_fact` are no longer written; ticket 10 dropped them (below).
- Not run: the MySQL tests (no Docker). `KeyFactDbTest` was deleted with its class and has no DB replacement yet.

**Old generation removed (ticket 10, 5 October 2026)**
- Deleted `SessionService.generate` with its validators, `PracticeBank.learn` (AI rows into the shared bank) with its personal-text check, the `QuestionService` stub and `KeyFacts`. Grading now reads only the question's own `key_facts`.
- New migration `V6__drop_key_fact.sql` drops `key_fact` and `resume_point.key_facts_*`. The bank drafts become `V7` and `V8` when moved. Export JSON is unchanged (it reads attempts).
- Not run: the MySQL tests and `V6` itself (no Docker). Unit tests pass.

**Session mix built (ticket 17, 5 October 2026)**
- A session is 5 multiple-correct and 5 fill-in-the-blank (bank), a brief part of 3 technical bank + 2 behavioral + 1 project question, and a voice part of 1 behavioral + 2 project questions: 19 questions, 80 marks. Project questions are the stored AI ones (`source = 'ai'`, no theme) and rest one month once mastered like any other. If a resume has too few of them, the bank's technical questions top the part up so it keeps its size. Voice unavailable no longer shrinks it (ticket 19). Docs updated: section 9 table, `trd.md` 7.2 and Step 3 check.

**Always 19 questions built (ticket 19, 5 October 2026)**
- Session start fills every slot: unmastered or weak first, then new or rested bank questions of the resume's skills, the role's gap skills, **any other skill in the bank**, the generic templates, and last, resting questions of the right kind that are not yet in the session. Nothing repeats in a session. No AI call is needed: with AI down the project slots come from stored project questions or the technical bank (the 1 brief and 2 voice project slots are topped up from technical questions when none exist).
- Whisper down: `voice_skipped` is always `false` for new sessions (the column and "voice part skipped" stay for old sessions). `VoiceAnswer.jsx` tries Whisper first; when transcription fails it asks the user to speak again through Web Speech and keeps doing so for the rest of the session. `PUT attempts` accepts `voiceStats` with only `durationSeconds` and Java fills the rest.
- "Other skills of the same role" (fill step 4) is built as any bank skill, as the role's own skills are already step 3. Say so if you meant something narrower.
- Not run: the MySQL tests (no Docker); the new `SessionDbTest` test and the browser check are still to do.

**Interview mode built (ticket 13, 5 October 2026, owner decisions)**
- New FR-21. 6 spoken or typed questions (4 technical, 2 behavioral), one AI follow-up each, 3 minutes per answer, 10 marks per question (5 typed), counts toward mastery. Settles the open points of the "Interview mode" entry above.
- `practice_session.mode` and `attempt.follow_up_text` / `follow_up_answer` (`V7`). `POST /sessions` takes `mode`; `POST` and `PUT /sessions/{id}/attempts/{position}/follow-up` are new (api-contracts 6). Grading adds the follow-up answer to the evidence; the follow-up question is not evidence.
- The 3-minute timer is enforced by the browser only; the server accepts an answer at any time while the session is open.
- Not run: the MySQL tests (no Docker), so the `V7` migration and the new SQL have only been exercised by the manual check.

**Progress view built (ticket 14, 5 October 2026)**
- `GET /resumes/{id}/progress` (api-contracts 5) and a **Progress** page linked from the Resumes list: skills of bank questions weakest first with marks per completed session, and a "Study next" list (3 lowest skills under 100% plus up to 3 role gap skills). Scoped to the signed-in user; no AI, no schema change. Project questions have no skill and are not counted.

**UI redesign "Red pen on paper" (10 October 2026, owner decisions)**
- New visual identity: warm paper background, ink-black text and primary buttons, one red-pen accent for marks and highlights, Fraunces (serif) headings with Geist body, flat 8 px corners and hard offset shadows. No gradients anywhere (including the landing page hero, which had one in the docs). All glyph icons (tick, cross, dot, arrows) are now Phosphor SVG icons.
- **Dark mode is now in scope** and follows the system setting (`prefers-color-scheme`); there is no manual toggle. This reverses the v1 "no dark mode" decision. The landing page now uses the same tokens as the app (it was documented as dark).
- Layout polish inside the existing wireframes: step indicator on Upload, Review and Role skills check, choice cards for modes and answers, circled marks on scores, status chips in History and the report. No route, API call or copy changed. New dependency: `@fontsource-variable/fraunces`.
- Not run: no browser check yet (the Chrome extension was not connected), so the new screens have only been compiled (`vite build`), not looked at.

**SDE focus: story bank removed (ticket sde-focus/01, 10 October 2026, owner decision)**
- The scope is SDE, Full Stack, Backend and Frontend roles. The story bank was removed because nothing in the practice flow read it. Gone: the Stories screens and menu item, "Save as story" in the report, the `/stories` endpoints, the story section of the export JSON (now five sections), and the three story tables (`V9__drop_story.sql`, which deletes the data). Build order loses its Story bank step. FR-17 stays as an empty number.
- `Attempt.id` is no longer sent (it existed only for `sourceAttemptId`).
- Not run: the MySQL tests and `V9` itself (no Docker, and the backend `src/test` folder is deleted in the working tree). Compile and frontend build only.

**SDE focus: interview date removed (ticket sde-focus/02, 10 October 2026, owner decision)**
- Upload, Review and the Resume list no longer have an interview date; `PATCH /resumes/{id}` is gone, so a saved resume has no editable field. `V10__drop_interview_date.sql` drops the column. FR-2 and the user flow are updated. Not run: `V10` (no MySQL here).

**SDE focus: only the 4 software development roles (ticket sde-focus/03, 10 October 2026, owner decision)**
- Data and AI (5 roles) and Cloud and DevOps (2 roles) are out of scope. `V11__narrow_to_software_roles.sql` deletes them, the resumes that target them (with their questions and sessions), and the practice bank rows of every skill that no remaining role lists (30 skills stay in the bank, 750 skill rows plus 20 behavioral in the draft count; Communication, Critical Thinking and Python are no longer listed by any role). The `skill` table is unchanged, so skills still match on a resume.
- The unreviewed Data, Data Engineering and DevOps drafts (`bank_data.py`, `bank_dataeng.py`, `bank_devops.py`, `review_wave2.md`, the wave 2 SQL) were deleted from `seed-draft/`; they are in git history. `gen_bank.py` now covers 4 roles. `V8` is unchanged: its AI and ML rows are removed by `V11`. Not run: `V11` (no MySQL here).
- Open for the owner: Python is not listed by any of the 4 roles, so it has no bank rows. Say if Backend or Software Engineer should list it.

**SDE focus: Gemma removed (ticket sde-focus/04, 10 October 2026, owner decision)**
- Groq now writes each answer's feedback in the same reply as its checklist (`text`, `whatToFix`, `deliveryComment` for a recorded answer), and one Groq call writes the session report. The Gemma provider, `GOOGLE_AI_KEY`, the model setting, the 7-second gap and the `FeedbackService` stub are gone. A reply with a good checklist but no usable text still counts for the marks, and that answer gets the template text (`feedback_status = 'none'`). Keyword-graded answers get template text as before.
- Marks are unchanged by the feedback text: the ticks and the evidence rule still decide them. The checklist prompt is longer, so each answer uses more Groq tokens; measure it (Section 15 still open).
- The old `GOOGLE_AI_KEY` is no longer read. If it is a real key, revoke it in Google AI Studio and remove it from `application-local.yml`.
- Not run: no live Groq call and no MySQL here, so the new prompt and the feedback write have only been compiled.

**SDE focus: live words, spoken interviewer, design questions, role flavours (tickets sde-focus/05 to 11, 10 October 2026, owner decisions)**
- **Live words (05, 06):** `VoiceAnswer` runs the browser's speech recognition (interim results) next to the recorder and shows the words, pace and filler count in an `aria-live` box (only finished sentences are announced). Whisper's transcript replaces it on stop and stays the graded source. The same helper (`lib/liveWords.js`) now also gives the Web Speech transcript when Whisper is down, and keeps listening through pauses (Chrome ends recognition after silence). A throwaway page, `frontend/public/spike-live-captions.html`, checks that Chrome and Edge run both on one microphone. **The owner's result is still open**; the code never lets a live-words problem touch the recording.
- **Spoken interviewer (07):** `lib/speak.js` reads the question and follow-up; mute (remembered in the browser), replay, mic and timer wait while it speaks.
- **Code in monospace (08):** `lib/richText.js` and `Rich` render backtick code and fenced blocks on Practice, Interview and Report.
- **Design questions (09):** `V12` adds `bank_question.format` and `question.format` (`standard`, `design`, `code`) and `bank_question.track`. The interview is now 3 project questions, 1 design question, 2 behavioral. `Grading.itemKeys` has a 5-item design checklist and `Grading.score` scales marks per item (max / items). Keyword cues cover the 5 areas.
- **Role flavours (10, 11):** `PracticeBank.TRACKS` maps a role to its tracks; bank fill skips rows of other tracks and never uses design rows for ordinary slots. Software Engineer gets the backend flavour (change one line to alter that).
- **Drafts to review:** `seed-draft/V13__flavoured_bank.sql` and `review_flavours.md` hold 52 unreviewed questions (16 design, 13 SQL and endpoint, 23 frontend). They are **not loaded**: review, then move the file to `db/migration/`. Until then an interview has 5 questions plus a technical top-up, and Backend and Frontend sessions have no flavoured questions.
- Checked here: compile, `vite build`, `node --test` on the new helpers, and a throwaway Java check of the design scoring and interview slots. Not run: `V12`, `V13`, any browser or microphone, any live Groq call.
