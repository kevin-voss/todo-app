---
description: "Java coding style for Spring Boot backend"
globs: ["**/*.java"]
alwaysApply: false
---
# Java Coding Style

> Extends common coding style for Java/Spring Boot.

## Immutability

Prefer immutable DTOs and records where possible:

```java
// Prefer record for DTOs
public record TodoDto(Long id, String title, boolean completed) {}
```

## Spring Boot Conventions

- Controllers: `@RestController`, `@RequestMapping("/api/todos")`
- Entities: `@Entity`, JPA annotations
- Repositories: `JpaRepository<Todo, Long>`
- Use `@Valid` for request body validation

## Error Handling

- Return appropriate HTTP status codes (404 for not found, 400 for bad request)
- Use `ResponseEntity` for explicit status control
- Validate input at controller boundary (e.g., `@NotBlank` on title)

## File Organization

- `controller/` — REST controllers
- `entity/` or `model/` — JPA entities
- `repository/` — Spring Data repositories
- Package: `com.todo`
