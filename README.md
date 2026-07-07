# Task Manager API (TodoSimple)
🇧🇷 **Versão em Português:** [README.pt-br.md](README.pt-br.md)

An enterprise-ready RESTful API for task management built with **Java 17** and **Spring Boot 2.7**. This project focuses on applying production-grade backend architecture, stateless JWT security, centralized exception handling, and interactive OpenAPI documentation.

<div align="center">

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Matheus%20Henrique-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/matheus-henrique-araujo)
[![GitHub](https://img.shields.io/badge/GitHub-OdevMatheus-121011?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OdevMatheus)

</div>

---

## What is this?

This repository contains a robust Task Management API developed as a reference study for professional Java backend development. It implements a layered architecture, strong input validation, and profile-based security, ensuring a secure and containerized ecosystem out of the box.

---

## Technology Stack

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language** | Java 17 | Core programming language leveraging modern syntax features. |
| **Framework** | Spring Boot 2.7.2 | Base orchestrator for REST endpoints, dependency injection, and security. |
| **Security** | Spring Security + JWT | Stateless authentication and role-based authorization via cryptographically signed tokens. |
| **Database** | MySQL 5.7 | Relational persistence in containerized instances. |
| **Persistence** | Spring Data JPA + Hibernate | Object-Relational Mapping (ORM) and clean repository patterns. |
| **Documentation** | OpenAPI 3 / Swagger UI | Interactive API explorer, playground, and contract schema spec. |
| **Infrastructure** | Docker / Podman | Consistent runtime replication through Compose environments. |
| **Build Tool** | Maven | Package management, project lifecycles, and build automation. |

---

## Architecture & Design Patterns

- **Layered Architecture:** Follows a strict separation of concerns through `Controllers` (presentation), `Services` (business logic), `Repositories` (data access), and `DTOs` (data transfer).
- **Data Transfer Objects (DTOs):** Employs explicit DTOs (`UserCreateDTO`, `UserUpdateDTO`, `TaskCreateDTO`) to isolate persistence models from HTTP endpoints. This blocks over-posting attacks and enhances data safety.
- **Secure Credentials:** Passwords are salted and hashed using `BCryptPasswordEncoder` prior to persistence.
- **Centralized Exception Handling:** Standardized error responses are intercepting all controller exceptions via `GlobalExceptionHandler`, mapping them to structured `ErrorResponse` JSON schemas.
- **Pagination & Read Optimizations:** Task listing uses Spring's `Pageable` and custom database projection interfaces (`TaskProjection`) for memory-efficient and fast database queries.

---

## How to Run

### 📋 Prerequisites

Before starting, ensure you have the following installed on your machine:
* **Java 17 JDK** and **Maven** (if running locally outside containers)
* A container engine: **Docker** (with Docker Compose) OR **Podman** (with `podman-compose` / compose provider)

---

### 🚀 Quick Start (Containerized Environment)

We provide seamless container orchestration. Follow these steps to spin up the application and the database:

#### 1. Setup the Environment File
The container environment relies on variables defined in a `.env` file. You **MUST** clone the example file before running:

* **Linux / macOS:**
  ```bash
  cp .env.example .env
  ```
* **Windows (Command Prompt):**
  ```cmd
  copy .env.example .env
  ```
* **Windows (PowerShell):**
  ```powershell
  Copy-Item .env.example .env
  ```

> 💡 **Troubleshooting Port Collisions:** By default, the application runs on port `8080` and MySQL on port `3306`. If these ports are already in use on your host machine, open the `.env` file and change `SPRING_LOCAL_PORT` (e.g., to `8081`) and `MYSQLDB_LOCAL_PORT` (e.g., to `3307`).

#### 2. Start the Services
Run the following command depending on your container engine:

* **Using Docker:**
  ```bash
  docker compose up --build
  ```
* **Using Podman (Fully Supported!):**
  ```bash
  podman compose up --build
  ```

> ⚠️ **Database Healthcheck Note:** The application container is configured with a healthcheck dependency (`depends_on.mysqldb.condition: service_healthy`). The API will wait for the MySQL container to completely boot and become healthy before launching its Tomcat server. This prevents premature startup database connection crashes.

#### 3. Stopping the Environment
To stop and remove containers along with their persisted database volumes, run:
```bash
# Docker
docker compose down -v

# Podman
podman compose down -v
```

---

## Testing & Authentication Flow

### 1. Default Pre-Seeded Credentials
Upon database startup, the schema is automatically populated via `schema.sql` and `data.sql` with a default administrator account:
* **Username:** `admin`
* **Password:** `admin`

### 2. Interactive Swagger UI Documentation
Open your web browser and navigate to:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

### 3. Step-by-Step Login and Authorization Flow
Because the security is stateless (JWT), you must authenticate to access protected endpoints:

1. **Obtain the Token:** Send a login request using the Swagger UI playground or `curl`:
   ```bash
   curl -i -X POST http://localhost:8080/user/login \
     -H "Content-Type: application/json" \
     -d '{"username": "admin", "password": "admin"}'
   ```
2. **Copy the Token:** Copy the token string returned inside the `Authorization` response header (excluding the `Bearer ` prefix).
   * Format returned: `Authorization: Bearer <your-jwt-token>`
3. **Authorize in Swagger:**
   - Click the green **Authorize** button at the top-right of the Swagger page.
   - Paste your copied JWT token directly into the input field.
   - Click **Authorize**, then **Close**.
4. **Interact:** You can now query, create, update, and delete users or tasks as an authenticated administrator!

---

## Project Structure

```text
src/main/java/com/matheushenrique/todosimple
├── configs/       # Configurations (Security, CORS, OpenAPI/Swagger)
├── controllers/   # REST Controllers (User and Task endpoints)
├── exceptions/    # Global handlers and structured error responses
├── models/        # Database models, DTOs, projections, and enums
│   ├── DTOs/      # UserCreateDTO, UserUpdateDTO, TaskCreateDTO
│   ├── enums/     # ProfileEnum (ADMIN/USER privileges)
│   └── projection/# TaskProjection for fast, query-optimized responses
├── repositories/  # Database repository abstraction layers
├── Security/      # JWT validation filters and Auth details
└── services/      # Business logic services & custom exceptions
```

---

## Main Endpoints

| Method | Endpoint | Authorization | Description |
| :--- | :--- | :--- | :--- |
| **POST** | `/user` | Public | Register a new user |
| **POST** | `/user/login` | Public | Autenticate and obtain a JWT Token |
| **GET** | `/user/{id}` | Authenticated | Retrieve user details by ID |
| **PUT** | `/user/{id}` | Authenticated | Update user information (such as password) |
| **PATCH**| `/user/{id}/profiles` | **Admin Only** | Change user role privileges (e.g., promote to Admin) |
| **GET** | `/task/{id}` | Authenticated | Retrieve a specific task by ID |
| **GET** | `/task/user` | Authenticated | List all tasks associated with the authenticated user (Paged) |
| **POST** | `/task/{userId}` | Authenticated | Create a new task linked to a specific user |
| **PUT** | `/task/{id}` | Authenticated | Update a task description |
| **DELETE**| `/task/{id}` | Authenticated | Permanently delete a task |

---

## Author

**Matheus Henrique de Araujo**

* [LinkedIn](https://www.linkedin.com/in/matheus-henrique-araujo/)
* [GitHub](https://github.com/OdevMatheus)
