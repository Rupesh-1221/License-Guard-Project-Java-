# LicenseGuard - User Manual

Welcome to LicenseGuard! This manual will guide you through the features and daily operations of the Software License Management System.

## 1. Starting LicenseGuard
To start the application, you must run both the backend server and the desktop client:
1. Double-click `start-backend.bat` and leave the terminal window open. Wait a few seconds for it to start.
2. Double-click `start-frontend.bat` to launch the graphical desktop application.

## 2. Dashboard
Upon opening the application, you will see the **Dashboard**.
The dashboard provides a real-time overview of your entire organization:
- **Total Licenses**: Total license records in the system.
- **Active Licenses**: Licenses currently within their valid date range.
- **Expiring Licenses**: Licenses expiring within the next 30 days (Highlighted in Amber).
- **Available Seats**: Total unassigned seats across all active licenses.
- You can also view quick summaries of **Recent Assignments** and **Recent Renewals**.

## 3. Managing Departments & Users
- **Departments**: Navigate to the Departments tab to add or view organizational units. You cannot delete a department if it contains active users.
- **Users**: Navigate to the Users tab to add employees. Every user must belong to a Department. Users can be marked as ACTIVE or INACTIVE.

## 4. Managing Vendors & Software
- **Vendors**: Add software manufacturers (e.g., Microsoft, Adobe) here.
- **Software**: Add specific software titles (e.g., Office 365, Photoshop) and link them to the respective Vendors.

## 5. Licenses
The core of the system. 
1. Go to the **Licenses** tab.
2. Click **Add License**.
3. Select the Software, input the License Key, Purchase Date, Expiry Date, Total Seats, and Cost.
4. The system will automatically track how many seats are available.

## 6. License Assignments
To allocate a license to an employee:
1. Go to **Assignments**.
2. Select a User and a License.
3. The system will verify if the License has **Available Seats**. If it is full (0 seats), the assignment will be rejected.
4. Click **Assign**. The Available Seats will instantly drop by 1.

To unassign a license:
1. Select an ACTIVE assignment in the table.
2. Click **Unassign**. The assignment status becomes INACTIVE and the seat is returned to the pool.

## 7. Renewals
When a license is expiring, do NOT delete it. Instead:
1. Go to **Renewals**.
2. Select the expiring license.
3. Input the **New Expiry Date** and the Renewal Cost.
4. Click **Renew**. The system will update the License's expiry date and permanently log the historical transaction.

## 8. Reports
Navigate to the **Reports** tab to view analytics on license utilization. You can see which software titles are heavily assigned and which licenses are approaching their expiration dates.

## 9. Error Messages
If you attempt an invalid action (e.g., assigning a full license, entering an expiry date in the past, or deleting a user with active assignments), LicenseGuard will display a clear popup error message explaining why the action was prevented.

## 10. Closing the Application
To exit, simply close the LicenseGuard desktop window. Then, return to the `start-backend.bat` terminal window and press `Ctrl+C` to stop the backend server safely.
