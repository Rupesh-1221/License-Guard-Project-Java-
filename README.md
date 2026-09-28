# LicenseGuard

LicenseGuard is an enterprise-grade Software License Management System built to help organizations track, manage, and optimize their software assets. It provides a robust backend architecture for enforcing complex business rules regarding license seat assignments, renewals, and expirations, seamlessly integrated with a responsive, non-blocking JavaFX desktop client.

## Project Objectives
- Provide a centralized repository for tracking software licenses across departments.
- Prevent license over-allocation through strict, transactional seat management.
- Automate the tracking of expiring licenses and renewal histories.
- Ensure high data integrity via server-side business logic and validation.

## Main Features
- **Department & User Management**: Organize users by departments for clear visibility.
- **Vendor & Software Catalog**: Track software titles and their respective vendors.
- **License Lifecycle Management**: Manage subscriptions, perpetual licenses, seat capacities, and costs.
- **Seat Assignment**: Assign licenses to users with real-time seat availability updates.
- **Renewal Tracking**: Renew expiring licenses while preserving the historical chain of renewals.
- **Dashboard & Reporting**: View real-time utilization metrics and expiring license alerts.

## Technology Stack
- **Backend**: Java 21, Spring Boot 4.1.1, Spring Web, Spring Data JPA
- **Database**: MySQL 8.0, Hibernate ORM
- **Frontend**: JavaFX 21, FXML
- **Build Tool**: Maven

## System Architecture
LicenseGuard follows a strict multi-tier architecture to ensure security and maintainability.

### Overall Architecture
`JavaFX Frontend` ➔ `HTTP/JSON REST API` ➔ `Spring Boot Controller` ➔ `Service (Business Logic)` ➔ `Spring Data JPA` ➔ `Hibernate` ➔ `MySQL Database`

### Frontend Architecture
The JavaFX client operates entirely isolated from the database. It uses standard `java.net.http.HttpClient` to make asynchronous REST API calls. Using `CompletableFuture` and `Platform.runLater()`, the UI remains highly responsive and completely non-blocking, fetching JSON responses and mapping them to Java objects for display in Tables and Dashboards.

### Backend Architecture
The Spring Boot backend is the authoritative source of truth. It strictly separates concerns:
- **Controllers**: Handle HTTP routing and JSON serialization/deserialization.
- **Services**: Execute complex business rules, ensure data consistency, and manage `@Transactional` boundaries.
- **Repositories**: Interface with Hibernate to perform CRUD operations via Spring Data JPA.

### Database Overview
The relational database consists of 7 tightly coupled tables (`departments`, `users`, `vendors`, `software`, `licenses`, `license_assignments`, `renewals`) enforcing referential integrity with foreign keys. 

### Hibernate/JPA Overview
Hibernate acts as the ORM layer mapping Java Entity classes to MySQL tables. It uses `FetchType.LAZY` for performance optimization and `@Transactional` for ACID compliance. The schema generation is strictly set to `validate` to prevent accidental database modifications in production.

### Business Logic Overview
The Service layer enforces rules such as:
- Preventing duplicate license assignments.
- Ensuring `available_seats` never drops below zero.
- Validating chronological dates (e.g., Renewal date > Expiry date).
- Blocking the deletion of entities (like Users or Licenses) if they have dependent historical records.

### Security Overview
- **Data Protection**: Passwords are mathematically excluded from JSON serialization (`WRITE_ONLY`).
- **SQL Injection**: Prevented globally by Hibernate's use of Parameterized Queries.
- **Error Handling**: Stack traces are caught by a `@ControllerAdvice` Global Exception Handler and sanitized into user-friendly messages.

## Testing
- **Unit Tests**: 25 comprehensive JUnit 5 / Mockito tests validating every business logic constraint and Service layer method.
- **E2E Integration Tests**: Full-suite automated REST API testing validating the complete data lifecycle.

## Project Structure
```text
LicenseGuard/
├── src/
│   ├── main/
│   │   ├── java/com/licenseguard/
│   │   │   ├── controller/      # REST API Endpoints
│   │   │   ├── service/         # Business Logic
│   │   │   ├── repository/      # Spring Data JPA
│   │   │   ├── entity/          # Hibernate Models
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   └── exception/       # Error Handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit Tests
├── frontend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/licenseguard/frontend/
│   │       │   ├── api/         # HTTP Clients
│   │       │   ├── controller/  # JavaFX Controllers
│   │       │   └── model/       # UI Data Models
│   │       └── resources/
│   │           ├── fxml/        # UI Layouts
│   │           └── css/         # Styling
│   └── pom.xml                  # Frontend Build
├── pom.xml                      # Backend Build
├── README.md
├── ARCHITECTURE.md
├── DATABASE_DOCUMENTATION.md
├── HIBERNATE_DOCUMENTATION.md
├── BUSINESS_LOGIC.md
├── SECURITY.md
├── API_DOCUMENTATION.md
├── TESTING.md
└── INSTALLATION.md
```

## Requirements/Prerequisites
- **Java**: JDK 21+
- **Maven**: 3.8+
- **Database**: MySQL 8.0+
- **Git**: For version control

## Documentation & Guides
- [User Manual](USER_MANUAL.md) - Step-by-step guide for end-users operating the application.
- [Installation Guide](INSTALLATION.md) - Setup instructions for both developers and users.
- [Architecture & Design](ARCHITECTURE.md) - Deep dive into system design.

## Download
Download the latest packaged release for Windows:
[Download LicenseGuard v1.0.0](https://github.com/Rupesh-1221/License-Guard-Project-Java-/releases/download/v1.0.0/LicenseGuard-Windows.zip)

## Known Limitations & Future Improvements
- **Authentication**: Currently lacks an integrated authentication framework (e.g., Spring Security/JWT) and password hashing (BCrypt).
- **Role-Based Access Control**: UI does not currently restrict views based on user roles (Admin vs User).
- **Audit Logging**: While renewals are tracked, general entity modifications (who updated a software title) are not historically logged.

---
**GitHub Repository**: [License-Guard-Project-Java-](https://github.com/Rupesh-1221/License-Guard-Project-Java-)