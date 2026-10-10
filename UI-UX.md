# UI/UX: Abhyas.tech

**Status:** Draft v2, from the grilling sessions on 3 October 2026. Every decision below was answered by the owner; items marked *(my decision)* were filled in by me and can be changed. v2 folds in the layout decisions from the wireframes session.
**Related:** `prd.md` (requirements, FR numbers), `CONTEXT.md` (glossary), `wireframes.md` (screen layouts W0 to W12).

---

## 1. Principles

1. **Calm and minimal, like a marked exam sheet.** Interview practice is stressful. No gamification (streaks, badges). "Red pen on paper" identity (10 October 2026): warm paper, ink-black text, one red-pen accent for marks and highlights, flat solid colors, hard offset shadows, short settled motion. Light theme by default, dark theme when the system asks for it.
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

Defined once as CSS custom properties on `:root`. No gradients anywhere: solid colors, soft shadows and translucent materials (`backdrop-filter`) only. v4 (10 October 2026) replaced the Apple-style v3 palette with the "red pen on paper" tokens in `frontend/src/styles/tokens.css` (paper `#FAF7F0`, card `#FFFFFF`, ink `#1B1A17`, pen red `#C8321F`, radius 8 px, Fraunces headings, Geist body, a dark set under `prefers-color-scheme: dark`); that file is the current source and the table below is the v2 original. v3 (3 October 2026) had restyled the tokens Apple-style: `--color-bg` is now `#F5F5F7` (page) and `--color-surface` is `#FFFFFF` (cards), primary is `#0066CC`, radius is 12 px (buttons are pills), font is Geist with a system fallback, and motion uses `--ease` (`cubic-bezier(.32,.72,0,1)`), press feedback at 120 ms and entrances at 280 ms. The table values below are the v2 originals; `frontend/src/styles/tokens.css` is the current source. `prefers-reduced-motion`, `prefers-reduced-transparency` and `prefers-contrast: more` are honored. Colors are never the only signal: success, warning and error each pair a color with an icon and a word.

| Token | Value | Use |
|---|---|---|
| `--color-bg` | `#FFFFFF` | page background |
| `--color-surface` | `#F5F7FA` | cards, form panels |
| `--color-border` | `#D1D5DB` | borders, dividers |
| `--color-border-strong` | `#6B7280` | borders of inputs and buttons (4.8:1 on white; the light border was 1.5:1, below the 3:1 WCAG 1.4.11 needs) |
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
| `--shadow` | `0 1px 2px rgba(31,41,55,.08)` | cards (a hairline lift, not a gradient) |
| `--font-size-h1..h3` | 1.875, 1.375, 1.125 rem | heading scale |

**Buttons:** secondary (white, strong border) by default, `.primary` (blue) for the one main action of a block, `.danger` (red text and border) for delete and leave. Error messages (`role="alert"` with text) show as a red-edged box that keeps the "!" and the word "Error".

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
- Content, top to bottom: hero (heading, one call to action, privacy proof line, an example marked answer), the problem, five benefits, a tagline section whose words light up one by one on scroll, the three steps, a privacy note, FAQ, a final call to action identical to the first, and an AGPL-3.0 footer. The YouTube tutorial and GitHub links sit in the footer, each shown only once its URL is set.
- **Visual (10 October 2026):** the landing page uses the same tokens as the app, with larger serif type, a hand-drawn red underline in the hero, a floating paper-and-ink nav bar with a hamburger menu on narrow screens, and scroll reveals. Styles are scoped to `.lp` in `landing.css`. No gradients. Motion is switched off under `prefers-reduced-motion`.
- After login or sign up the user lands on Home (6.2), not the landing page.

### 6.1 Sign up / log in (FR-1)
- Fields: userId, password. Sign up and log in switch on one page.
- Inline errors under the failed field (Section 7.1). No OAuth.

### 6.2 Home
- **First time (no resume yet):** one call to action, "Upload your resume". The first path is: sign up, upload, review, role skills check, first practice session.
- **After that, top to bottom:** a "Continue your practice session" card (only if one is unfinished, Section 6.7), then a resume selector (default: the most recently used resume) with a **Mode** choice (Quiz or Interview) and a "Start practice session" ("Start interview") button, then the latest session summary (total, mastered count, weakest resume points) with an "Open report" button. With no completed session yet the last block says "No practice sessions yet."

