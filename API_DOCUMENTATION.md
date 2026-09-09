# LicenseGuard REST API Documentation

**Base URL**: `http://localhost:8080`

---

## Standard Response Structure

### Success Response (HTTP 200 / 201)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-08-31T17:17:49.5677411"
}
```

### Error Response (HTTP 400 / 404 / 409 / 500)
```json
{
  "error": "Error Category",
  "message": "Detailed description",
  "path": "/api/...",
  "status": 400,
  "timestamp": "2026-08-31T17:17:49.5837281",
  "validationErrors": {
    "field": "Validation error message"
  }
}
```

---

## 1. Departments

### `POST /api/departments`
- **Purpose**: Create a new department.
- **Expected Status**: `201 Created`
- **Request Body Example**:
  ```json
  {
    "departmentName": "Engineering",
    "description": "Software Development Department"
  }
  ```
- **Response Data Example**:
  ```json
  {
    "departmentId": 1,
    "departmentName": "Engineering",
    "description": "Software Development Department",
    "userCount": 0
  }
  ```

### `GET /api/departments`
- **Purpose**: Retrieve all departments.
- **Expected Status**: `200 OK`

### `GET /api/departments/{id}`
- **Purpose**: Get department details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/departments/{id}`
- **Purpose**: Update department details.
- **Expected Status**: `200 OK`

### `DELETE /api/departments/{id}`
- **Purpose**: Delete department by ID.
- **Expected Status**: `200 OK`

---

## 2. Users

### `POST /api/users`
- **Purpose**: Create a new user.
- **Expected Status**: `201 Created`
- **Business Rule**: Passwords are saved securely and MUST NEVER be returned in API responses.
- **Request Body Example**:
  ```json
  {
    "name": "Jane Doe",
    "email": "jane.doe@company.com",
    "password": "SecretPassword123",
    "role": "USER",
    "status": "ACTIVE",
    "departmentId": 1
  }
  ```
- **Response Data Example**:
  ```json
  {
    "userId": 1,
    "name": "Jane Doe",
    "email": "jane.doe@company.com",
    "role": "USER",
    "status": "ACTIVE",
    "departmentId": 1,
    "departmentName": "Engineering"
  }
  ```

### `GET /api/users`
- **Query Params**: `departmentId` (optional)
- **Purpose**: List all users or filter users by department ID.
- **Expected Status**: `200 OK`

### `GET /api/users/{id}`
- **Purpose**: Get user details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/users/{id}`
- **Purpose**: Update user details.
- **Expected Status**: `200 OK`

### `DELETE /api/users/{id}`
- **Purpose**: Delete user by ID.
- **Expected Status**: `200 OK`

---

## 3. Vendors

### `POST /api/vendors`
- **Purpose**: Register a new software vendor.
- **Expected Status**: `201 Created`
- **Request Body Example**:
  ```json
  {
    "vendorName": "JetBrains",
    "contactEmail": "sales@jetbrains.com",
    "website": "https://www.jetbrains.com"
  }
  ```
- **Response Data Example**:
  ```json
  {
    "vendorId": 1,
    "vendorName": "JetBrains",
    "contactEmail": "sales@jetbrains.com",
    "website": "https://www.jetbrains.com",
    "softwareCount": 0
  }
  ```

### `GET /api/vendors`
- **Purpose**: Retrieve all vendors.
- **Expected Status**: `200 OK`

### `GET /api/vendors/{id}`
- **Purpose**: Get vendor by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/vendors/{id}`
- **Purpose**: Update vendor details.
- **Expected Status**: `200 OK`

### `DELETE /api/vendors/{id}`
- **Purpose**: Delete vendor by ID.
- **Expected Status**: `200 OK`

---

## 4. Software

### `POST /api/software`
- **Purpose**: Register a new software title under a vendor.
- **Expected Status**: `201 Created`
- **Request Body Example**:
  ```json
  {
    "softwareName": "IntelliJ IDEA Ultimate",
    "version": "2024.1",
    "description": "Java IDE for Enterprise",
    "vendorId": 1
  }
  ```
- **Response Data Example**:
  ```json
  {
    "softwareId": 1,
    "softwareName": "IntelliJ IDEA Ultimate",
    "version": "2024.1",
    "description": "Java IDE for Enterprise",
    "vendorId": 1,
    "vendorName": "JetBrains",
    "licenseCount": 0
  }
  ```

