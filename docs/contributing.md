# Contributing

## Conventions

- **Tests are the spec** — Do not change test files; implement production code to satisfy them.
- **TDD workflow** — Red → Green → Refactor.
- **Conventional commits** — Use `feat:`, `fix:`, `refactor:`, `test:`, `chore:` prefixes.

## Validation

After changes, run:

- **Backend:** `cd backend && mvn test`
- **Frontend:** `cd frontend && npm run test`

## AI / Agent Workflow

See [AGENTS.md](../AGENTS.md) for when to use planner, tdd-guide, code-reviewer, and security-reviewer agents.
