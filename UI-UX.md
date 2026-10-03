# UI/UX: Abhyas.tech

**Status:** Draft v2, from the grilling sessions on 3 October 2026. Every decision below was answered by the owner; items marked *(my decision)* were filled in by me and can be changed. v2 folds in the layout decisions from the wireframes session.
**Related:** `prd.md` (requirements, FR numbers), `CONTEXT.md` (glossary), `wireframes.md` (screen layouts W0 to W12).

---

## 1. Principles

1. **Calm and minimal.** Interview practice is stressful. Light theme only, no gamification (streaks, badges), no dark mode in v1.
2. **Defend your resume.** Every screen points back to the user's own resume points, not generic content.
3. **Plain CSS, shared tokens.** No UI library or Tailwind. Colors, spacing and type come only from the tokens in Section 4, so screens don't drift.
4. **Accessible by default.** WCAG 2.1 AA (Section 8).
5. **Never lose work.** Answers are saved as the user moves on (Section 6.6).
6. **Silent fallback.** If AI is unavailable the app falls back without telling the user (FR-20). Only a hard failure shows a message.

## 2. Users and context

- Multiple users on one laptop-hosted app (localhost). Each sees only their own data.
- Desktop-first. Tablet-width layouts must not break. Phones are not supported in v1, and voice recording on a phone is out of scope.
- Voice answers need a microphone. Voice is never the only way to answer (Section 7.3).

## 3. Terminology

The UI uses the names on the left. The PRD and code keep the internal names on the right. Full definitions are in `CONTEXT.md`.

| UI term | Internal term |
|---|---|
| Resume point | Claim |
| Key facts | Answer key |
| Practice session | Session |
| Role skills check | Gap check |
| Mastered | Mastered (same) |
| Story bank | Story bank (same) |
| Target role | Target role (same) |

## 4. Design tokens *(my decision: exact values)*

Defined once as CSS custom properties on `:root`. No gradients anywhere (confirmed): flat colors only. Colors are never the only signal: success, warning and error each pair a color with an icon and a word.

| Token | Value | Use |
|---|---|---|
| `--color-bg` | `#FFFFFF` | page background |
| `--color-surface` | `#F5F7FA` | cards, form panels |
| `--color-border` | `#D1D5DB` | borders, dividers |
| `--color-text` | `#1F2937` | body text |
| `--color-text-muted` | `#4B5563` | secondary text |
| `--color-primary` | `#1D4ED8` | primary buttons, links, focus ring |
| `--color-success` | `#15803D` | correct, mastered |
| `--color-warning` | `#B45309` | needs work |
| `--color-error` | `#B91C1C` | errors, wrong, destructive actions |
| `--space-1..6` | 4, 8, 16, 24, 32, 48 px | spacing scale |
| `--font` | `system-ui, -apple-system, "Segoe UI", sans-serif` | one font family |
| `--font-size-base` | `16px` | body text; headings scale up from it |
| `--radius` | `6px` | cards, buttons, inputs |

Contrast of every text and color pairing is checked against WCAG AA (4.5:1 for normal text) when the CSS is written. White text on `--color-primary` is used for primary buttons.

## 5. Navigation

- A **top bar** with: Practice (Home, at `/practice`), Resumes, Stories, History, Account. The "Abhyas.tech" name in any top bar links to the landing page (Section 6.0).
- During a practice session the top bar is hidden, so the user cannot leave by accident. The only way out is "Leave session" (Section 6.6).
- No resume state in the nav (there is no "active resume").
- **Page shell:** content sits in a centered single column, max width 960 px, under a full-width top bar. The Practice session uses a narrower column (about 720 px) with no top bar. The current nav item is marked with an underline and `aria-current="page"`, and "Skip to content" is the first tab stop.

## 6. Screens

Each screen lists its purpose, what it shows, and its actions. FR numbers refer to `prd.md`.

### 6.0 Landing page (W13)
- Public page at `/`, shown to everyone, signed in or not. It has its own simple top bar: the name, and "Log in" and "Sign up" (or "Go to practice" when signed in).
- Content: what the product does, the four-step flow, the extras (role skills check, story bank, Export JSON), a privacy note, a link to the YouTube tutorial and a link to the GitHub repository (each shown only once its URL is set), and an AGPL-3.0 footer.
- After login or sign up the user lands on Home (6.2), not the landing page.

