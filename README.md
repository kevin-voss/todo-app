Hello # Todo App

A minimal todo application with React (frontend), Java Spring Boot (backend), and H2 SQL (database).

---

## Profile

**Aria Abbaspour**

### Summary

Internationales Informationsmanagement Bachelor-Absolvent mit Schwerpunkt auf Informationsmanagement, Prozessoptimierung und Datenqualität. Mehrjährige praktische Erfahrung in unterschiedlichen Arbeitsumgebungen mit ausgeprägtem Qualitätsbewusstsein und Detailgenauigkeit. Versiert im Umgang mit digitalen Systemen und Dokumentationstools. Selbstständige, lösungsorientierte Arbeitsweise mit nachweislicher Erfahrung in der Remote-Arbeit.

### Experience

#### Aug 2024 – Jan 2026 · Werkstudent Betriebswirtschaft & Datenanalyse  
**EVI Energieversorgung Hildesheim GmbH & Co. KG · Hildesheim**

- Unterstützung mehrerer Fachabteilungen (Fernwärme, Energielogistik, Finanz- und Anlagenwesen, Controlling, Personalmanagement) im operativen Tagesgeschäft
- Analyse, Aufbereitung und Pflege von Daten zur Unterstützung betriebswirtschaftlicher, energiewirtschaftlicher und personalbezogener Prozesse
- Strukturierte Aufbereitung von Kennzahlen sowie Erstellung von Auswertungen, Übersichten und Reportings
- Pflege und Verwaltung von Informationen in internen IT-Systemen sowie sicherer Umgang mit MS Excel
- Unterstützung bei abrechnungs-, anlagen- und personalbezogenen Tätigkeiten
- Mitwirkung bei der Analyse, Optimierung und Weiterentwicklung interner Prozesse und Arbeitsabläufe
- Selbstständige Übernahme von Teilaufgaben nach entsprechender Einarbeitung
- Abteilungsübergreifende Zusammenarbeit mit verschiedenen Ansprechpartnern

#### Okt 2022 – Dez 2023 · Werkstudent Buchhaltung & Finanzwesen  
**AMEOS Krankenhausgesellschaft Niedersachsen mbH · Hildesheim**

- Unterstützung der Buchhaltung im operativen Tagesgeschäft
- Mitarbeit bei der Erfassung, Prüfung und Verbuchung von Rechnungen
- Unterstützung bei der Kontierung und Zuordnung von Geschäftsvorfällen
- Pflege und Aktualisierung von Finanz- und Buchhaltungsdaten in internen Systemen
- Unterstützung bei Monats- und Jahresabschlussvorbereitungen
- Erstellung und Aufbereitung von Übersichten, Listen und Auswertungen
- Bearbeitung administrativer Aufgaben im Finanzwesen
- Zusammenarbeit mit internen Ansprechpartnern aus Verwaltung und Controlling

### Education

#### Okt 2019 – Dez 2025 · Bachelor of Arts (B.A.) Internationales Informationsmanagement  
**Universität Hildesheim · Hildesheim**

- Interdisziplinäres Studium mit Schwerpunkt auf Betriebswirtschaft, Informations- und Datenmanagement
- Vermittlung von Datenanalyse, Controlling und Prozessoptimierung
- Erfahrung im Umgang mit digitalen Informationssystemen und Tools
- Schulung in interkultureller Kommunikation und internationaler Zusammenarbeit
- Vorbereitung auf die Unterstützung informationsbasierter Entscheidungsprozesse in Unternehmen

#### Aug 2015 – Sep 2018 · Fachhochschulreife (BWL, VWL, Wirtschaftswissenschaften)  
**Friedrich-List-Schule · Hildesheim**

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
