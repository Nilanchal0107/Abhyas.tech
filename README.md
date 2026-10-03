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
   `jdbc:mysql://localhost:3306/resume_trainer?serverTimezone=UTC`). Only the three `DB_*` values are needed for the first step.
4. Backend: `cd backend`, then `mvn spring-boot:run`. Flyway creates the tables on first start.
5. Frontend: `cd frontend`, `npm install`, `npm run dev`, then open http://localhost:5173.

The page shell loads and says "Not signed in" until login is built.

## Tests

- Backend: `cd backend`, `mvn test`. The schema test uses Testcontainers and needs Docker; it is skipped when Docker is missing.
- Frontend: `cd frontend`, `npm test`.

## License

AGPL-3.0, see `LICENSE`. Copyright (C) 2026 Paras Panchal. The resume parser added in a later step is copied from
[OpenResume](https://github.com/xitanggg/open-resume), which is also AGPL-3.0; its notice stays with the copied code.
This is not legal advice: check the terms again before you share or host the app.
