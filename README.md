# TaskFlow API

RESTful API for task and project management with JWT authentication.

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.1-6DB33F?style=flat&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License">
</p>

## Overview

TaskFlow is a backend API that provides:

- **Authentication** — JWT-based with access/refresh tokens
- **Projects** — CRUD with owner-based access control  
- **Tasks** — Status tracking, priorities, deadlines, assignees
- **Search** — Full-text search with filtering and pagination

## Quick Start

```bash
# Clone and start with Docker
git clone https://github.com/YOUR_USERNAME/taskflow-api.git
cd taskflow-api
docker-compose up -d

# API: http://localhost:8080/api/v1
# Docs: http://localhost:8080/swagger-ui.html
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Runtime | Java 17, Spring Boot 3.4 |
| Security | Spring Security, JWT (jjwt) |
| Data | PostgreSQL, Spring Data JPA, Flyway |
| Docs | SpringDoc OpenAPI |
| Build | Maven, Docker |
| CI/CD | GitHub Actions |

## Project Structure

```
src/main/java/com/taskflow/
├── config/          # Security, JPA, OpenAPI configuration
├── controller/      # REST endpoints
├── dto/             # Request/Response objects
├── entity/          # JPA entities
├── exception/       # Global error handling
├── mapper/          # MapStruct converters
├── repository/      # Data access layer
├── security/        # JWT filter and service
└── service/         # Business logic
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register` | Create account |
| `POST` | `/api/v1/auth/login` | Get tokens |
| `POST` | `/api/v1/auth/refresh` | Refresh access token |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/projects` | List projects |
| `POST` | `/api/v1/projects` | Create project |
| `GET` | `/api/v1/projects/{id}` | Get project |
| `PUT` | `/api/v1/projects/{id}` | Update project |
| `DELETE` | `/api/v1/projects/{id}` | Delete project |
| `GET` | `/api/v1/projects/search?q=` | Search |

### Tasks
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/tasks` | List tasks |
| `POST` | `/api/v1/tasks` | Create task |
| `GET` | `/api/v1/tasks/{id}` | Get task |
| `PUT` | `/api/v1/tasks/{id}` | Update task |
| `DELETE` | `/api/v1/tasks/{id}` | Delete task |
| `PATCH` | `/api/v1/tasks/{id}/status` | Update status |
| `GET` | `/api/v1/tasks/project/{id}` | Tasks by project |
| `GET` | `/api/v1/tasks/overdue` | Overdue tasks |

## Usage Examples

**Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","password":"Pass123!"}'
```

**Create Task:**
```bash
curl -X POST http://localhost:8080/api/v1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"title":"Setup CI/CD","projectId":1,"priority":"HIGH","dueDate":"2026-01-15"}'
```

## Configuration

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | localhost |
| `DB_USERNAME` | Database user | taskflow |
| `DB_PASSWORD` | Database password | taskflow |
| `JWT_SECRET` | Signing key (Base64, 256+ bits) | — |

## Development

```bash
# Run locally (requires PostgreSQL)
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package -DskipTests
```

## Roadmap

- [x] JWT Authentication
- [x] Project Management
- [x] Task Management  
- [x] Search & Filtering
- [x] Docker Support
- [x] CI/CD Pipeline
- [ ] Email Notifications
- [ ] Task Comments
- [ ] File Attachments

## License

[MIT](LICENSE)
