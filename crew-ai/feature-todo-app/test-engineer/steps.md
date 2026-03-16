# Test Engineer — Step Details

## Step 1: test
- **Status:** completed
- **Duration:** 50s
- **Output:**







Creating acceptance tests for all ACs. Setting up the test structure:















Here’s what was added:

## Summary

14 acceptance tests were added across 3 files, covering AC-1 through AC-10 from `requirements.md`. Each test includes a `// covers AC-N` comment.

### Test Files

| File | ACs | Tests |
|------|-----|-------|
| `structure.test.js` | AC-1, AC-2, AC-3, AC-4, AC-9, AC-10 | 6 tests |
| `api.test.js` | AC-5, AC-6, AC-7 | 8 tests |
| `frontend.test.js` | AC-8 | 5 tests |

### Coverage

- **AC-1:** Frontend project with React (`frontend/`, `package.json`, React dependency)
- **AC-2:** Backend project with Spring Boot (`backend/`, `pom.xml` or `build.gradle`)
- **AC-3:** H2 in backend dependencies and datasource configuration
- **AC-4:** README with commands to start frontend and backend
- **AC-5:** POST `/api/todos` creates a todo and returns it (including empty-title handling)
- **AC-6:** GET `/api/todos` returns a list (including empty list)
- **AC-7:** PATCH and DELETE for update/remove, plus 404 for non-existent IDs
- **AC-8:** TodoList, TodoForm, TodoItem components with create/view/complete/delete and empty state
- **AC-9:** H2 embedded configuration (no external DB)
- **AC-10:** README documents independent startup for frontend and backend

### Artifacts

- `crew-ai/feature-todo-app/test/package.json` – Vitest setup
- `crew-ai/feature-todo-app/test/vitest.config.js` – Vitest config
- `crew-ai/feature-todo-app/test/helpers.js` – Path helpers
- `crew-ai/feature-todo-app/test/structure.test.js`
- `crew-ai/feature-todo-app/test/api.test.js`
- `crew-ai/feature-todo-app/test/frontend.test.js`
- `crew-ai/feature-todo-app/test/test-summary.md` – AC traceability
- `crew-ai/feature-todo-app/test-engineer/role-output.md` – Handoff

Tests are expected to fail until the implementation is in place. API tests assume the backend is running at `http://localhost:8080`.
