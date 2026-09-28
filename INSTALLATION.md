# LicenseGuard - Installation Guide

This guide covers both end-user deployment and developer setup.

## A. End-User Installation

If you have downloaded the `LicenseGuard-Windows.zip` release package, follow these steps:

### Prerequisites
- **Java 21**: Must be installed and added to your system PATH.
- **Maven**: Must be installed and added to your system PATH.
- **MySQL 8.0+**: Must be running locally on port `3306`.

### 1. Database Setup
1. Open your MySQL client (e.g., MySQL Workbench).
2. Create the database by running:
   ```sql
   CREATE DATABASE licenseguard;
   ```

### 2. Configuration
1. Open `src/main/resources/application.properties` in a text editor.
2. Update the credentials using your actual MySQL root password:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/licenseguard?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_ACTUAL_PASSWORD
   ```
3. *First-time run only*: Change `spring.jpa.hibernate.ddl-auto=validate` to `update`. Start the backend once to create the tables, then shut it down and change it back to `validate`.

### 3. Running the Application
1. Double-click `start-backend.bat`. A terminal will open and start the Spring Boot server. **Leave this terminal open.**
2. Double-click `start-frontend.bat`. The JavaFX application will launch and connect to the backend.


---

## B. Developer Setup

### Prerequisites
- JDK 21
- Maven 3.8+
- MySQL 8.0+
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/Rupesh-1221/License-Guard-Project-Java-.git
cd License-Guard-Project-Java-
```

### 2. Database Configuration
LicenseGuard uses environment variables for secure database configuration. 
You can either set these globally in your OS, or provide them locally:
- `DB_URL` (default: `jdbc:mysql://localhost:3306/licenseguard...`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (Required)

Alternatively, create an `application-local.properties` file for your IDE.

### 3. Compiling and Testing
Run the backend tests:
```bash
mvn clean test
```

### 4. Running the Project
Start the backend API (Port 8080):
```bash
mvn spring-boot:run
```

In a separate terminal, start the JavaFX Client:
```bash
cd frontend
mvn clean javafx:run
```