### 6.3 Resume list (FR-2)
- One row per resume: file name, target role, upload date, mastered question count.
- Actions per row: **Practice with this resume** (starts a session), **Role skills check** (opens 6.6 for that resume), Open, Delete. Delete asks for confirmation inline.
- There is no "active" resume. The resume is chosen when a session starts and cannot change during it.

### 6.4 Upload resume and review (FR-2, FR-4 to FR-6)
- **Two screens with a step indicator:** 1 Upload, 2 Review, 3 Role skills check. Back from Review returns to Upload with the file kept.
- **Upload:** PDF chooser, **target role** search (names and aliases, FR-3), optional interview date. If no role matches, the field says so and suggests search words; the 11 roles are fixed (6.5).
- **Review form:** one long form with collapsible sections (contact, education, experience, projects, skills), editable, with a Save button.
- When the fallback parse ran, a banner says "Some parts were read by an AI assistant. Please check them." Sections that came back empty are highlighted. A "Scan with AI" button is always shown on the preview, so the user can run the fallback parse even when the browser parse looked complete.
- Phone and email are shown, with a note that they are never sent to AI.
- Saving writes the resume first, then prepares resume points and key facts ("Preparing your questions..."). If AI is unavailable the resume is still saved and the key facts are prepared later (see FR-8 in `prd.md`).

### 6.5 Add a missing role (removed)
- Removed on 4 October 2026: the role list is fixed at 11 (FR-3). With no match the role field shows: No role found. Try "Backend", "Data" or "DevOps".

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
- **Whisper down:** the voice part stays. If transcription fails, the screen says "We couldn't turn your recording into text. Speak your answer again: your browser will write it down." and the next recording uses the browser's speech recognition (Chrome, Edge). Privacy note on the landing page and in the FAQ: in Chrome that audio goes to Google to be turned into text; we store none. No speech recognition or no microphone: the typed box (FR-20).

### 6.7a Interview (FR-21, W8i)
- One question per screen, forward only, with a progress bar and a countdown ("Time left: 02:41") for each answer, 3 minutes.
- The user speaks (the voice answer flow of 7.3) or types. "Next: follow-up" saves the answer and shows one follow-up question under the original one, answered the same way with its own timer. Saying nothing and pressing Skip question scores zero and has no follow-up.
- When the timer ends the answer is sent as it stands; a recording in progress finishes first. The last button reads "Finish interview" and opens the report.
- No feedback during the interview. Leave interview asks for confirmation like Leave session.
- The report shows each follow-up and the reply under the answer. History has a Mode column.

### 6.8 Session report (FR-12)
- **Top summary:** a row of stat tiles (total out of 80, or 50 on an old session with the voice part skipped, with a percentage; mastered count; weakest resume points), then a small table of marks per part, then Gemma's short report text. No charts: the table is the accessible form of the numbers.
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
- No banner or dialog; bank questions look like AI ones. A **keyword-graded answer** shows, under its detail in the report: "Graded without AI: keyword matching gave this mark, so it may be too strict, and it does not count toward mastery." plus a secondary button **Regrade with AI** (disabled while grading runs, "Regrading…" while busy, an inline error if AI is still unavailable). Changed 4 October 2026; it was fully silent before (FR-20).
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

A manual dark-mode toggle (dark follows the system), gamification, timers, live captions, phone support, toast notifications, an "active resume" setting, any visible AI-fallback indicator.

## 11. Wireframes

Layouts for every screen above are in `wireframes.md` (W0 shell to W12 account). They are low-fidelity text boxes with the real copy, drawn at desktop width with narrow-width rules written as notes. The layout questions that were open in v1 (session progress bar and part indicator, report summary and question list, where "Add a missing role" goes, History and Story bank layouts) are settled there.

No open items. Possible later additions: a read-only "key facts" view outside the report, and narrow-width wireframes if phone support is ever added.
