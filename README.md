# Spring Boot Blog REST API

This is a personal project I built to learn Spring Boot, RabbitMQ, and
event-driven architecture. It's a multi-module backend with a REST API and a
separate worker service that handles emails and notifications in the background.

The project has three modules:

- **api** – REST API, handles HTTP requests and talks to the database
- **worker** – background service that listens to RabbitMQ and sends emails
- **common** – shared classes and constants used by both modules

## How It Works

The project has two runnable services:

- **api** – REST API that handles HTTP requests, stores data in PostgreSQL, and
  publishes events to RabbitMQ.
- **worker** – background service that listens to RabbitMQ and processes tasks
  like sending emails. It does not have its own database.

Both services share common code through the **common** module (event classes,
RabbitMQ constants, Redis cache).

## Built With

- Java 21
- Spring Boot 4
- Spring Security, Spring Data JPA, Spring AMQP
- PostgreSQL
- RabbitMQ
- Redis
- Docker and Docker Compose
- Prometheus and Grafana (for monitoring)
- Mailpit (for testing emails locally)

## What It Can Do

### API (REST endpoints)

**Auth**
- Register, login, logout
- Refresh access tokens
- Verify email address
- CSRF token endpoint

**Users**
- Get current user profile
- Follow / unfollow other users
- List followers and following

**Posts**
- Create, update, publish, archive, unarchive, delete posts
- Get all posts (with filtering by category or author)
- Get single post by slug
- Search posts
- Check if a post slug is available
- View counting with unique visitor cookies

**Categories**
- Get all categories
- Create a new category (requires `blog.categories.create` permission)

**Storage**
- Upload files (images, etc.)
- Download files by filename
- Delete your own files (or moderate with permission)
- Upload profile picture

**Roles and permissions (privilege RBAC)**
- List and read permissions; update permission metadata (enable/disable, labels)
- Create, update, delete custom roles and attach permissions
- Get and update a user's role assignments
- Seeded roles are `owner` and `user` only; create any custom `admin` role via the API

**Notifications**
- Get notifications (paginated)
- Get count of unread notifications
- Mark single notification as read
- Mark all notifications as read
- Get and update notification preferences
- Stream notifications in real-time (SSE)

### Worker (background service)

- Sends welcome email when user registers
- Sends a reminder after 12 hours if user didn't verify email
- Does not send duplicate emails (checks Redis before sending)
- Failed emails go to dead letter queue and are retried automatically

## Project Structure

```text
spring-boot-blog-rest-api/
├── services/
│   ├── api/
│   ├── worker/
│   └── common/
├── configs/
├── scripts/
├── docker-compose.yml
├── pom.xml
└── .env.example
```

### API Service Structure

```text
services/api/
├── src/main/java/
│   ├── config/
│   ├── domains/
│   │   ├── users/
│   │   ├── posts/
│   │   ├── categories/
│   │   ├── notifications/
│   │   ├── privilege/
│   │   └── storage/
│   └── shared/
├── src/main/resources/
│   ├── db/changelog/
│   ├── messages/
│   └── application.yml
└── src/test/
```

### Worker Service Structure

```text
services/worker/
├── consumers/
├── services/email/
├── config/
└── resources/templates/
```

Each service is an independent Maven module with its own `pom.xml`.
The project follows a modular monolith architecture with shared components extracted into the `common` module.

## How to Run

### Prerequisites

- Docker
- Docker Compose
- Git

### Steps

1. Clone the repository:

```bash
git clone https://github.com/mohrezal/spring-boot-blog-rest-api.git
cd spring-boot-blog-rest-api
```

2. Copy the environment file:

```bash
cp .env.example .env
```

3. Start all services:

```bash
./scripts/up.sh
```

This will start:

- API service
- Worker service
- PostgreSQL
- Redis
- RabbitMQ
- Mailpit
- Prometheus
- Grafana

4. Seed the privilege catalog and owner (after first deploy or schema change):

```bash
# Seeds permissions and the configured owner/user roles.
# Existing users without user_roles rows get the user role.
./scripts/seed-privilege.sh

# Assigns the owner role to APPLICATION_OWNER_EMAIL (set in .env).
./scripts/seed-owner.sh
```

Set `APPLICATION_OWNER_EMAIL` in `.env` before running the owner seeder. Former
hardcoded `ADMIN` users are not auto-promoted; grant elevated roles through the
API. To create a custom admin-like role, call `POST /api/v1/roles` and assign
the moderate / category permissions you need. Do not hardcode an admin role.

## Accessing the Services

| Service                  | URL                                            |
| ------------------------ | ---------------------------------------------- |
| Swagger UI               | http://localhost:8080/swagger-ui/index.html    |
| RabbitMQ Management      | http://localhost:15672                         |
| Mailpit                  | http://localhost:8025                          |
| Prometheus               | http://localhost:9090                          |
| Grafana                  | http://localhost:3000                          |

### Default Credentials

| Service   | Username | Password |
| ----------| -------- | -------- |
| RabbitMQ  | guest    | guest    |
| Grafana   | admin    | admin    |

> Note: Swagger UI is disabled when you start the application with `APPLICATION_SWAGGER_ENABLED=false`
(the default in `.env.example`). To enable it, set `APPLICATION_SWAGGER_ENABLED=true` in your `.env`.

## Breaking Changes (privilege RBAC)

These changes apply when moving from the previous role-scope model
(`ADMIN` / `USER` enums and JWT `scope` claims) to permission-based RBAC.

- **Access tokens change shape.** Claims use `permissions` and
  `privilegeVersion` instead of `scope`. Existing access tokens are invalid;
  clients must refresh or log in again.
- **`UserSummary.role` is gone.** Fetch roles with
  `GET /api/v1/roles/assignments/{userId}`.
- **Deploy / seed order.** Run `scripts/seed-privilege.sh`, then
  `scripts/seed-owner.sh` with `APPLICATION_OWNER_EMAIL` set. Existing users
  without `user_roles` get the `user` role from the privilege seeder. Former
  `ADMIN` rows are not auto-promoted.
- **No hardcoded admin role.** Seeded roles are `owner` and `user` only.
  Create a custom `admin` role via `POST /api/v1/roles` and assign moderate /
  category permissions there.
