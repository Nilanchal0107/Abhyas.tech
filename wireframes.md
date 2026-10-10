# Wireframes: Abhyas.tech

**Status:** Draft v1, from the grilling session on 3 October 2026.
**Related:** `UI-UX.md` (principles, tokens, screens, states), `prd.md` (FR numbers), `CONTEXT.md` (glossary).

## How to read this file

- **Format:** low-fidelity text boxes with the real UI copy. Colors, spacing and type come from the tokens in `UI-UX.md` Section 4 and are not repeated here.
- **Width:** drawn at one desktop width (about 1280 px). Narrow-width behavior is written as a rule under each screen (phones are not supported).
- **Content area only:** except for the shell (W0) and the Practice session, screens show just the content area under the top bar.
- **Notation:** `[ Button ]` button, `[____]` text input, `( )` radio, `[ ]` checkbox, `[v]` select, `▸ / ▾` collapsed / open, `...` more of the same.
- **Status words:** every status uses an icon and a word, never color alone (shown here as `✓ word`, `! word`, `✗ word`).
- Each screen: purpose, layout, then notes (FR, states, keyboard order, narrow rule).

## Contents

W0 Shell · W1 Sign up / log in · W2 Home · W3 Resume list · W4 Upload · W5 (removed) · W6 Review · W7 Role skills check · W8 Practice session (8a to 8f) · W9 Session report · W10 History · W11 Story bank (list and form) · W12 Account and deletion · W13 Landing page

---

## W0. Page shell

Shared by every screen except the Practice session.

```
+--------------------------------------------------------------------------+
|  Abhyas.tech   Practice  Resumes  History  Account  [Log out] |
+--------------------------------------------------------------------------+
|                                                                          |
|            +--------------------------------------------+                |
|            |  Page title                                |                |
|            |                                            |                |
|            |  content area (centered, max width 960 px) |                |
|            |                                            |                |
|            +--------------------------------------------+                |
+--------------------------------------------------------------------------+
```

- The current nav item is shown with an underline and `aria-current="page"` (not by color alone).
- "Skip to content" link is the first tab stop.
- Narrow rule: nav items wrap to a second row. Content stays one column.

---

## W1. Sign up / log in (FR-1)

Purpose: get the user into their account. No top bar until logged in.

```
            +--------------------------------------+
            |  Abhyas.tech              |
            |                                      |
            |   ( Log in )   ( Sign up )           |
            |                                      |
            |  User ID                             |
            |  [______________________________]    |
            |  Password                            |
            |  [______________________________]    |
            |  ✗ That user ID or password is       |
            |    not right. Try again.             |
            |                                      |
            |  [ Log in ]                          |
            +--------------------------------------+
```

- Sign up shows the same form with a "Confirm password" field and the button "Create account". Passwords are hashed server-side (FR-1).
- Errors appear inline under the field or button that failed (UI-UX 7.1). A taken user ID: "That user ID is already used."
- Tab order: Log in / Sign up switch, User ID, Password, button.

---

## W2. Home

Purpose: start or continue practice. Single column.

**First time (no resume):**

```
  Welcome. Let's start with your resume.

  +----------------------------------------------+
  |  Upload your resume                          |
  |  We turn it into questions about the things  |
  |  you will be asked to defend.                |
  |                                              |
  |  [ Upload your resume ]                      |
  +----------------------------------------------+
```

**Returning user:**

```
  +----------------------------------------------+
  |  ! Continue your practice session            |   (only if unfinished)
  |  Started 3 Oct · Part 3 of 4 · Question 11   |
  |  [ Continue ]   [ Leave session ]            |
  +----------------------------------------------+

  Start a practice session
  Resume        [v] Paras_Resume.pdf (Backend Developer)
  [ Start practice session ]

  Latest practice session (3 Oct)
  Total 61 / 80 · Mastered 9 of 19
  Weakest resume points:
   • Designed REST APIs for ...
   • Reduced query time by ...
  [ Open report ]
```

