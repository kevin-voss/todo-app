---
description: "Java testing for Spring Boot backend"
globs: ["**/*.java"]
alwaysApply: false
---
# Java Testing

> Extends common testing for JUnit 5 and Spring Boot Test.

## Test Structure

- `@SpringBootTest` for integration tests
- `@AutoConfigureMockMvc` for controller tests
- `@ActiveProfiles("test")` for test H2 config

## Conventions

- Test class suffix: `*Test`
- Use `MockMvc` for REST API tests
- Use `@BeforeEach` for setup (e.g., clean state)
- Assert with JUnit 5 `Assertions.*`

## H2 Test Profile

Ensure `application-test.properties` or `application-test.yml` configures H2 for tests (in-memory, auto DDL).
