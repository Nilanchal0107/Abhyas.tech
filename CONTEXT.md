# Abhyas.tech

A web app that helps people prepare for job interviews by turning their own resume into questions, grading their answers, and repeating weak questions until they are strong.

## Language

**Resume point**:
One project bullet, experience bullet or skill on the resume that the user must be able to defend in an interview.
_Avoid_: Claim (internal name in the PRD and code)

**Key facts**:
The true facts about a resume point, drafted by AI and used to grade answers. Shown read-only after answering.
_Avoid_: Answer key (internal name in the PRD and code)

**Interview mode**:
A practice session of 6 spoken or typed questions (3 about the user's projects, 1 **design question**, 2 behavioral), each followed by one **follow-up** the AI writes from the answer, read aloud by the browser. The follow-up answer is graded with the main one. The other mode is the **quiz** (the 19-question practice session).

**Practice session**:
One run of 19 questions worth 80 marks, grouped into four parts: multiple-correct, fill-in-the-blank, brief answers and voice answers.
_Avoid_: Session, test, quiz

**Mastered**:
A question is mastered when its mark reaches the mastery mark for its part. Mastered questions rest for one month before they can return. For a bank question, mastered belongs to the user and holds across all their resumes; for a project question it belongs to the resume.
_Avoid_: Passed, learned

**Role skills check**:
A comparison of the skills on the user's resume with the skills of their target role, showing which have evidence and which do not.
_Avoid_: Gap check (internal name in the PRD and code), skill gap analysis

**Practice bank**:
The shared database of ready-made questions, tagged by skill, reviewed by the owner and used to build every session.
_Avoid_: Question bank (internal name in the PRD), fallback bank

**Part**:
One of the four question groups in a practice session: multiple-correct, fill-in-the-blank, brief answers, voice answers.
_Avoid_: Section, round

**Session report**:
The results screen shown after a practice session is submitted: totals, marks per part, and every question with its feedback.
_Avoid_: Results page, feedback page

**Voice part skipped**:
An old practice session (before ticket 19) run without the voice part because transcription was unavailable at the start: 16 questions and 50 marks. New sessions always have 19 questions; with Whisper down, voice answers use the browser's Web Speech API.
_Avoid_: Partial session, reduced session

**Target role**:
The role the user is preparing for, chosen from the roles database at resume upload.
_Avoid_: Job, position

**Design question**:
An interview question about designing a system, a class model or a front end component, graded on five areas: requirements, API, data model, scale, trade-offs.
_Avoid_: System design round

**Role flavour**:
The kind of questions a target role gets beyond the common ones: backend (SQL, endpoints), frontend (JavaScript, CSS, React), or both for Full Stack.
_Avoid_: Track (internal name in the code)

**Live words**:
The preview of what the user is saying, shown while recording. The graded transcript is written again when the recording stops.
_Avoid_: Captions, subtitles
