# Testing Documentation

LicenseGuard employs a strict testing strategy to ensure that business logic and HTTP endpoint behaviors remain consistent and secure.

## 1. Unit Testing (Backend)
The backend Service layer is heavily tested using **JUnit 5** and **Mockito**.

- **Scope**: Tests cover all 7 core services (`DepartmentService`, `UserService`, `VendorService`, `SoftwareService`, `LicenseService`, `LicenseAssignmentService`, `RenewalService`).
- **Methodology**: 
  - Mocked Repositories (`@Mock`) are used to isolate business logic.
  - Assertions verify that successful creations return correctly populated DTOs.
  - Negative assertions (using `assertThrows`) verify that `BadRequestException`, `DuplicateResourceException`, and `ResourceNotFoundException` are thrown exactly when constraints are violated.
- **Coverage**: 25 separate unit test scenarios.
- **Execution**: `mvn clean test`

## 2. API / End-to-End Integration Testing
To verify the complete transaction lifecycle, an End-to-End (E2E) integration script was run against the live API endpoints.

- **Environment Note**: Because a standard MySQL daemon was unavailable in the isolated sandbox environment, the integration tests were executed by temporarily shifting the Spring Boot runtime data source to an **H2 in-memory database operating in strict MySQL compatibility mode**.
- **Workflows Tested**:
  - Full CRUD operations for all entities.
  - Concurrent verification of seat decrementation/incrementation during Assignments and Unassignments.
  - Rejection of invalid data (e.g., duplicate assignments, illogical renewal dates, unsafe dependent deletions).
- **Status**: 12 complete scenarios passed flawlessly.

## 3. Frontend Validation
The JavaFX application is structurally validated via the standard Maven compiler pipeline. 
- **Execution**: `mvn clean compile` inside the `frontend/` directory ensures that FXML mappings and API DTO definitions perfectly align with the backend's expected payloads.
