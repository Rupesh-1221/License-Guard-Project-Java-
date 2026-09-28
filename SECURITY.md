# Security & Data Protection

This document outlines the security architecture and data protection mechanisms implemented in **LicenseGuard**.

## 1. Security Architecture
LicenseGuard uses a strict 3-tier architecture:
- **JavaFX Client**: Operates without direct database access. It only communicates via HTTP/JSON.
- **Spring Boot REST API**: The authoritative source of all data validation, access logic, and database persistence.
- **MySQL Database**: Heavily protected by Spring Data JPA and strict ORM configurations.

*Note: As an internal enterprise tool, this project does not currently integrate Spring Security, JWT, OAuth, or complex authentication frameworks, as they were not required by the core project scope. Network-level security is assumed.*

## 2. Password Handling
- **Database Storage**: Currently stored in plain text per original project requirements. (See Future Improvements).
- **API Exposure Prevention**: 
  - The `User.java` entity uses `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` on the password field to mathematically prevent Jackson from ever serializing the password out to an HTTP response.
  - The `UserResponse.java` DTO explicitly omits the password field.

## 3. API Input Validation
- **Jakarta Validation**: Endpoints use robust `@Valid` checking.
  - `@NotBlank`, `@Size`, `@Email` ensure invalid strings or excessively large payloads are rejected before hitting the database.
- **Business Rule Enforcement**: The service layer ensures dates are chronological (`purchaseDate <= startDate <= expiryDate`) and prevents accidental negative seat assignments.

## 4. JPA/Hibernate Database Protection
- **ORM Strictness**: The application uses `spring.jpa.hibernate.ddl-auto=validate`. Hibernate is forbidden from executing `CREATE`, `DROP`, or `ALTER` statements against the production schema.
- **SQL Injection Prevention**: Spring Data JPA utilizes parameterized queries (PreparedStatement) automatically, rendering the application immune to traditional SQL injection attacks.

## 5. Transaction Protection
- **`@Transactional`**: Operations that span multiple entities (e.g., creating a license assignment and simultaneously decrementing available seats) are locked within a single transaction. If an error occurs, the entire transaction rolls back, preventing orphaned records or mathematically invalid seat counts.

## 6. Error Handling
- **Structured Error Responses**: `GlobalExceptionHandler.java` intercepts all backend errors.
- **Data Leak Prevention**: Internal server exceptions (`Exception.class`) have been patched to return a generic `"An unexpected server error occurred. Please contact support."` message, preventing the accidental leakage of SQL statements, stack traces, or internal server states to the client.

## 7. Secret & Configuration Management
- No production database passwords, API keys, or JWT secrets are hardcoded in the source code or tracked by Git.
- `application.properties` uses placeholder credentials (`YOUR_MYSQL_PASSWORD`), requiring the deployer to provide local variables or environment overrides.

## 8. GitHub Security Practices
- `.gitignore` rigorously excludes `.env`, `*.key`, IDE settings (`.idea`, `.vscode`), and built binaries (`target/`).

## 9. Current Security Limitations & Future Improvements
While robust against data-corruption and SQL injection, the project currently lacks:
- **Password Hashing**: Passwords are saved raw. Future iterations should implement `BCryptPasswordEncoder`.
- **Stateless Authentication**: Implement Spring Security + JWT for session tracking.
- **Role-Based Access Control (RBAC)**: Enforce route protection based on the `role` field.