- The resume selector defaults to the most recently used resume. There is no "active" resume (UI-UX 6.2).
- With no completed session yet, the "Latest practice session" block is replaced by "No practice sessions yet."
- Narrow rule: none needed (single column).

---

## W3. Resume list (FR-2)

Purpose: manage resumes and start practice with one.

```
  Your resumes                                    [ Upload a resume ]

  +----------------------------------------------------------------+
  | Paras_Resume.pdf                                               |
  | Target role: Backend Developer · Uploaded 28 Sep               |
  | Mastered questions: 14                                         |
  | [ Practice with this resume ] [ Role skills check ] [ Open ] [ Delete ] |
  +----------------------------------------------------------------+
  +----------------------------------------------------------------+
  | Paras_Resume_v2.pdf                                            |
  | ...                                                            |
  +----------------------------------------------------------------+
```

- "Practice with this resume" starts a session with that resume (UI-UX 6.3).
- "Role skills check" opens W7 for that resume and its target role.
- Delete asks for confirmation inline ("Delete this resume and its questions and scores? [ Delete ] [ Cancel ]").
- Empty: "No resumes yet." with an Upload a resume button.
- Narrow rule: the four buttons wrap onto two lines.

---

## W4. Upload (FR-2)

Purpose: step 1 of 3. Choose the file and the target role.

```
  Step 1 of 3: Upload      2 Review      3 Role skills check

  Resume (PDF)
  [ Choose file ]  Paras_Resume.pdf  (212 KB)

  Target role
  [ Search roles, e.g. "SDE" or "Backend" ____ ]
   ▾ Software Development Engineer (SDE)
     Backend Developer
     Full Stack Developer

  [ Continue ]
```

- Role search matches names and aliases (FR-3). Typing shows a listbox of results with arrow-key navigation.
- No match shows: No role found. Try "Backend", "Data" or "DevOps". (UI-UX 6.5; adding a role was removed.)
- Errors (inline, UI-UX 7.1): not a PDF ("Please choose a PDF file."), file too large, no role selected ("Choose a target role to continue.").
- Loading after Continue: "Reading your resume..." (the OpenResume parse runs in the browser; the fallback runs if it looks incomplete). Then the Review screen opens.
- Narrow rule: none needed.

---

## W5. Add a missing role (removed)

Removed on 4 October 2026: the role list is fixed at 11 (FR-3).

---

## W6. Review (FR-6)

Purpose: step 2 of 3. Check and fix the parsed resume. One long form with collapsible sections.

```
  1 Upload      Step 2 of 3: Review      3 Role skills check

  ! Some parts were read by an AI assistant. Please check them.   (fallback only)

  ▾ Contact
    Name   [__________]   Phone [__________]   Email [__________]
    Phone and email are never sent to AI.
  ▸ Education
  ▾ Experience                                    ! Nothing found
    [ Add a job ]
  ▾ Projects
    Project 1  Name [__________]
      Bullets  [__________________________]  [Remove]
               [__________________________]  [Remove]
      [ Add a bullet ]
  ▾ Skills
    [ Java ] [ Spring Boot ] [ MySQL ] [ Add a skill ____ ]

  [ Back ]                                       [ Save and continue ]
```

- Sections are buttons with `aria-expanded`. Empty sections are open and marked "! Nothing found" (icon plus word).
- Saving writes the final resume to MySQL, then creates resume points (FR-7) and key facts (FR-8), with "Preparing your questions..." while it runs. If AI is unavailable the resume is still saved and key facts are prepared later.
- Back returns to W4 with the file kept.
- Narrow rule: the three contact fields stack.

---

## W7. Role skills check (FR-16)

Purpose: step 3 of 3, also reachable from W3. Shows which role skills the resume proves.

```
  Role skills check: Backend Developer · Paras_Resume.pdf

  ✓ Skills with evidence on your resume (6)
   • Java            Designed REST APIs ... (Project 1)
   • Spring Boot     Built ... (Experience 2)
   • MySQL           ...

  ! Skills with no evidence yet (4)
   • Docker
   • Redis
   • Message queues
   • System design

  [ Start practice session ]       [ Back to resumes ]
```