### 6.1 Sign up / log in (FR-1)
- Fields: userId, password. Sign up and log in switch on one page.
- Inline errors under the failed field (Section 7.1). No OAuth.

### 6.2 Home
- **First time (no resume yet):** one call to action, "Upload your resume". The first path is: sign up, upload, review, role skills check, first practice session.
- **After that, top to bottom:** a "Continue your practice session" card (only if one is unfinished, Section 6.7), then a resume selector (default: the most recently used resume) with a "Start practice session" button, then the latest session summary (total, mastered count, weakest resume points) with an "Open report" button. With no completed session yet the last block says "No practice sessions yet."

### 6.3 Resume list (FR-2)
- One row per resume: file name, target role, upload date, mastered question count.
- Actions per row: **Practice with this resume** (starts a session), **Role skills check** (opens 6.6 for that resume), Open, Delete. Delete asks for confirmation inline.
- There is no "active" resume. The resume is chosen when a session starts and cannot change during it.

### 6.4 Upload resume and review (FR-2, FR-4 to FR-6)
- **Two screens with a step indicator:** 1 Upload, 2 Review, 3 Role skills check. Back from Review returns to Upload with the file kept.
- **Upload:** PDF chooser, **target role** search (names and aliases, FR-3), optional interview date. If no role matches, a link opens **Add a missing role** (6.5).
- **Review form:** one long form with collapsible sections (contact, education, experience, projects, skills), editable, with a Save button.
- When the fallback parse ran, a banner says "Some parts were read by an AI assistant. Please check them." Sections that came back empty are highlighted. A "Scan with AI" button is always shown on the preview, so the user can run the fallback parse even when the browser parse looked complete.
- Phone and email are shown, with a note that they are never sent to AI.
- Saving writes the resume first, then prepares resume points and key facts ("Preparing your questions..."). If AI is unavailable the resume is still saved and the key facts are prepared later (see FR-8 in `prd.md`).

### 6.5 Add a missing role (FR-3)
- Shown only when search finds no match by name or alias. It is an **inline panel** that expands under the role search on the Upload screen (not a page or dialog), so the chosen file is not lost. Focus moves to the panel heading when it opens and back to the role field when it closes.
- The AI drafts the skills, shown as an editable list. The user edits and saves. The role is private to the user. If AI is unavailable the list starts empty and the user types their own skills.
- Before saving, a near-duplicate name or alias is checked ("A similar role exists: ... Use it instead?").

### 6.6 Role skills check (FR-16)
- Two lists: role skills with evidence on the resume, and role skills without. Each evidence item links to the resume point.
- Step 3 of the first-time path, and reachable later from the "Role skills check" button on each row of the Resume list. In the first-time path the last button reads "Start your first practice session". If the resume has no skills, it says so and links back to the Review form.

### 6.7 Practice session (FR-9 to FR-12)
- **Start:** the user picks a resume on Home. The resume is fixed for the whole session.
- **Layout:** the four parts are shown one part per page: multiple-correct, fill-in-the-blank, brief answers, voice answers. Inside a part, **one question per screen**, with a progress bar (question N of 19) and a part indicator.
- **Navigation:** Back is allowed until the session is submitted. Skipping a question counts as zero. No timer.
- **Autosave:** each answer is saved when the user moves on. Closing the tab keeps progress, and Home shows "Continue your practice session". An unfinished session cannot be exported.
- **No feedback during the session.** Feedback appears only in the report.
- **Leave session:** asks for confirmation, then marks the session abandoned in History.
- **Voice answer flow** (Section 7.3).
- **Voice part skipped:** if Whisper is unavailable when the session starts, the voice part is left out: 16 questions and 50 marks (FR-20).

### 6.8 Session report (FR-12)
- **Top summary:** a row of stat tiles (total out of 80, or 50 if the voice part was skipped, with a percentage; mastered count; weakest resume points), then a small table of marks per part, then Gemma's short report text. No charts: the table is the accessible form of the numbers.
- **Below:** all questions grouped by the four parts. Each question is a collapsible row whose header is a button (`aria-expanded`) showing the number, type, marks and a status icon with a word, for example "Q7 · Brief answer · 3/5 · Needs work", followed by the first line of the question. Questions that are not mastered are open by default, mastered ones are collapsed.
- **Gradual feedback:** Gemma's feedback arrives one call at a time, so each row shows "Feedback is being written..." until its text arrives.
- **Per question:**
  - Multiple-correct and fill-in-the-blank: marks and the correct options or accepted answers.
  - Brief and voice: marks, the checklist with a tick or cross, the quoted proof for each tick, the key facts (read-only), Gemma's feedback with a "what to fix" line, and voice stats beside voice answers.
