# Todo App

A full-stack todo application with a **React** (Vite) frontend, **Java Spring Boot** backend, and **H2** in-memory database. Supports create, read, update, delete, and toggle-completion of todos.

## About the Author

**Kevin Voß, B.Sc.** — Senior Fullstack GenAI Software Engineer | Hannover, Germany

### Professional Summary

Senior Full-Stack and GenAI Engineer with 4.5+ years of professional experience delivering scalable cloud-native and AI-powered systems. Leveraging React, TypeScript, Java, Spring Boot, and AWS to build and maintain reliable platforms while guiding technical decisions across teams. Experienced in system design, infrastructure optimization, and implementing RAG and LLM-based solutions to streamline workflows and accelerate delivery. Skilled in facilitating collaboration between stakeholders, freelancers, and engineers—improving productivity, reducing cloud costs by up to 90%, and supporting faster feature development.

### Core Skills

- **AI & GenAI:** LLMs (OpenAI, Claude, Cursor, AI IDEs), RAG (Spring AI), Python, Prompt Engineering, Vector Databases (Qdrant), AI Agents & Sub-Agents, AI Tools/Skills/Rules
- **Frontend:** React, Next.js, TypeScript, Angular, Tailwind CSS, Web Components, Microfrontends
- **Backend:** Java, Kotlin, Spring Boot, Spring AI, Microservices, Node.js, REST APIs
- **Cloud & DevOps:** AWS, Docker, CI/CD, CDK, Terraform, IaC
- **Methods & Tools:** Agile/Scrum, TDD, System Design, Event-Driven Architecture, Git, Jest, Playwright, Mentoring

### Professional Experience

| Role | Company | Period |
|------|---------|--------|
| Software Engineer | Adesso SE (Hannover, Hybrid) | Oct 2025 – Present |
| Mid–Senior Software Engineer | Micromerce GmbH (Hamburg, Remote) | Dec 2021 – Sep 2025 |

**Adesso SE:** GS1 Rule Transformer (GenAI pipeline), WebSocket Load Balancer POC for Mercedes-Benz, RAG Validator for Siemens AG. Tech: Java, Python, Angular, TypeScript, OpenAI API, WebSockets, Docker.

**Micromerce GmbH:** Directed technical evolution of core CMS; designed microfrontend architecture (React/Next.js); developed microservices (Java, Kotlin, Spring Boot); reduced infrastructure costs by up to 90% via AWS optimization; boosted productivity with AI tools (Copilot, Cursor, Claude); mentored junior engineers and working students.

### Education

- **B.Sc. Business Informatics** — University of Hildesheim (04/2019 – 03/2023)
- **Computer Science** — Leibniz University Hannover (10/2018 – 03/2019)
- **Abitur** — Werner von Siemens Schule Hildesheim (09/2018) — Focus: C++, PHP, SQL, Robotics

### Certifications

- AWS Certified Developer – Associate (Expires: Nov 2028)
- AWS Certified Cloud Practitioner (Expires: Oct 2028)

### Additional

- **Languages:** German (Native), English (Fluent)
- **Work Preferences:** Remote (open to hybrid). Small companies and startups with hands-on mentality and fast delivery cycles.
- **Interests:** Programming, Cooking, Gym, Piano, Gaming, Concerts, Painting

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
│   │   ├── components/  # AddTodoForm, TodoList, TodoItem
│   │   ├── App.jsx
│   │   └── main.jsx
│   └── index.html
├── backend/           # Spring Boot app
│   └── src/main/java/com/todo/
│       ├── config/    # CORS (WebConfig)
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
- [docs/](docs/) — [Architecture](docs/architecture.md), [API reference](docs/api.md), [Contributing](docs/contributing.md)
