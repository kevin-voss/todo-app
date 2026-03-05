# Todo App — Agent Instructions

## Tech Stack

- **Frontend:** React 18, Vite 5, TypeScript, Vitest
- **Backend:** Java 17, Spring Boot 3.2, Maven, JPA, H2
- **Database:** H2 (in-memory/file)

## Architecture

- `frontend/` — React SPA; `src/components/`, `src/api/`
- `backend/` — Spring Boot; `com.todo` package; entity, repository, service, controller

## Conventions

- REST API at `/api/todos`; Todo: `{ id, title, completed }`
- Do not modify test files
- Use H2 as sole database
- Keep features minimal

## Commands

- Frontend: `npm run dev`, `npm run test`
- Backend: `./mvnw spring-boot:run`, `./mvnw test`
