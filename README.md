# LicenseGuard

**LicenseGuard** is a comprehensive, enterprise-grade Software License Management System designed to help organizations track, manage, and assign software licenses effectively.

## 🏗️ Architecture

The system is built on a strict, modern 3-tier architecture:

1. **Frontend**: Java / JavaFX (Desktop Client)
2. **Backend**: Spring Boot 3 REST API (Java 21)
3. **Database**: MySQL 8 (Persistent Storage via Spring Data JPA / Hibernate)

The frontend communicates exclusively with the backend via HTTP/JSON REST endpoints. The backend acts as the authoritative source for all business logic, data validation, and data persistence.

## ✨ Features

LicenseGuard provides full lifecycle management across the following core modules:
- **Departments**: Manage organizational units.
- **Users**: Manage employees and assign them to departments.
- **Vendors**: Track software providers and vendors.
- **Software**: Catalog software products associated with vendors (enforces unique name + version constraints).
- **Licenses**: Track purchased licenses, total seats, available seats, costs, and valid chronological date ranges (purchase date, start date, expiry date).
- **License Assignments**: Assign available license seats to specific users. Tracks active assignments and gracefully handles seat restoration upon unassignment.
- **Renewals**: Log historical and future renewals for licenses, automatically extending expiry dates and tracking renewal costs.

## 🛡️ Business Logic & Validation

The Spring Boot backend enforces strict business rules to maintain database integrity:
- **Delete Protections**: Records (like Departments or Vendors) cannot be deleted if they have dependent records (like Users or Software) assigned to them.
- **Seat Management**: License assignments are transactionally verified to ensure seats cannot be over-provisioned. Unassigning a user correctly restores the available seat count.
- **Duplicate Prevention**: Core resources are protected against duplication (e.g., duplicate user emails, duplicate department names, duplicate software versions).
- **Graceful Error Handling**: All constraint violations are translated into clear, structured JSON error responses with proper HTTP status codes (e.g., `400 Bad Request`, `409 Conflict`), allowing the JavaFX frontend to display user-friendly messages.

## 🧪 Testing

The backend business logic is fully verified by a fast, robust test suite powered by **JUnit 5** and **Mockito**. The tests validate all constraint rules, edge cases, and exception handling without requiring a live database connection.

To run the test suite:
```bash
mvn clean test
```

## 🚀 Getting Started

### Prerequisites
- **Java 21**
- **Maven**
- **MySQL 8**

### Backend Setup
1. Create a MySQL database named `licenseguard`.
2. Update the `src/main/resources/application.properties` with your MySQL credentials.
3. Start the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
   *(Note: The database schema will be automatically validated based on the JPA entity mappings).*

### Frontend Setup
1. Ensure the Spring Boot backend is running on `http://localhost:8080`.
2. Navigate to the `frontend` directory.
3. Run the JavaFX application.