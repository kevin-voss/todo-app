# Requirements

## Functional

- **AC-1:** The repository contains a separate frontend project built with React JS.
- **AC-2:** The repository contains a separate backend project built with Java Spring Boot.
- **AC-3:** The backend project includes H2 SQL database as a dependency and has it configured (connection, datasource, or equivalent).
- **AC-4:** A README (or equivalent documentation) describes how to start the frontend and backend so a developer can run the boilerplate.
- **AC-5:** The Todo App supports creating a todo item (title/description).
- **AC-6:** The Todo App supports listing todo items.
- **AC-7:** The Todo App supports marking a todo item as complete (or deleting it).
- **AC-8:** The frontend displays todos and allows basic user interaction (create, view, complete/delete).

## Non-functional

- **AC-9:** The boilerplate runs without requiring external database setup (H2 in-memory or file-based).
- **AC-10:** The frontend and backend can be started independently via documented commands.

---

# Data Model

**Todo**
- `id`: Unique identifier (generated)
- `title`: Text (required)
- `description`: Text (optional)
- `completed`: Boolean (default: false)
- `createdAt`: Timestamp (optional)

Relationship: One-to-many not required for minimal scope. No user/auth entity.

---

# API Contracts

**Base URL:** `/api` (or equivalent)

| Method | Endpoint       | Request Body                    | Response                          |
|--------|----------------|----------------------------------|-----------------------------------|
| GET    | `/todos`       | —                                | List of todos                     |
| POST   | `/todos`       | `{ title, description? }`        | Created todo                      |
| PATCH  | `/todos/{id}`  | `{ completed }` or equivalent   | Updated todo                      |
| DELETE | `/todos/{id}`  | —                                | 204 No Content or success status  |

Response shape for a todo: `{ id, title, description?, completed, createdAt? }`.

---

# Acceptance Criteria

1. **AC-1:** Assert that a `frontend/` (or similar) directory exists with React JS project structure (e.g., `package.json` with React dependency).
2. **AC-2:** Assert that a `backend/` (or similar) directory exists with Spring Boot project structure (e.g., `pom.xml` or `build.gradle` with Spring Boot).
3. **AC-3:** Assert that H2 is in backend dependencies and `application.properties`/`application.yml` configures H2 datasource.
4. **AC-4:** Assert that README contains commands to start frontend and backend.
5. **AC-5:** Assert that a POST request to create a todo succeeds and returns the created todo.
6. **AC-6:** Assert that a GET request returns a list of todos.
7. **AC-7:** Assert that a PATCH or DELETE request updates or removes a todo.
8. **AC-8:** Assert that the frontend UI renders todos and provides controls for create, view, and complete/delete.
9. **AC-9:** Assert that the app runs without external DB (H2 in-memory or embedded file).
10. **AC-10:** Assert that frontend and backend start via documented commands without cross-dependency on startup order (beyond API availability).

---

# Edge Cases

- **AC-5:** Empty or null title: system rejects or uses default (e.g., "Untitled").
- **AC-6:** Empty list: returns `[]` or equivalent, not error.
- **AC-7:** PATCH/DELETE on non-existent id: returns 404 or equivalent.
- **AC-8:** No todos: UI shows empty state, not error.

---

# Out of Scope

- User authentication and authorization
- Multi-tenancy or user-scoped todos
- Persistence beyond H2 (e.g., PostgreSQL, MySQL)
- Advanced features: due dates, categories, search, pagination
- Production deployment configuration
- Unit/integration test implementation (spec only)
