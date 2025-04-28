# Event Management System (Backend)

## Overview

This repository contains the backend implementation of a **Scalable Event Management System**. It is designed as a production-ready **RESTful API** using **Spring Boot 3.x**, **Java 17**, and **PostgreSQL**. The system allows users to manage events, track attendance, and perform various operations with secure access, filtering capabilities, and scalability in mind.

## Table of Contents

* [Features](#features)
* [Tech Stack](#tech-stack)
* [API Endpoints](#api-endpoints)
* [Authentication & Authorization](#authentication--authorization)
* [Pagination & Filtering](#pagination--filtering)
* [Caching](#caching)
* [Error Handling](#error-handling)
* [Testing](#testing)
* [Configuration](#configuration)
* [Setup Instructions](#setup-instructions)
* [Future Enhancements](#future-enhancements)

## Features

### Core Features

* **User Management**: CRUD operations for users with roles (USER, ADMIN).
* **Event Management**: Create, update, delete, and list events.
* **Attendance Tracking**: Users can mark their attendance status (GOING, MAYBE, DECLINED) for events.
* **Event Visibility**: Events can be marked as PUBLIC or PRIVATE.
* **Soft Deletes**: Events are archived instead of being permanently deleted.

### Value-Added Features

* **JWT-Based Authentication**: Secure access with role-based authorization.
* **Pagination & Filtering**: Support for pagination, sorting, and advanced filters (e.g., by date range, location, visibility).
* **Caching**: Frequent queries (e.g., upcoming events) are cached using Caffeine.
* **Rate Limiting**: Basic request throttling per user/IP.
* **Comprehensive Error Handling**: Custom error responses for invalid inputs and exceptions.

## Tech Stack

* **Language**: Java 17
* **Framework**: Spring Boot 3.x
* **Database**: PostgreSQL
* **ORM**: Spring Data JPA (Hibernate)
* **Authentication**: Spring Security (JWT)
* **Caching**: Caffeine
* **Build Tool**: Maven/Gradle
* **Testing**: JUnit, Mockito, Testcontainers, H2
* **API Design**: RESTful principles with DTOs and validation.

## API Endpoints

### Authentication

* **POST /api/v1/auth/login**
    * **Request Body**:
        ```json
        {
          "email": "user@example.com"
        }
        ```
    * **Response**: JWT token for authenticated user.

### Events

* **POST /api/v1/events**
    * Create a new event (requires authentication).
    * **Request Body**: `EventRequestDTO`
    * **Response**: `EventResponseDTO`
* **GET /api/v1/events/{eventId}**
    * Get details of a specific event, including attendee count.
* **GET /api/v1/events/upcoming**
    * List upcoming events with pagination.
    * **Query Params**: `page`, `size`
* **PATCH /api/v1/events/{eventId}**
    * Update an event (only host or admin).
* **DELETE /api/v1/events/{eventId}**
    * Archive an event (soft delete).

### Attendance

* **POST /api/v1/attendance/{eventId}**
    * Mark attendance for an event.
    * **Request Body**:
        ```json
        {
          "status": "GOING"
        }
        ```
* **GET /api/v1/attendance/user**
    * Get all events a user is attending.
      **Request Body Parameters:**

* `status` (String): The attendance status. Possible values are: `GOING`, `NOT_GOING`, `MAYBE`.
* `Authentication`: Requires user authentication to identify the user marking attendance. The user's ID is typically extracted from the authentication context.

**Example Request (using curl):**

```bash
curl -X POST \
'http://localhost:8080/api/v1/attendance/a1b2c3d4-e5f6-7890-1234-567890abcdef' \
-H 'Content-Type: application/json' \
-H 'Authorization: Bearer <YOUR_AUTH_TOKEN>' \
-d '{
"status": "GOING"
}'
  ```
## `GET /api/v1/attendance/event/{eventId}/count`

**Description:** Retrieves the count of attendees (status `GOING` or `MAYBE`) for a specific event.

**Path Parameter:**

* `eventId` (UUID): The unique identifier of the event.

**Authentication:** May require specific authorization to access attendance counts for an event.

**Example Request (using curl):**

```bash
curl -X GET \
  'http://localhost:8080/api/v1/attendance/event/a1b2c3d4-e5f6-7890-1234-567890abcdef/count' \
  -H 'Authorization: Bearer <YOUR_ADMIN_TOKEN>'
  ```
## `GET /api/v1/attendance/user`

**Description:** Retrieves all events that the authenticated user is attending.

**Authentication:** Requires user authentication to identify the user whose attendances are being requested.

**Example Request (using curl):**

```bash
curl -X GET \
  'http://localhost:8080/api/v1/attendance/user' \
  -H 'Authorization: Bearer <YOUR_AUTH_TOKEN>'
  ```
## Authentication & Authorization

* **JWT Tokens**: Generated upon login and used for subsequent requests.
* **Roles**:
    * `USER`: Can create and attend events.
    * `ADMIN`: Can manage all events.
* **Authorization Rules**:
    * Only hosts or admins can update/delete events.
    * Only authenticated users can create events or mark attendance.

## Pagination & Filtering

* **Pagination**: All list endpoints support pagination with `page` and `size` query params.
* **Sorting**: Results can be sorted by fields like `startTime`.
* **Filters**: Advanced filters include date range, location, and visibility.

## Caching

* **Caffeine Cache**: Used for frequent queries like upcoming events and event details.
* **Cache Eviction**: Cache is invalidated when events are updated or deleted.

## Error Handling

* **Custom Responses**: Includes timestamp, status code, error message, and path.
* **Validation Errors**: Detailed field-level validation messages.
* **Global Exception Handling**: Covers runtime exceptions and unhandled errors.

## Testing

* **Unit Tests**: Cover service and repository layers.
* **Integration Tests**: Use Testcontainers for database testing.
* **Mocking**: Mockito is used for mocking dependencies.

## Configuration

### `application.yml`

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/event_management
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
jwt:
  secret: --- your secret key ----
  expirationMs: 3600000

```
## Setup Instructions

1.  **Clone the Repository**:

    ```bash
    git clone <repository-url>
    cd event-management-system
    ```

2.  **Set Up Database**:

      * Install PostgreSQL and create a database named `event_management`.
      * Update `application.yml` with your database credentials.

3.  **Build the Project**:

    ```bash
    ./mvnw clean install
    ```

4.  **Run the Application**:

    ```bash
    ./mvnw spring-boot:run
    ```

5.  **Test the API**:

      * Use tools like Postman or cURL to interact with the API.

## Future Enhancements

  * **Frontend Integration**: Develop a frontend interface for better user experience.
  * **Email Notifications**: Notify users about event updates.
  * **Advanced Analytics**: Provide insights into event participation trends.
  * **Social Login**: Add support for OAuth2 providers like Google or GitHub.

## License

This project is licensed under the MIT License. See the [https://www.google.com/search?q=LICENSE](https://www.google.com/search?q=LICENSE) file for details.
