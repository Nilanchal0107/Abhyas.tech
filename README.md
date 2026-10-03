# Abhyas.tech

**Defend your resume.** Turns your resume into interview questions, grades your answers, and repeats weak questions until you have mastered them.
Stack: React (Vite), Spring Boot (Java 21, Maven), MySQL 8.

**Meant for localhost use only.** The backend binds to `127.0.0.1`. Do not expose it to a network without a security review.

## Setup (Windows)

Details and reasons are in `trd.md` Section 5.

1. Install MySQL 8.0.16 or newer. Check `SELECT VERSION();`.
2. Create the database and user:
   ```sql
   CREATE DATABASE resume_trainer CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   CREATE USER 'trainer'@'localhost' IDENTIFIED BY '<password>';
   GRANT ALL PRIVILEGES ON resume_trainer.* TO 'trainer'@'localhost';
   ```
   If the `mysql` command is not on your PATH, use MySQL Shell instead: `\connect root@localhost`, then `\sql`, then paste the statements.
3. Set the environment variables listed in `.env.example` (`DB_URL` must contain `serverTimezone=UTC`, for example
   `jdbc:mysql://localhost:3306/resume_trainer?serverTimezone=UTC`). `DB_*` and `JWT_SECRET` (at least 32 bytes) are required.
   `GROQ_API_KEY` and `GOOGLE_AI_KEY` are optional: without them the app still works, with ready-made questions and keyword grading,
   but voice answers cannot be transcribed (they can be typed instead).
4. Backend: `cd backend`, then `mvn spring-boot:run`. Flyway creates the tables on first start.
5. Frontend: `cd frontend`, `npm install`, `npm run dev`, then open http://localhost:5173.

Sign up on the first screen. Then upload a resume, review it, practice, read the report, keep STAR stories in the Story bank and
export a finished session from History as a JSON file (phone and email are removed from it).

## Tests

- Backend: `cd backend`, `mvn test`. The schema test uses Testcontainers and needs Docker; it is skipped when Docker is missing.
- Frontend: `cd frontend`, `npm test`.

## License

AGPL-3.0, see `LICENSE`. Copyright (C) 2026 Paras Panchal. The resume parser in `frontend/src/lib/openresume/` is copied from
[OpenResume](https://github.com/xitanggg/open-resume), which is also AGPL-3.0; its license stays with the copied code
(`frontend/src/lib/openresume/LICENSE`). Small changes are marked "Changed from OpenResume" in the files.
This is not legal advice: check the terms again before you share or host the app.
