# Todo App

A minimal todo application with React (frontend), Java Spring Boot (backend), and H2 SQL (database).

---

## Profile

**Aria Abbaspour**

### Summary

Internationales Informationsmanagement Bachelor-Absolvent mit Schwerpunkt auf Informationsmanagement, Prozessoptimierung und Datenqualität. Mehrjährige praktische Erfahrung in unterschiedlichen Arbeitsumgebungen mit ausgeprägtem Qualitätsbewusstsein und Detailgenauigkeit. Versiert im Umgang mit digitalen Systemen und Dokumentationstools. Selbstständige, lösungsorientierte Arbeitsweise mit nachweislicher Erfahrung in der Remote-Arbeit.

### Experience

| Period | Role | Company | Description |
|--------|------|---------|-------------|
| Aug 2024 – Jan 2026 | Werkstudent Betriebswirtschaft & Datenanalyse | EVI Energieversorgung Hildesheim GmbH & Co. KG | Unterstützung mehrerer Fachabteilungen (Fernwärme, Energielogistik, Finanz- und Anlagenwesen, Controlling, Personalmanagement). Analyse, Aufbereitung und Pflege von Daten. Strukturierte Aufbereitung von Kennzahlen, Auswertungen und Reportings. Pflege von IT-Systemen, MS Excel. Mitwirkung bei Prozessoptimierung und abteilungsübergreifender Zusammenarbeit. |
| Okt 2022 – Dez 2023 | Werkstudent Buchhaltung & Finanzwesen | AMEOS Krankenhausgesellschaft Niedersachsen mbH | Unterstützung der Buchhaltung im operativen Tagesgeschäft. Erfassung, Prüfung und Verbuchung von Rechnungen. Kontierung und Zuordnung von Geschäftsvorfällen. Pflege von Finanz- und Buchhaltungsdaten. Monats- und Jahresabschlussvorbereitungen. Erstellung von Übersichten und Auswertungen. Zusammenarbeit mit Verwaltung und Controlling. |

### Education

| Period | Degree / Qualification | Institution |
|--------|------------------------|-------------|
| Okt 2019 – Dez 2025 | Bachelor of Arts (B.A.) Internationales Informationsmanagement | Universität Hildesheim |
| Aug 2015 – Sep 2018 | Fachhochschulreife (BWL, VWL, Wirtschaftswissenschaften) | Friedrich-List-Schule, Hildesheim |

### Skills

- **Datenanalyse & Reporting (fortgeschritten):** MS Excel (Pivot, Power Query), Power BI, SQL
- **Controlling & Finanzwesen (fortgeschritten):** SAP FI/CO, DATEV, Lexware
- **Prozessoptimierung (fortgeschritten):** Analyse & Weiterentwicklung interner Abläufe, SharePoint, Confluence
- **Projekt- & abteilungsübergreifende Zusammenarbeit (sehr gut):** MS Teams, Outlook, Trello, Jira
- **Digitale Informationssysteme & MS Office (sehr gut):** Excel, Access, SAP IS-U
- **Kundenberatung & Servicekompetenz (sehr gut):** Beratung, Vertragsabwicklung, Kommunikation
- **Weitere:** SEA & SEO

### Languages

- **Deutsch:** Muttersprache
- **Englisch:** Fließend

### Contact

- **Email:** ariaabbaspour1998@gmail.com
- **Phone:** 017666602639
- **LinkedIn:** [Profil hinterlegen]
- **Location:** 31134 Hildesheim, Deutschland

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