- Each evidence item links to the resume point it came from.
- In the first-time path the last button reads "Start your first practice session".
- Empty (resume has no skills): "We found no skills on this resume. Go back and add some." with a link to W6.

---

## W8. Practice session (FR-9 to FR-13)

Purpose: answer 19 questions in four parts. No top bar. Narrower column (about 720 px). The resume is fixed for the whole session.

### W8 frame (all question types)

```
  Part 2 of 4: Fill in the blank          Question 7 of 19
  [██████████░░░░░░░░░░░░░░░░░░]  7 of 19 answered     [ Leave session ]

  +----------------------------------------------------------+
  |                  (one question, see 8a to 8e)            |
  +----------------------------------------------------------+

  [ Back ]                                   [ Next ]
```

- Progress bar is text plus bar, with `role="progressbar"`.
- Back is allowed until the session is submitted. Skipping (Next with no answer) counts as zero. No timer. No feedback during the session.
- Each answer is saved when the user presses Next, so closing the tab keeps progress (home shows "Continue").
- The last question's button reads "Submit practice session". The session is graded and the report (W9) opens.

### 8a. Multiple-correct (2 marks)

```
  Question 3 · Multiple correct · 2 marks
  Select every correct answer.

  In your project "Order Service" you used Spring Data JPA.
  Which of these are true about lazy loading?

  [ ]  A. It loads related data only when it is accessed
  [ ]  B. It always avoids the N+1 query problem
  [ ]  C. It needs an open persistence session when accessed
  [ ]  D. It cannot be turned off
```

- Four options, one or more correct. Real checkboxes in a `fieldset` with the question as `legend`.

### 8b. Fill in the blank (2 marks)

```
  Question 7 · Fill in the blank · 2 marks

  In Spring Boot, the ________ annotation marks a class as a REST
  controller that returns data directly instead of a view name.

  Your answer:  [____________________]
```

- One or two blanks. With two blanks each is 1 mark and they are numbered inputs ("Blank 1", "Blank 2").

### 8c. Brief answer (5 marks)

```
  Question 11 · Brief answer · Technical · 5 marks

  At Acme Corp you wrote: "Reduced API response time by 40%."
  How did you measure that result, and what did you change?

  [                                                        ]
  [                                                        ]
  [                                                        ]
                                                  0 words
```

- Behavioral brief answers use the same layout with a STAR hint line: "Tip: Situation, Task, Action, Result."
- The text area has a visible label (the question) and a word counter announced politely.

### 8d. Voice answer (10 marks)

```
  Question 16 · Voice answer · Behavioral · 10 marks

  Tell me about a time you faced a tight deadline while working
  on "Order Service".

  [ ● Start recording ]        or  [ Type your answer instead ]
```

### 8e. Voice answer, states

```
  Recording                         Transcript shown
  +--------------------------+      +------------------------------+
  | ● Recording  00:42       |      | Your answer (transcribed)    |
  | [ ■ Stop ]               |      | "At Acme we had two weeks..."|
  +--------------------------+      |                              |
                                    | [ Re-record ]  [ Submit ]    |
  Mic denied / Whisper failed       +------------------------------+
  +--------------------------------------------------+
  | ✗ We couldn't use your microphone.               |
  | Type your answer instead.                        |
  | [______________________________________________] |
  | Graded like a brief answer (5 marks), no voice   |
  | stats.                                           |
  +--------------------------------------------------+
```

- Flow: Record, then automatic transcription ("Transcribing..."), then the transcript with Re-record and Submit (UI-UX 7.3).
- A visible recording indicator (red dot plus the word "Recording" and a timer) is announced to screen readers.
- Typed fallback: graded like a brief answer (5 marks), no delivery stats, and the question loses 5 of its 10 marks (FR-20).
- If Whisper is unavailable the voice part stays: after a failed transcription the user speaks again through the browser's speech recognition, or types (see UI-UX 7.3). Sessions made before ticket 19 can still be "voice part skipped" (8f).