### `GET /api/software`
- **Query Params**: `vendorId` (optional)
- **Purpose**: List all software or filter software by vendor ID.
- **Expected Status**: `200 OK`

### `GET /api/software/{id}`
- **Purpose**: Get software details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/software/{id}`
- **Purpose**: Update software details.
- **Expected Status**: `200 OK`

### `DELETE /api/software/{id}`
- **Purpose**: Delete software by ID.
- **Expected Status**: `200 OK`

---

## 5. Licenses

### `POST /api/licenses`
- **Purpose**: Create a new software license.
- **Expected Status**: `201 Created` / `409 Conflict` (if key exists)
- **Request Body Example**:
  ```json
  {
    "softwareId": 1,
    "licenseKey": "IJ-98765-ABCDE-2026",
    "licenseType": "Subscription",
    "purchaseDate": "2026-01-01",
    "startDate": "2026-01-01",
    "expiryDate": "2026-12-31",
    "totalSeats": 10,
    "availableSeats": 10,
    "cost": 499.00,
    "status": "ACTIVE"
  }
  ```

### `GET /api/licenses`
- **Query Params**: `softwareId` (optional), `status` (optional)
- **Purpose**: List all licenses or filter by software ID / status.
- **Expected Status**: `200 OK`

### `GET /api/licenses/expiring`
- **Query Params**: `days` (default 30)
- **Purpose**: List licenses expiring within specified days.
- **Expected Status**: `200 OK`

### `GET /api/licenses/{id}`
- **Purpose**: Get license details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/licenses/{id}`
- **Purpose**: Update license details.
- **Expected Status**: `200 OK`

### `DELETE /api/licenses/{id}`
- **Purpose**: Delete license by ID.
- **Expected Status**: `200 OK`

---

## 6. License Assignments

### `POST /api/license-assignments`
- **Purpose**: Assign a license seat to a user.
- **Expected Status**: `201 Created` / `400 Bad Request` / `409 Conflict`
- **Business Rules**:
  - `available_seats` must be > 0. Decreases `available_seats` by 1.
  - Returns `400 Bad Request` if `available_seats` == 0.
  - Returns `409 Conflict` if active assignment already exists for user and license.
- **Request Body Example**:
  ```json
  {
    "licenseId": 1,
    "userId": 1,
    "assignedDate": "2026-08-31"
  }
  ```

### `GET /api/license-assignments`
- **Query Params**: `licenseId` (optional), `userId` (optional)
- **Purpose**: List all assignments or filter by license ID / user ID.
- **Expected Status**: `200 OK`

### `GET /api/license-assignments/{id}`
- **Purpose**: Get assignment details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `PUT /api/license-assignments/{id}/unassign`
- **Purpose**: Unassign a license assignment.
- **Expected Status**: `200 OK` / `400 Bad Request`
- **Business Rules**:
  - Sets status to `INACTIVE` and records `unassigned_date`.
  - Restores `available_seats` by 1 (max `total_seats`).
  - Returns `400 Bad Request` if assignment is already inactive.

### `DELETE /api/license-assignments/{id}`
- **Purpose**: Delete an assignment record.
- **Expected Status**: `200 OK`

---

## 7. Renewals

### `POST /api/renewals`
- **Purpose**: Renew a license with a new expiry date.
- **Expected Status**: `201 Created` / `400 Bad Request`
- **Business Rules**:
  - Saves current license `expiryDate` as `oldExpiryDate`.
  - `newExpiryDate` must be strictly after current `expiryDate` (otherwise `400 Bad Request`).
  - Updates license `expiryDate` and sets status to `ACTIVE`.
  - Transactional operation.
- **Request Body Example**:
  ```json
  {
    "licenseId": 1,
    "newExpiryDate": "2027-12-31",
    "renewalDate": "2026-08-31",
    "renewalCost": 499.00,
    "renewedBy": 1,
    "remarks": "Annual subscription renewal"
  }
  ```

### `GET /api/renewals`
- **Query Params**: `licenseId` (optional)
- **Purpose**: List all renewals or filter by license ID.
- **Expected Status**: `200 OK`

### `GET /api/renewals/{id}`
- **Purpose**: Get renewal details by ID.
- **Expected Status**: `200 OK` / `404 Not Found`

### `DELETE /api/renewals/{id}`
- **Purpose**: Delete a renewal record.
- **Expected Status**: `200 OK`
