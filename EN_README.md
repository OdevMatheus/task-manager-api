# Task Manager API
Note: This project was developed under the internal codename TodoSimple.

TodoSimpleAPI is a RESTful API for task management built with Java 17 and Spring Boot 2.7.2. The project focuses on applying backend architecture patterns, stateless security with JWT, centralized error handling, and interactive documentation with OpenAPI 3 / Swagger UI.

The project is structured to follow patterns used in production environments, with clear separation of responsibilities, persistence with JPA/Hibernate, role-based access control, and isolated execution via Docker Compose. The proposal is to serve as a study base for professional backend development best practices.

## Summary
- [Technology stack](#technology-stack)
- [Architecture and Implementation](#architecture-and-implementation)
- [Quick test guide](#quick-test-guide)
- [Authentication and authorization flow](#authentication-and-authorization-flow)
- [API documentation](#api-documentation)
- [Simplified project structure](#simplified-project-structure)
- [Environment configuration](#environment-configuration)
- [Main endpoints for validation](#main-endpoints-for-validation)
- [Implementation Decisions](#implementation-decisions)
- [Author](#author)
- [Contact](#contact)

## Technology stack

| Layer | Technology                  | Purpose |
| --- |-----------------------------| --- |
| Language | Java 17                     | Application base, with support for modern Java features. |
| Framework | Spring Boot 2.7.2           | Main REST API framework and Spring ecosystem orchestration. |
| Security | Spring Security + JWT       | Stateless authentication and authorization with token validation. |
| Persistence | Spring Data JPA + Hibernate | Object-relational mapping and database access. |
| Database | MySQL 5.7                   | Relational persistence in a containerized environment. |
| Documentation | OpenAPI 3 / Swagger UI      | Interactive catalog of endpoints and API testing. |
| Infrastructure | Docker + Docker Compose     | Consistent provisioning of the runtime environment. |
| Productivity | Maven + Lombok              | Build, dependencies, and boilerplate reduction. |

## Architecture and Implementation

### Layered architecture

The code follows a classic and sustainable layered organization, with clear responsibilities between `Controller`, `Service`, `Repository`, and `DTO`. This approach facilitates maintenance, testability, and domain evolution without excessive coupling between HTTP input and persisted entities.

### DTOs for input protection and validation

User creation and update operations use `UserCreateDTO` and `UserUpdateDTO` to decouple the API from the domain entity. This reduces exposure of internal attributes, improves validation consistency, and reinforces control over the data accepted by the application layer.

### JWT security and authentication

Authentication and authorization are implemented with Spring Security and JWT, using custom filters in the request lifecycle:

- `JWTAuthenticationFilter`: processes credentials and issues the token after successful authentication.
- `JWTAuthorizationFilter`: validates the token on subsequent requests and rebuilds the security context.

The result is a stateless API, appropriate for integration with web clients, testing tools, and decoupled architectures.

### Role-based access control

The project adopts a role hierarchy with support for `ROLE_ADMIN` and `ROLE_USER`, represented by `ProfileEnum` with explicit codes. This design favors traceability, domain readability, and future permission expansion without losing clarity.

### Secure credential persistence

Passwords are stored using `BCryptPasswordEncoder`, ensuring strong hashing aligned with backend security recommendations.

### Resilience and centralized exception handling

Errors are handled consistently through `GlobalExceptionHandler`, which standardizes JSON responses with `ErrorResponse`. This strategy improves API predictability, simplifies client consumption, and reduces divergences between validation, authorization, and domain failure scenarios.

### Read optimization with JPA Projections

Specific queries use `TaskProjection` to return only the necessary fields in certain operations, reducing data transfer and favoring performance in read scenarios.

### Results pagination

Task listing endpoints use Spring Data's `Pageable` interface. This allows large data volumes to be processed efficiently, returning metadata about total elements and pages, reducing network traffic and server load.

## Quick test guide

### 1. Start the environment

The recommended local execution flow is via Docker Compose:

```powershell
docker-compose up --build
```

This command provisions the application and the database in isolated containers.

### 2. Consider the `.env` file

Sensitive and environment variables should be centralized in a `.env` file, which improves portability and avoids hardcoding credentials in the repository.

Example of expected environment variables:

```env
MYSQLDB_USER=root
MYSQLDB_ROOT_PASSWORD=root
MYSQLDB_DATABASE=todosimple
MYSQLDB_LOCAL_PORT=3306
MYSQLDB_DOCKER_PORT=3306

SPRING_LOCAL_PORT=8080
SPRING_DOCKER_PORT=8080
```

### 3. Open the API documentation

With the containers running, the interactive documentation is available at:

```text
http://localhost:8080/swagger-ui.html
```

### 4. Use the default credentials

The database is automatically initialized by the `src/main/resources/schema.sql` and `src/main/resources/data.sql` scripts to simplify local development and automated testing setup.

Initial credentials:

```text
username: admin
password: admin
```

### 5. Log in and obtain the token

Call the authentication endpoint in Swagger:

```http
POST /user/login
```

Send the payload:

```json
{
  "username": "admin",
  "password": "admin"
}
```

The JWT token will be returned in the `Authorization` response header, using the `Bearer <token>` format.

### 6. Authorize subsequent requests in Swagger

In Swagger UI, click the `Authorize` button and provide the full value received in the `Authorization` header, including the `Bearer ` prefix. After this step, protected endpoints can be executed with the authenticated identity.

### 7. Validate the main flows

Recommended initial exploration flow:

```text
1. Log in with admin/admin
2. Authorize Swagger with Bearer Token
3. Query user and task endpoints
4. Create, update, and delete records while authenticated
5. Validate error and permission behavior
```

### 8. To stop containers and remove persisted volumes (database)
```powershell
docker-compose down -v
```

## Authentication and authorization flow

1. The client sends credentials to the `POST /user/login` endpoint.
2. `JWTAuthenticationFilter` intercepts the request and validates username and password.
3. After successful authentication, the application issues a signed JWT.
4. The token is sent by the client in subsequent requests in the `Authorization` header.
5. `JWTAuthorizationFilter` validates the signature, expiration, and user context.
6. Spring Security applies access rules based on the role assigned to the authenticated user.

## API documentation

Documentation is configured with OpenAPI 3 and Swagger UI, including a `bearerAuth` security scheme to simplify running protected endpoints and validating the authentication journey.

## Simplified project structure

```text
src/main/java/com/matheushenrique/todosimple
├── configs
├── controllers
├── exceptions
├── models
│   ├── DTOs
│   ├── enums
│   └── projection
├── repositories
├── Security
└── services
```

## Environment configuration

### Variables and profiles

The project uses environment variables to decouple application configuration from source code. This practice facilitates local execution, CI pipelines, and promotion between environments.

Additionally, execution profiles are separated via Spring Profiles, with support for environment-specific adjustments for development and production.

### Database and initialization

The environment is prepared with a MySQL container and automatic data initialization via `schema.sql` and `data.sql`. This allows the application to be ready for testing immediately after container startup.

## Main endpoints for validation

| Method | Endpoint               | Purpose |
|--------|------------------------| --- |
| POST   | `/user`                | Create user. |
| POST   | `/user/login`          | Authenticate and obtain JWT. |
| GET    | `/user/{id}`           | Query authenticated or authorized user. |
| PUT    | `/user/{id}`           | Update user data. |
| PATCH  |  `/user/{id}/profiles` |  Promote role/profile (ADMIN only). |
| GET    | `/task/{id}`           | Query a specific task. |
| GET    | `/task/user`           | List tasks for the authenticated user. |
| POST   | `/task/{userId}`       | Create a task linked to a user. |
| PUT    | `/task/{id}`           | Update a task. |
| DELETE | `/task/{id}`           | Remove a task. |

## Implementation Decisions

- Use JWT-based authentication and authorization to ensure a stateless security flow aligned with production patterns.
- Decouple HTTP input from persistence model using DTOs, aiming for data integrity and security.
- Centralize exception handling to provide standardized and predictable responses to the API client.
- Implement role-based access control (Roles) for segmentation of admin and regular user permissions.
- Automated documentation to facilitate integration, validation, and endpoint testing.
- Containerized provisioning to ensure environment parity between development and execution.

## Author

Matheus Henrique de Araujo.

## Contact

[![LinkedIn](https://img.shields.io/badge/linkedin-%230077B5.svg?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/matheus-henrique-araujo/)
[![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/OdevMatheus)
