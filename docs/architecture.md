# Architecture Overview

## Stack

| Layer | Technology |
|-------|------------|
| Frontend | React 18, Vite, Vitest |
| Backend | Java 17, Spring Boot 3.2.5 |
| Database | H2 (in-memory) |
| API | REST, JSON |

## Data Flow

1. **Frontend** (`App.jsx`) loads todos on mount via `getTodos()` from `src/api/todos.js`.
2. **API client** (`todos.js`) calls `http://localhost:8080/api/todos` (or `VITE_API_URL`).
3. **Backend** (`TodoController`) handles CRUD and delegates to `TodoRepository`.
4. **Repository** persists to H2 via Spring Data JPA.

## Key Components

- **Todo** (entity): `id`, `title`, `completed`
- **TodoController**: REST endpoints at `/api/todos`
- **TodoRepository**: `JpaRepository<Todo, Long>`
- **todos.js**: `getTodos`, `createTodo`, `updateTodo`, `deleteTodo`
