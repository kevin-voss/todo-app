# Todo App Acceptance Test Summary

## Overview

Acceptance tests validate the Todo App specification (requirements.md) AC-1 through AC-10. Tests are **expected to fail** until the implementation is complete.

## Test Files

| File | ACs Covered | Description |
|------|-------------|-------------|
| `structure.test.js` | AC-1, AC-2, AC-3, AC-4, AC-9, AC-10 | Project structure, dependencies, configuration, README |
| `api.test.js` | AC-5, AC-6, AC-7 | REST API endpoints (requires backend running) |
| `frontend.test.js` | AC-8 | Frontend components and UI structure |

## AC Traceability

| AC-ID | Description | Test(s) |
|-------|-------------|---------|
| AC-1 | Separate frontend project with React JS | structure.test.js |
| AC-2 | Separate backend project with Spring Boot | structure.test.js |
| AC-3 | H2 SQL DB in backend deps and configured | structure.test.js |
| AC-4 | README describes how to start frontend/backend | structure.test.js |
| AC-5 | Create todo (POST) | api.test.js |
| AC-6 | List todos (GET) | api.test.js |
| AC-7 | Mark complete / delete (PATCH/DELETE) | api.test.js |
| AC-8 | Frontend displays todos and allows interaction | frontend.test.js |
| AC-9 | Runs without external DB (H2 embedded) | structure.test.js |
| AC-10 | Frontend/backend start independently | structure.test.js |

## Running Tests

```bash
cd crew-ai/feature-todo-app/test
npm install
npm test
```

**Note:** API tests require the backend to be running at `http://localhost:8080`. Structure and frontend tests run without any services.

## Test Framework

- **Vitest** — Fast, ESM-native test runner
- Tests use `describe`/`it`/`expect` pattern
- Each test includes `// covers AC-N` traceability comment
