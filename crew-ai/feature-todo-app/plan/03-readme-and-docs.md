# Step 03 — README and Documentation

## Context

This step builds on **01-backend-setup** and **02-frontend-todo-ui**. Both projects exist and run. Documentation is required so developers can start the boilerplate (AC-4, AC-10).

## Tasks

### Docs

1. Create or update `README.md` at repository root.
2. Document:
   - Project overview (Todo App with React + Spring Boot + H2).
   - Prerequisites (JDK 17+, Node 18+, Maven).
   - How to start the backend: `cd backend && mvn spring-boot:run`.
   - How to start the frontend: `cd frontend && npm install && npm run dev`.
   - That backend runs on port 8080, frontend on 5173 (Vite default).
   - That frontend and backend can be started independently; backend must be running for API calls.
   - Optional: H2 console URL if enabled (`http://localhost:8080/h2-console`).

## Acceptance Criteria

- [ ] **AC-4:** README contains commands to start frontend and backend.
- [ ] **AC-10:** Documentation states that frontend and backend start via documented commands without cross-dependency on startup order (beyond API availability).

## Commands to Run

```bash
# No build commands; verify README exists and is readable
cat README.md | head -50
```

## Files to Modify

| File | Action | Purpose |
|------|--------|---------|
| `README.md` | create or update | Run instructions for frontend and backend |

## Architecture / Diagrams

None required for documentation step.

## Technical Decisions

| Decision | Rationale |
|----------|-----------|
| Root README | Single entry point for developers |
| Explicit port numbers | Avoid confusion (8080, 5173) |
| Prerequisites listed | Reduces setup friction |

## Code Examples / Files

### README.md (structure)

```markdown
# Todo App Boilerplate

React JS frontend + Java Spring Boot backend + H2 database.

## Prerequisites

- JDK 17+ (or 11+)
- Node.js 18+
- Maven 3.6+

## Running the App

### Backend

cd backend && mvn spring-boot:run

Backend runs at http://localhost:8080

### Frontend

cd frontend && npm install && npm run dev

Frontend runs at http://localhost:5173

### Notes

- Start backend first for full functionality; frontend can run independently.
- H2 console (if enabled): http://localhost:8080/h2-console
```

## Docs Updates

This step is the docs update.

## Commit Message

```
docs: add README with run instructions for frontend and backend
```
