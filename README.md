# Spring Boot Blog REST API

A personal project to explore Spring Boot and modern Java patterns. It provides a complete blog backend with users, posts, categories, file uploads, and notifications. Built for learning purposes, but designed as a real-world application, not just a portfolio project.

## Built With

Java 21, Spring Boot 4, Spring Security, PostgreSQL, RabbitMQ, AWS S3

## Features

**Users:** Users can register, login, and follow other users.

**Posts:** Users can create, edit, publish, and search blog posts.

**Categories:** Users can organize posts by categories.

**Storage:** Users can upload and manage images and files.

**Notifications:** Users receive real-time and email notifications.

## Structure

```
src/main/java/.../api/
├── config/             # App configuration (security, rate limiting, logging)
├── domains/            # Feature modules (users, posts, categories, storage, notifications)
└── shared/             # Shared code (models, exceptions, services, utilities)
```

## How to Run

### What you need

To run this application, you need Docker installed on your machine. Check the official guide: https://docs.docker.com/engine/install/

### Steps

1. Clone the project:
```bash
git clone https://github.com/mohrezal/spring-boot-blog-rest-api.git
cd spring-boot-blog-rest-api
```

2. Copy `.env.example` to `.env` and fill in the values:
```bash
cp .env.example .env
```

3. Build and start all services:
```bash
./scripts/up.sh
```

4. Open in your browser:
- API: `http://localhost:8080/api/v1`
- API Documentation: `http://localhost:8080/swagger-ui/index.html`
- Email Testing: `http://localhost:8025`
- RabbitMQ Dashboard: `http://localhost:15672`

## Future Plans

Things I want to add later:

### Users
- [ ] Reset password by email
- [ ] Change password
- [ ] OAuth2 authentication
- [ ] Update profile information
- [ ] Save posts to bookmarks
- [ ] User settings page

### Posts
- [ ] Home feed with personalized posts
- [ ] Comments on posts
- [ ] Like posts
- [ ] Share posts
- [ ] Show reading time
- [ ] Show related posts

### Categories
- [ ] This module needs more work (CRUD operations, images, etc.)
