# Resume Defense Trainer

A web app that turns a user's resume into interview questions, grades the answers, and repeats weak questions until they are mastered. Stack: React (Vite, JavaScript), Spring Boot (Java 21, Maven), MySQL 8. Licensed AGPL-3.0.

## Source of truth

Read the relevant doc before changing anything. If code and a doc disagree, tell the user instead of silently picking one.

| File | What it holds |
|---|---|
| `prd.md` | requirements (FR numbers), decisions, risks, change log |
| `trd.md` | architecture, pipelines, build steps with manual checks |
| `api-contracts.md` | exact request and response of every endpoint, error codes |
| `database-schema.md` | tables, V1 SQL, starter roles data |
| `UI-UX.md`, `wireframes.md` | screens, states, copy, layouts |
| `CONTEXT.md` | glossary. Use these terms in code comments, UI copy and docs |

When a decision changes, update the affected docs and add a change-log entry in `prd.md`.

## Git rules

- **Never run `git add`, `git commit` or `git push`** (or anything that does the same, such as `git commit -a`, `git stash`, `git rebase`, `git reset` or `git checkout --`). The user runs these.
- Read-only git commands (`git status`, `git diff`, `git log`) are fine.
- **After every finished task, suggest the commands** in a code block for the user to run, with the specific files you changed (never `git add .`), and a short commit message in the form `type(scope): summary` (for example `feat(auth): add signup and login`). Do not suggest `git push` unless the user asks.
  ```
  git add backend/src/main/java/.../auth api-contracts.md
  git commit -m "feat(auth): add signup and login"
  ```
- If the folder is not a git repository yet, suggest `git init` first and do not run it.

## Working rules

- **Manual check after every build step.** At the end of each build step (Login, Resume parser, Questions, Feedback, Story bank, Export JSON, see `trd.md` Section 13), give the user that step's manual checklist and wait for their confirmation before starting the next step.
- Build in the order of `prd.md` Section 6. Do not start a step before the previous one is confirmed.
- Never put secrets in the repo. API keys, the JWT secret and the database password come from environment variables or the gitignored `application-local.yml`.
- Never log resume text, answers, transcripts, prompts or AI replies (`trd.md` 6.5).
- Every query on user data must be scoped to the signed-in user. Another user's resource returns `404`.
- Do not store the original PDF or any audio.
- Say plainly what you did not run or verify. The V1 SQL has not been run against a real MySQL yet.

## Agent skills

### Issue tracker

Issues and specs live as local markdown files under `.scratch/<feature>/` (no remote). See `docs/agents/issue-tracker.md`.

### Triage labels

Default five-role vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: one `CONTEXT.md` and `docs/adr/` at the repo root. See `docs/agents/domain.md`.
