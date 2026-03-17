# Todo App

A full-stack todo application with a **React** (Vite) frontend, **Java Spring Boot** backend, and **H2** in-memory database. Supports create, read, update, delete, and toggle-completion of todos.

## Prerequisites

- **Node.js** 18+ (for frontend)
- **Java 17** (for backend)
- **Maven 3.6+** (for backend; or use `./mvnw` if Maven wrapper is present)

## Quick Start

### Backend

```bash
cd backend
mvn spring-boot:run
# Or, if using Maven wrapper:
# ./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080`. H2 console (if enabled) at `http://localhost:8080/h2-console`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173` (Vite default). Ensure the backend is running so the app can fetch todos.

## Key Commands

| Action | Command |
|--------|---------|
| Backend run | `cd backend && mvn spring-boot:run` |
| Backend test | `cd backend && mvn test` |
| Frontend dev | `cd frontend && npm run dev` |
| Frontend build | `cd frontend && npm run build` |
| Frontend test | `cd frontend && npm run test` |

## Project Structure

```
├── frontend/          # React + Vite app
│   ├── src/
│   │   ├── api/       # API client (todos.js)
│   │   ├── App.jsx
│   │   └── main.jsx
│   └── index.html
├── backend/           # Spring Boot app
│   └── src/main/java/com/todo/
│       ├── config/    # CORS, etc.
│       ├── controller/
│       ├── entity/
│       ├── repository/
│       └── service/
└── CLAUDE.md          # AI/codebase guide
```

## API

REST API at `/api/todos`:

- `GET /api/todos` — list all todos
- `GET /api/todos/{id}` — get one todo
- `POST /api/todos` — create (body: `{ "title": "..." }`)
- `PUT /api/todos/{id}` — update (body: `{ "title": "...", "completed": true }`)
- `DELETE /api/todos/{id}` — delete

Todo shape: `{ id, title, completed }`.

## Configuration

- **Frontend API URL**: Set `VITE_API_URL` (e.g. `http://localhost:8080`) if the backend is not at the default.
- **Backend**: See `backend/src/main/resources/application.properties` for H2 and JPA settings.

## Documentation

- [CLAUDE.md](CLAUDE.md) — Codebase guide for AI tools
- [AGENTS.md](AGENTS.md) — Agent workflow and conventions
- [docs/](docs/) — Architecture and API details