### 8i. Interview question (FR-21)

```
  Interview · Question 2 of 6 · Technical          [ Leave interview ]
  [=====----------]  1 of 6 done
  Time left: 02:41

  Why did you choose Kafka for the order events?

  Follow-up: What happens to an order event if a consumer is down?   (second step only)

  [ ● Start recording ]  or  [ Type your answer instead ]

  [ Next: follow-up ]        (second step: [ Next question ] / [ Finish interview ])
```

- Forward only, no Back. Empty main answer: the button reads "Skip question".

### 8f. Leave session and start-up variants

```
  Leave this practice session?
  Your answers so far are saved. The session will be marked
  "abandoned" in History.
  [ Leave session ]   [ Keep practicing ]
```

- Opening the Practice session (from Home or Resumes) with the voice part skipped shows the Part indicator as "Part 3 of 3" and the total as "out of 50". No explanation banner (silent fallback, UI-UX 7.4).
- Focus is trapped in the confirmation and returns to the Leave button when it closes.

---

## W9. Session report (FR-12)

Purpose: show results. Summary first, then all questions grouped by part.

```
  Practice session report · 3 Oct · Backend Developer · Paras_Resume.pdf

  Nice start. Three resume points still need work.         (the report headline)

  +-----------+  +-------------------+  +----------------------------+
  | Total     |  | Mastered          |  | Weakest resume points      |
  | 61 / 80   |  | 9 of 19           |  | • Designed REST APIs ...   |
  | 76%       |  |                   |  | • Reduced query time ...   |
  +-----------+  +-------------------+  +----------------------------+

  Marks by part
  +--------------------------------+--------+-------+
  | Part                           | Marks  | Out of|
  | Multiple correct               | 8      | 10    |
  | Fill in the blank              | 7      | 10    |
  | Brief answers                  | 22     | 30    |
  | Voice answers                  | 24     | 30    |
  +--------------------------------+--------+-------+

  Session summary: ...a short paragraph...

  Part 3: Brief answers
  ▾ Q11 · Brief answer · 3/5 · ! Needs work
      At Acme Corp you wrote "Reduced API response time by 40%" ...
      Your answer: "..."
      Checklist
       ✓ Names the change you made   "I added a Redis cache ..."
       ✗ Explains why
       ✓ Consistent with my resume   "..."
       ...
      Key facts (read-only): • ... • ...
      Feedback: ... What to fix: ...
  ▸ Q12 · Brief answer · 5/5 · ✓ Mastered
  ...
  Part 4: Voice answers
  ▾ Q16 · Voice answer · 7/10 · ! Needs work
      Transcript: "..."
      Delivery: 142 words/min · 5 filler words · 96 words · 40 s
      (comment on the numbers)
      Checklist ... Key facts ... Feedback ...

  [ Back to Home ]   [ Export JSON ]
```

- Not-mastered rows are open by default, mastered rows collapsed. Row headers are buttons with `aria-expanded` (UI-UX 6.8).
- Multiple-correct and fill-in-the-blank rows show marks and the correct options or accepted answers only.
- When the voice part was skipped the summary says "Total 44 / 50 (88%)" and the table has no voice row. A typed voice answer shows "typed, no delivery stats".
- "Export JSON" appears only for completed sessions (W10 has the same action).
- Loading right after Submit: "Marking your answers..." then "Writing your feedback...". Feedback is saved as each answer is marked (FR-12), so rows may fill in gradually; each row shows "Feedback is being written..." until its text arrives.
- Narrow rule: the three stat tiles stack.

---

## W10. History and export (FR-19)

Purpose: list past sessions and export one.

