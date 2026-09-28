# Installation Guide

Follow these steps to deploy and run LicenseGuard locally.

## Prerequisites
Before you begin, ensure you have the following installed on your machine:
- **Java**: JDK 21
- **Maven**: 3.8+
- **Database**: MySQL 8.0+
- **Git**

## 1. Database Setup

1. Open your MySQL client (e.g., MySQL Workbench or CLI).
2. Create the database:
   ```sql
   CREATE DATABASE licenseguard;
   ```
3. *(Optional)* Create a dedicated user, or prepare to use your `root` credentials.

## 2. Backend Configuration

1. Clone the repository:
   ```bash
   git clone https://github.com/Rupesh-1221/License-Guard-Project-Java.git
   cd License-Guard-Project-Java
   ```
2. Configure the database connection by editing the `src/main/resources/application.properties` file:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/licenseguard?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
   *Replace `YOUR_MYSQL_PASSWORD` with your actual MySQL password.*
   
   **IMPORTANT**: Leave `spring.jpa.hibernate.ddl-auto=validate`. The schema should be created via standard SQL dumps, or you may temporarily set it to `update` for the very first run to let Hibernate generate the tables, and then immediately switch it back to `validate` for production safety.

## 3. Running the Backend (Spring Boot API)

From the root directory (`License-Guard-Project-Java/`), run:
```bash
mvn clean install
mvn spring-boot:run
```
The REST API will start on `http://localhost:8080`.

## 4. Running the Frontend (JavaFX)

The JavaFX desktop application operates entirely independently.

1. Open a new terminal window.
2. Navigate to the `frontend/` directory:
   ```bash
   cd frontend
   ```
3. Compile and run the desktop client:
   ```bash
   mvn clean javafx:run
   ```
The LicenseGuard desktop application will launch!
