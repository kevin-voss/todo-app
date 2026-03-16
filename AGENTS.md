# Todo App — Agent Instructions

## Code Style

- Immutability: create new objects, never mutate existing ones
- Small files (<800 lines), high cohesion
- Error handling at every level; validate input at boundaries

## Architecture

- **Backend:** Spring Boot + JPA + H2. Entity `Todo`, REST controller at `/api/todos`
- **Frontend:** React + Vite. API client in `api.ts`; calls backend at `http://localhost:8080/api`
- **Tests:** Do not modify; implement production code to satisfy them

## Conventions

- TDD: write tests first, implement to pass, refactor
- Conventional commits: `feat:`, `fix:`, `refactor:`, etc.
- Backend: Maven; Frontend: npm

## Key Paths

- Backend: `backend/src/main/java/com/example/todo/`
- Frontend: `frontend/src/`
- Tests: `backend/src/test/`, `frontend/src/*.test.tsx`
