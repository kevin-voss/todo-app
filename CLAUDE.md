# Todo App — Claude Project Instructions

## 1. Project Overview

Minimal CRUD todo application: React frontend (Vite + TypeScript) calling a Spring Boot REST API backed by H2 in-memory database. Users can list, create, toggle completion, and delete todos. No auth, no persistence beyond process lifetime.

**Stack:** React 18, Vite, Vitest, Spring Boot 3.2, Java 17, Spring Data JPA, H2.

## 2. Common Commands

| Action | Command |
|--------|---------|
| Backend run | `cd backend && mvn spring-boot:run` |
| Backend test | `cd backend && mvn test` |
| Frontend dev | `cd frontend && npm run dev` |
| Frontend test | `cd frontend && npm run test` |
| Frontend build | `cd frontend && npm run build` |

**Verification:** Run `mvn test` in backend and `npm run test` in frontend; all tests must pass.

## 3. Architecture and Conventions

- **Backend:** `backend/src/main/java/com/example/todo/` — `Todo` entity, `TodoRepository`, `TodoController`, `TodoApplication`
- **Frontend:** `frontend/src/` — `App.tsx`, `api.ts`, `types.ts`; API base `http://localhost:8080/api`
- **API:** GET/POST `/api/todos`, PATCH/DELETE `/api/todos/{id}`
- **Conventions:** Immutability, small focused files, TDD (tests first), no mutation of existing objects

## 4. Rules and Constraints

- Do NOT modify test files — they define expected behavior
- Do NOT add auth, filtering, sorting, or external persistence
- Validate input at boundaries; reject empty title with 400
- Return 404 for PATCH/DELETE on non-existent id
- CORS must be enabled for frontend (port 5173) to call backend (port 8080)

## 5. Key Types

- **Todo:** `id` (number), `title` (string), `completed` (boolean)
- Backend: `com.example.todo.Todo` (JPA entity)
- Frontend: `Todo` in `src/types.ts`
