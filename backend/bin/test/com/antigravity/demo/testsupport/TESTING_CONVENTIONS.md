# Testing Conventions

## Unit Tests
- **Framework**: JUnit 5 + Mockito.
- **Extension**: Use `@ExtendWith(MockitoExtension.class)`.
- **Scope**: No Spring Context. Use direct instantiation or Mockito for dependencies.
- **Naming**: `<ClassName>Test.java`.
- **Method Naming**: `should<ExpectedBehavior>_When<Scenario>()` or similar descriptive style.
- **Pattern**: Arrange-Act-Assert.

## Integration Tests
- **Framework**: Spring Boot Test + Testcontainers.
- **Extension**: `@SpringBootTest` + `@ActiveProfiles("test")`.
- **Infrastructure**: Inherit from `PostgresTestContainerConfig` for shared database.
- **Naming**: `<Feature>IT.java` or `<ClassName>IT.java`.

## Common Utilities
- **Data Factory**: Use `ProductTestData` for consistent payloads.
- **Assertions**: Use `AssertionsEx` for common checks (e.g., `assertNearNow`).
- **Time**: Avoid exact Instant equality; use tolerance-based assertions.
