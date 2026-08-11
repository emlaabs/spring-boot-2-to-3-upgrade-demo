# Spring Boot 2 → Spring Boot 3 Migration Demo

This project demonstrates the process of upgrading a Spring Boot 2.x application to Spring Boot 3.x.  
It serves as a practical reference for understanding breaking changes, required code updates, dependency shifts, and Jakarta EE namespace migration.

The goal is to show clean, step‑by‑step modernization of a real application while documenting the reasoning behind each change.

---

## Purpose of This Project

- Explore the full upgrade path from Spring Boot 2 to 3
- Document breaking changes and how to resolve them
- Modernize the codebase to align with Spring Boot 3 best practices
- Provide a clear example for developers facing similar upgrades
- Build a portfolio piece demonstrating backend engineering skills

---

## Key Migration Areas

### 1. Jakarta EE Namespace Changes
Spring Boot 3 requires Jakarta EE 9+, meaning imports change from:
- `javax.*` → `jakarta.*`

This affects:
- Controllers
- Filters
- Validation
- JPA entities
- Servlet components

### 2. Spring Security Updates
Spring Security 6 introduces:
- Lambda‑based configuration
- Removal of `WebSecurityConfigurerAdapter`
- Updated authorization rules
- New password encoder defaults

### 3. Dependency Upgrades
Examples include:
- Spring Framework 6
- Hibernate 6
- Updated validation libraries
- Updated test libraries (JUnit, Mockito, etc.)

### 4. Java Version Requirements
Spring Boot 3 requires:
- **Java 17 or higher**

### 5. Deprecated APIs Removed
During the migration, several APIs from Spring Boot 2 are no longer available.  
This project documents each removal and the replacement approach.

---

## Project Structure

* /src
* /main
* /java
* /resources
* /test
* pom.xml
* README.md
---

## How to Run the Project

### Requirements
- Java 17+
- Maven or Gradle
- Spring Boot 3.x

### Commands

**Maven**
mvn spring-boot:run


---

## Spring Boot 3 Migration and Modernization 

### Completed Phases
This project demonstrates a full upgrade and modernization of a Spring Boot application from legacy versions (Spring Boot 1.x and 2.x) to Spring Boot 3, including Jakarta EE 9, Spring Security 6, Hibernate 6, and Micrometer Observability.
The following phases document the work completed during the migration.

#### Phase 1 — Upgrade to Spring Boot 3 and Jakarta EE 9
Migrated all imports from javax.* to jakarta.*.

Updated Maven dependencies to Spring Boot 3.x.

Resolved compilation issues caused by Jakarta namespace changes.

Verified successful application startup under Spring Boot 3.

Result:  
The application now runs on the modern Spring Boot 3 platform with full Jakarta EE 9 compatibility.

#### Phase 2 — Actuator Modernization and Micrometer Tracing
Updated Actuator endpoints for Spring Boot 3.

Added Micrometer Tracing with OpenTelemetry bridge.

Added Zipkin exporter for distributed tracing.

Enabled automatic instrumentation for HTTP, JDBC, and Spring MVC.

Result:  
The application emits modern metrics and distributed traces compatible with Prometheus, Grafana, Zipkin, Jaeger, and OpenTelemetry Collector.

#### Phase 3 — Test Suite Modernization
Updated tests to use jakarta.* imports.

Fixed MockMvc tests impacted by Spring Security 6 changes.

Added @AutoConfigureMockMvc(addFilters = false) to disable security filters in controller tests.

Ensured repository tests work correctly with Hibernate 6.

Result:  
All tests compiled and run successfully under Spring Boot 3’s updated testing infrastructure.

#### Phase 4 — Spring Security 6 Migration
Removed the deprecated WebSecurityConfigurerAdapter.

Introduced SecurityFilterChain bean configuration.

Replaced antMatchers() with requestMatchers().

Updated security DSL to the new lambda-based API.

Result:  
Security configuration is fully aligned with Spring Security 6 and modern best practices.

#### Phase 5 — Hibernate 6 Compatibility and Deep Testing
Verified entity mappings under Hibernate 6.

Added deeper repository tests covering JPQL strictness, ordering, flush behavior, dirty checking, delete behavior, and lazy loading.

Added domain relationships (Author ↔ Book).

Added cascading, orphan removal, and lazy loading tests.

Result:  
The persistence layer is validated against Hibernate 6’s stricter behavior and modern JPA rules.

#### Phase 6 — Modern Observability (Micrometer and OpenTelemetry)
Adopted Micrometer Observability (metrics, traces, logs).

Enabled automatic instrumentation for HTTP, JDBC, Hibernate, and Spring MVC.

Explained differences between Spring Boot Admin and Spring Boot 3 Observability.

Ensured unified telemetry signals across metrics, logs, and traces.

Result:  
The application is cloud-ready with modern observability pipelines.

#### Phase 7 — Modern API Design (DTOs, Validation, Error Handling)
Introduced request DTOs (CreateBookRequest).

Introduced response DTOs (BookResponse).

Added Jakarta Validation annotations.

Added a global exception handler using @ControllerAdvice for consistent JSON error responses.

Updated controllers to use DTOs instead of entities.

Result:  
The API is stable, safe, versionable, and aligned with modern REST best practices.

#### Phase 8 — Service Layer Architecture
Introduced a dedicated service layer (BookService).

Added a clean implementation (BookServiceImpl).

Moved business logic out of controllers.

Added proper transaction boundaries using @Transactional.

Centralized entity-to-DTO mapping logic.

Improved testability and separation of concerns.

Result:  
The application now follows a clean, maintainable, enterprise-grade architecture.

### Remaining Phases

The following phases outline the remaining work planned for this Spring Boot 3 modernization project. These steps will continue building on the foundation established in the completed phases.

#### Phase 9 — Authentication and Authorization (Users, Roles, JWT)
Introduce a complete security model using Spring Security 6:

User and Role entities

Password hashing with BCrypt

Login and registration endpoints

Stateless authentication using JWT

Role‑based authorization for protected endpoints

Security filter chain updates for token validation

Goal:  
Provide a modern, secure authentication system aligned with Spring Security 6 best practices.

#### Phase 10 — Advanced Hibernate and Domain Modeling
Enhance the domain model with more complex persistence features:

One‑to‑one and many‑to‑many relationships

Join tables and composite keys

Fetch strategies and fetch joins

DTO projections and custom queries

N+1 query detection and performance tuning

Goal:  
Build a robust, efficient domain model suitable for real‑world applications.

#### Phase 11 — Deployment and Cloud Readiness
Prepare the application for deployment in modern environments:

Dockerfile and containerization

Externalized configuration

Health and readiness probes

Logging and metrics integration for cloud platforms

Optional Kubernetes manifests

Goal:  
Make the application portable, observable, and ready for cloud or containerized deployment.

#### Phase 12 — Optional Enhancements
Depending on the project needs, additional improvements may include:

Pagination and sorting for API endpoints

Caching with Spring Cache and Redis

File upload/download support

Async processing or reactive endpoints

Testcontainers for integration testing

Structured JSON logging

Goal:  
Extend the application with features commonly required in production systems.


---

## References

- Spring Boot 3 Migration Guide
- Spring Framework 6 Documentation
- Jakarta EE 9+ Specification
- Hibernate 6 Migration Notes

---

## About the Author

**Eric Laabs**  
Backend Engineer — Java, Spring Boot, Cloud  
GitHub: https://github.com/emlaabs
