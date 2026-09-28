# Business Logic

This document details the core rules and transactional validations enforced by the LicenseGuard Service Layer. The backend strictly prevents data corruption and ensures logical consistency before any database operation occurs.

## 1. Creation Protections
- **Unique Name/Email Constraints**: Departments cannot share the same exact name. Users cannot share the same email address. Software titles cannot have identical combinations of name + version + vendor.
- **Mathematical Integrity**: When a license is created, `available_seats` MUST equal `total_seats`.
- **Chronological Integrity**: `purchaseDate` and `startDate` cannot occur after the `expiryDate`.

## 2. Deletion Protections (Dependent Deletion)
Entities cannot be hard-deleted if they are relied upon by historical or child records. This prevents orphaned data.
- **Department**: Cannot be deleted if it has assigned Users.
- **Vendor**: Cannot be deleted if they have Software cataloged.
- **Software**: Cannot be deleted if it has active or expired Licenses.
- **User**: Cannot be deleted if they have historical License Assignments or authored Renewals.
- **License**: Cannot be deleted if it has Assignments or Renewal history.

*Instead of deletion, the system encourages updating the entity's status to INACTIVE.*

## 3. License Assignment Workflow
The most critical workflow in the application.

1. **Validation**: The Service fetches the User and the License.
2. **Seat Check**: Verifies `license.getAvailableSeats() > 0`. If 0, throws `BadRequestException`.
3. **Duplicate Check**: Queries `assignmentRepository` to ensure the User does not already hold an `ACTIVE` assignment for this exact License. If true, throws `DuplicateResourceException`.
4. **Execution**:
   - `available_seats` is decremented by 1.
   - An `ACTIVE` assignment record is inserted.
5. **Atomicity**: The entire block is wrapped in `@Transactional`. If either step fails, the seat count is not altered.

## 4. Unassignment Rules
1. **Validation**: Fetches the Assignment.
2. **Status Check**: If the assignment is already `INACTIVE`, throws `BadRequestException`.
3. **Execution**:
   - Updates Assignment status to `INACTIVE`.
   - Populates `unassigned_date` with today's date.
   - Increments `license.available_seats` by 1.

## 5. Renewal Rules
Licenses cannot be arbitrarily extended without a paper trail.

1. **Validation**: `newExpiryDate` MUST be chronologically strictly *after* the `oldExpiryDate`. If it is the same or earlier, the request is rejected.
2. **Execution**:
   - A new `Renewal` historical record is inserted storing the exact `oldExpiryDate`, costs, and the user who processed it.
   - The `License` entity's `expiryDate` is updated to the `newExpiryDate`, and its status is reset to `ACTIVE`.
3. **Atomicity**: Locked within a `@Transactional` block.