```
  History

  +-----------+-------------------+-------------------+-------------+--------+-------------------+
  | Date      | Resume            | Role              | Status      | Total  | Action            |
  +-----------+-------------------+-------------------+-------------+--------+-------------------+
  | 3 Oct     | Paras_Resume.pdf  | Backend Developer | ✓ Completed | 61 / 80| [ Export JSON ]   |
  | 1 Oct     | Paras_Resume.pdf  | Backend Developer | ✓ Completed, voice part skipped | 44 / 50 | [ Export JSON ] |
  | 28 Sep    | Paras_Resume_v2   | Backend Developer | ! In progress | -    | [ Continue ]      |
  | 26 Sep    | Paras_Resume.pdf  | Backend Developer | ✗ Abandoned | -      |                   |
  +-----------+-------------------+-------------------+-------------+--------+-------------------+
```

- Clicking a completed row opens its report (W9). Export downloads the file directly with no preview, as UTF-8 JSON with phone and email removed (FR-19).
- Only completed sessions can be exported, so In progress rows show Continue and Abandoned rows have no action.
- Empty: "No practice sessions yet. Start one from Home."
- Narrow rule: the table scrolls sideways inside its container, and the Action column stays visible.

---

## W11. Story bank (removed 10 October 2026)

The story bank was removed. The number stays so other references hold.

---

## W12. Account and deletion (FR-18)

Purpose: account details and deleting the account.

### W12a. Account

```
  Account
  User ID: paras24

  Danger zone
  +----------------------------------------------------------+
  | Delete my account                                        |
  | This permanently deletes all your data.                  |
  | [ Delete my account ... ]                                |
  +----------------------------------------------------------+
```

### W12b. Delete confirmation (own page)

```
  Delete your account

  This will permanently delete:
   • your resumes and resume points
   • key facts, questions, answers, transcripts and scores
   • your role skills check results
   • roles you added
  The shared starter roles are not affected.

  Want to keep your results? [ Go to History to export JSON ]

  Password         [______________________]
  Type DELETE      [______________________]

  [ Delete my account ]  (disabled until both are correct)   [ Cancel ]
```

- The delete button is disabled until the password is entered and the typed word matches (UI-UX 6.11). A wrong password shows an inline error and deletes nothing.
- After success the user is logged out and returned to W1 with "Your account was deleted."

---

## W13. Landing page (public, `/`)

Dark theme, see UI-UX 6.0. Layout type A (classic hero plus sections).

```
+--------------------------------------------------------------+
|        ( Abhyas.tech   [Log in] [Sign up] )   floating pill  |  (signed in: [Go to practice]; narrow: hamburger)
|                                                              |
|  Defend every line                                           |
|  of your resume.                                             |
|  One paragraph: resume -> questions -> marks -> repeat.      |
|  [Create a free account]                                     |
|  (shield) No PDF or voice recording is ever stored...        |
|  +----------------------------------------------------+      |
|  | Example question      | Marked 4 of 5 with proof   |      |
|  +----------------------------------------------------+      |
|                                                              |
|  Question lists do not know your resume   (problem)          |
|  What you get: [5 benefit cards]                             |
|                                                              |
|  Large tagline, words light up one by one on scroll          |
|                                                              |
|  How it works: [1 Upload] [2 Answer 19 questions] [3 Fix]   |
|  Your data stays yours                                       |
|  Questions: 8 FAQ items (expand in place)                    |
|  Find the weak spots before the interviewer does.            |
|  [Create a free account]   Free account. Delete anytime.     |
+--------------------------------------------------------------+
|  Open source under AGPL 3.0   [Tutorial] [GitHub] (when set)  |
+--------------------------------------------------------------+
```

## Cross-screen notes

- **Errors:** always inline under the failed action with a plain cause and a Retry button (UI-UX 7.1). Only an expired login redirects to W1, and it returns the user to where they were.
- **Loading:** a spinner plus words (for example "Reading your resume...", "Marking your answers...") with the layout kept stable.
- **AI fallback:** invisible in every screen (UI-UX 7.4). Nothing in these wireframes shows it.
- **Keyboard:** every control is reachable by Tab in reading order, with a visible focus ring. Dialogs and panels move focus in and restore it on close.

## Open items

- None blocking. Possible later additions: a read-only "key facts" view outside the report, and wireframes for the Home screen's narrow width, if phone support is ever added.
