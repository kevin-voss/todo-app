# Todo App — Project Instructions

## Project Overview

Minimal todo application with **React** (frontend), **Java Spring Boot** (backend), and **H2 SQL** (database). Single-user, no authentication. Features: create, list, update, delete todos; mark complete/incomplete; persistence across restarts.

## Common Commands

| Action | Command |
|--------|---------|
| Frontend dev | `cd frontend && npm run dev` |
| Frontend build | `cd frontend && npm run build` |
| Frontend test | `cd frontend && npm run test` |
| Backend run | `cd backend && ./mvnw spring-boot:run` |
| Backend test | `cd backend && ./mvnw test` |
| All tests | Run both frontend and backend test commands |

## Architecture and Conventions

- **frontend/** — React + Vite + TypeScript. Vitest for tests. Components in `src/components/` (TodoList, TodoItem), API client in `src/api/`, types in `src/types/`.
- **backend/** — Spring Boot 3.2, Java 17, Maven. JPA + H2. Package: `com.todo`. Layered: entity, repository, service, controller.
- **API base path:** `/api/todos`. REST: GET (list), POST (create), PUT (update), DELETE (delete).
- **Todo shape:** `{ id, title, completed }`. Title required, non-empty.

## Rules and Constraints

- Do NOT modify test files — they are the spec.
- Backend MUST use H2 as sole database (`spring.datasource.url` contains `h2`).
- Frontend MUST export `getTodos`, `createTodo`, `updateTodo`, `deleteTodo` from `src/api/todos`.
- Frontend MUST have `TodoList` component in `src/components/TodoList.tsx`.
- Keep feature set minimal; no auth, no advanced features.

## Key Types

- **Todo:** `{ id: number | string; title: string; completed: boolean }`
- **API:** JSON over HTTP. Create returns 201, update 200, delete 204, not-found 404, validation 400.