- **Save as story:** a button on behavioral answers (Section 6.10).

### 6.9 History and export (FR-19)
- A table with the columns Date, Resume, Role, Status, Total, Action. Status uses an icon plus a word (completed, in progress, abandoned, "voice part skipped"). Resume is shown because the resume is chosen per session. On narrow widths the table scrolls sideways and the Action column stays visible. Empty: "No practice sessions yet. Start one from Home."
- Completed rows have an **Export JSON** button that downloads the file directly with no preview. In-progress rows show **Continue**. Clicking a completed row opens its report.

### 6.10 Story bank (FR-17)
- A single-column list of cards (title, tags, a two-line preview, Edit and Delete) with tag filter chips above (real buttons with `aria-pressed`). The edit form is on **its own page**, not a side panel, which is simpler for keyboard and screen-reader users.
- Form fields: title, **separate boxes for Situation, Task, Action and Result**, lesson learned, tags, optional links to resume points.
- **Save as story** on a behavioral answer opens the form pre-filled, with the answer text in the Action box for the user to split up.

### 6.11 Account and deletion (FR-18)
- Account details, and a **Danger zone** section.
- Delete flow: a confirmation page that lists exactly what is deleted (resumes, resume points, key facts, questions, answers, transcripts, scores, stories, gap results, roles the user added), a link to "Export JSON first" for past sessions, a password box and a typed word, DELETE. The delete button stays disabled until both are correct.

## 7. Cross-cutting states

### 7.1 Errors
- One pattern: an **inline message under the failed action**, with a plain-language cause and a Retry button. No toast pop-ups (they disappear too fast and fail accessibility).
- Only an expired login causes a full-page redirect to log in, and it returns the user to where they were.
- Examples: upload not a PDF, file too large, parse failed (use the review form), role not found, save failed.

### 7.2 Empty and loading
- Every list has an empty state with one sentence and one action (for example "No stories yet. Save one from a behavioral answer.").
- Loading shows a spinner with text (never color alone) and keeps the page layout stable. Slow AI steps (parsing, grading) say what is happening, for example "Marking your answers".

### 7.3 Voice answer
1. **Record**, with a visible recording indicator and a stop button.
2. **Transcribe** automatically (Whisper).
3. **Show the transcript** with **Re-record** and **Submit**.
- **Mic denied or Whisper failed during the question:** show "Type your answer instead". A typed answer is graded like a brief answer (5 marks), with no delivery stats, and the question loses 5 of its 10 marks. The question stays a voice question and is offered again in a later session (FR-20).
- Live captions are not included (not in the PRD).

### 7.4 AI unavailable
- **Silent.** No banner, dialog or label. Bank questions and keyword grades look like AI ones. `graded_by` is stored and exported only (FR-20, FR-19).
- A hard failure with no fallback available shows the inline error pattern in 7.1.

## 8. Accessibility (WCAG 2.1 AA)

- Everything works by keyboard, in a logical tab order, with a visible focus ring (`--color-primary`).
- All inputs have visible labels, and errors are announced next to the field.
- Color is never the only signal. Success, warning and error also use an icon and a word.
- Text contrast meets 4.5:1, and large text and UI components 3:1.
- Voice answers always have a typed alternative.
- Progress, loading and recording states are exposed to screen readers.

## 9. Copy tone

- Feedback headlines are supportive, like a coach: "Nice start. Two points still need work."
- Numbers and checklist items are neutral and factual: "2 of 5 marks. 3 items missing."
- Never blame the user. Words like "missing" describe the answer, not the person.
- UI terms come from the table in Section 3.

## 10. Out of scope (v1)

Dark mode, gamification, timers, live captions, phone support, toast notifications, an "active resume" setting, any visible AI-fallback indicator.

## 11. Wireframes

Layouts for every screen above are in `wireframes.md` (W0 shell to W12 account). They are low-fidelity text boxes with the real copy, drawn at desktop width with narrow-width rules written as notes. The layout questions that were open in v1 (session progress bar and part indicator, report summary and question list, where "Add a missing role" goes, History and Story bank layouts) are settled there.

No open items. Possible later additions: a read-only "key facts" view outside the report, and narrow-width wireframes if phone support is ever added.
