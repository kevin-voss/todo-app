# Todo App — Claude Codebase Guide

## Overview

Full-stack todo application: **React** (Vite) frontend, **Java Spring Boot** backend, **H2** in-memory database. Monorepo layout: `frontend/` and `backend/`.

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

## Rules

- `.cursor/rules/` — Cursor rules (common + TypeScript/JS + Java).
- ECC base config: coding style, testing, git workflow, security, hooks.
- **Frontend**: JS/JSX; TypeScript rules apply via globs.
- **Backend**: Java; use `java-coding-style.md` and `java-testing.md`.

## Key Types

- **Todo**: `{ id: number, title: string, completed: boolean }`
- **API base URL**: `http://localhost:8080` (dev) or via `VITE_API_URL`
