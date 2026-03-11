# Todo App

A minimal todo application with React (frontend), Java Spring Boot (backend), and H2 SQL (database).

---

## Profile

<!-- Replace the sections below with your resume data from Lebenslauf.pdf -->

### Summary

[Your professional summary — 2–3 sentences about your background, expertise, and career focus.]

### Experience

| Period | Role | Company | Description |
|--------|------|---------|-------------|
| [Dates] | [Job Title] | [Company] | [Key responsibilities and achievements] |
| [Dates] | [Job Title] | [Company] | [Key responsibilities and achievements] |

### Education

| Period | Degree / Qualification | Institution |
|--------|------------------------|-------------|
| [Dates] | [Degree] | [University/School] |

### Skills

- **Technical:** [List your technical skills, e.g. Java, React, TypeScript, Spring Boot, SQL]
- **Tools:** [List tools and platforms]
- **Languages:** [Spoken/written languages and proficiency]

### Contact

- **Email:** [your.email@example.com]
- **LinkedIn:** [linkedin.com/in/yourprofile]
- **Location:** [City, Country]

---

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

- **frontend/** — React + Vite + TypeScript. Vitest for tests. Components in `src/components/`, API client in `src/api/`.
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
