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
3. **Backend** (`TodoController`) handles HTTP and delegates to `TodoService`.
4. **TodoService** applies business logic (validation, etc.) and delegates to `TodoRepository`.
5. **TodoRepository** persists to H2 via Spring Data JPA.

## Key Components

### Backend

- **Todo** (entity): `id`, `title`, `completed`
- **TodoController**: REST endpoints at `/api/todos`
- **TodoService**: Business logic layer (validation, CRUD orchestration)
- **TodoRepository**: `JpaRepository<Todo, Long>`
- **WebConfig**: CORS for frontend origins (localhost:5173, localhost:3000)

### Frontend

- **todos.js**: `getTodos`, `createTodo`, `updateTodo`, `deleteTodo`
- **App.jsx**: Main component; fetches todos on mount, orchestrates handlers
- **AddTodoForm**: Form to create a new todo
- **TodoList**: Renders list of todos; empty state when none
- **TodoItem**: Single todo row (checkbox, title, edit, delete; inline edit on Enter)
