# Abhyas.tech

A web app that helps people prepare for job interviews by turning their own resume into questions, grading their answers, and repeating weak questions until they are strong.

## Language

**Resume point**:
One project bullet, experience bullet or skill on the resume that the user must be able to defend in an interview.
_Avoid_: Claim (internal name in the PRD and code)

**Key facts**:
The true facts about a resume point, drafted by AI and used to grade answers. Shown read-only after answering.
_Avoid_: Answer key (internal name in the PRD and code)

**Practice session**:
One run of 19 questions worth 80 marks, grouped into four parts: multiple-correct, fill-in-the-blank, brief answers and voice answers.
_Avoid_: Session, test, quiz

**Mastered**:
A question is mastered when its mark reaches the mastery mark for its part. Mastered questions rest for one month before they can return.
_Avoid_: Passed, learned

**Role skills check**:
A comparison of the skills on the user's resume with the skills of their target role, showing which have evidence and which do not.
_Avoid_: Gap check (internal name in the PRD and code), skill gap analysis

**Story bank**:
The user's saved STAR stories, reusable when practising behavioral questions.
_Avoid_: Story library, answers vault

**Practice bank**:
The shared database of ready-made questions, tagged by skill, used when AI is unavailable and fed by validated AI-generated questions.
_Avoid_: Question bank (internal name in the PRD), fallback bank

**Part**:
One of the four question groups in a practice session: multiple-correct, fill-in-the-blank, brief answers, voice answers.
_Avoid_: Section, round

**Session report**:
The results screen shown after a practice session is submitted: totals, marks per part, and every question with its feedback.
_Avoid_: Results page, feedback page

**Voice part skipped**:
A practice session run without the voice part because transcription was unavailable at the start: 16 questions and 50 marks.
_Avoid_: Partial session, reduced session

**Target role**:
The role the user is preparing for, chosen from the roles database at resume upload.
_Avoid_: Job, position
