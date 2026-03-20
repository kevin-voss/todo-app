# Agent Guidance — Todo App

## Project Context

Full-stack todo app: **React** (Vite) frontend, **Java Spring Boot** backend, **H2** in-memory database. Monorepo: `frontend/` and `backend/`.

## Commands

| Action | Command |
|--------|---------|
| Backend run | `cd backend && mvn spring-boot:run` |
| Backend test | `cd backend && mvn test` |
| Frontend dev | `cd frontend && npm run dev` |
| Frontend build | `cd frontend && npm run build` |
| Frontend test | `cd frontend && npm run test` |

## Architecture

- **Frontend**: React 18, Vite, Vitest. API client in `src/api/todos.js`. Entry: `main.jsx` → `App.jsx`.
- **Backend**: Spring Boot 3.2.5, Java 17, Spring Data JPA, H2. REST API at `/api/todos`.
- **API**: CRUD on `/api/todos` (GET list, POST create, GET/PUT/DELETE by id). Todo shape: `{ id, title, completed }`.

## Key Types

- **Todo**: `{ id: number, title: string, completed: boolean }`
- **API base URL**: `http://localhost:8080` (dev) or via `VITE_API_URL`

## Rules

- `.cursor/rules/` — Cursor rules (common + TypeScript/JS + Java).
- ECC base config: coding style, testing, git workflow, security, hooks.
- **Frontend**: JS/JSX; TypeScript rules apply via globs.
- **Backend**: Java; use `java-coding-style.md` and `java-testing.md`.
- Project-specific: `tech-stack.mdc`, `frontend-react.mdc`, `backend-spring.mdc`.

## When to Use Agents

- **planner** — New features, refactors, multi-step work
- **tdd-guide** — New features, bug fixes (tests first)
- **code-reviewer** — After implementing or changing code
- **security-reviewer** — Before commits, when handling auth or sensitive data

## Conventions

- **Tests are the spec** — Do not change test files; implement to satisfy them
- **TDD workflow** — Red → Green → Refactor
- **Conventional commits** — `feat:`, `fix:`, `refactor:`, `test:`, `chore:`
- **File list** — When a plan lists "Files to Modify", do not add or change files outside that list

## Validation

After changes, run:

- Backend: `cd backend && mvn test`
- Frontend: `cd frontend && npm run test`
