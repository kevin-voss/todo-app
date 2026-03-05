---
description: "Spring Boot patterns: layered architecture, JPA, REST, H2"
globs: ["**/*.java", "**/pom.xml", "**/application*.properties", "**/application*.yml"]
alwaysApply: false
---
# Spring Boot Patterns

> Project-specific rules for the todo backend. Extends common patterns.

## Layered Architecture

- **Entity** — JPA `@Entity` with `id`, `title`, `completed`
- **Repository** — `JpaRepository<Todo, Long>`
- **Service** — Business logic; delegates to repository
- **Controller** — REST `@RestController`; maps HTTP to service calls

## REST Conventions

- Base path: `/api/todos`
- GET `/` → list all (200)
- POST `/` → create (201), body `{ "title": "..." }`
- PUT `/{id}` → update (200), body `{ "title": "...", "completed": true }`
- DELETE `/{id}` → delete (204)
- 404 for non-existent id; 400 for validation (empty title)

## H2 Configuration

- Use `spring.datasource.url` with `jdbc:h2:...` (file or mem)
- `spring.jpa.hibernate.ddl-auto=update` for schema
- H2 is the sole data store; no other DB

## Validation

- Validate title: non-null, non-empty, trim whitespace
- Use `@Valid` and `@NotBlank` / `@Size` on request DTOs
