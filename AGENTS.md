# Agent Guidance — Todo App

## Project Context

Full-stack todo app: React (Vite) frontend, Java Spring Boot backend, H2 database.

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
