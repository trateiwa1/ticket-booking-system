# Ticket Booking System REST API
A backend REST API for managing events, tickets, bookings, payments, and venues with JWT-based authentication and PostgreSQL persistence.
## Table of Contents

- Features

- Tech Stack

- Project Architecture

- Database

- Getting Started (Run locally / Docker)

- API Documentation

- Authentication

- API Endpoints

- Testing

---

## Features

- User Authentication (JWT-based authentication with role-based access control)

- Event Management (Create, read, update, and delete events)

- Ticket Management (Generate tickets and track availability)

- Booking Management (Book tickets, view booking history, cancel bookings)

- Payment Processing (Process payments and confirm bookings)

- Venue Management (Manage venues and capacity)

- Role-Based Access (USER, ORGANIZER, and ADMIN roles with different permissions)

- API Documentation (Interactive Swagger UI for testing)

- Docker support for PostgreSQL database

---


## Tech Stack

| Technology        | Description |
|------------------|------------|
| Java 21          | Core programming language |
| Spring Boot      | Backend framework |
| Spring Security  | Authentication & Authorization |
| Spring Data JPA  | ORM and database interaction |
| PostgreSQL 16    | Production-grade relational database |
| Docker           | Containerized database deployment |
| JWT              | Secure authentication |
| Maven            | Build tool |
| Swagger/OpenAPI  | API documentation |


---


## Project Architecture

The project follows a layered architecture:
```
Controller Layer
       ↓
 Service Layer
       ↓
Repository Layer
       ↓
    Database
```
### Structure
```
src/main/java/com/example/ticketbookingsystem/
├── config/        → Security & OpenAPI configuration
├── controller/    → REST API endpoints
├── dto/           → Request/Response objects
├── enums/         → Application enums
├── exception/     → Global exception handling
├── mapper/        → Entity-DTO mappers
├── model/         → JPA entities
├── repository/    → Spring Data repositories
├── security/      → JWT authentication & filters
└── service/       → Business logic

src/test/java/com/example/ticketbookingsystem/
└── service/       → Unit tests for service layer
```

---

## Database

The primary database used in this project is **PostgreSQL 16**.

- Runs in a Docker container
- Persistent storage 
- Automatically managed schema using Hibernate (JPA)
- Supports production-like environment setup

---

## Getting Started

### Requirements
- Java 21+
- Maven
- Docker

1) Clone the repository and change the directory
```
git clone https://github.com/trateiwa1/ticket-booking-system.git  

cd ticket-booking-system
```

**A) Run the application with Docker**

Start PostgreSQL (Docker)
```
docker run --name postgres-db \
-e POSTGRES_PASSWORD="Password&123" \
-e POSTGRES_DB=ticket_booking_db \
-p 5432:5432 \
-d postgres:16
```
**B) Run the application locally**
```
mvn clean install
 
mvn spring-boot:run
```
Application URL:
```
http://localhost:8080
```
Note: This is a backend REST API - Use Swagger UI to access and test endpoints when the application is running: 
```
http://localhost:8080/swagger-ui/index.html
```
## API Documentation

Once running, access:

- Swagger UI: http://localhost:8080/swagger-ui/index.html  
- API Docs: http://localhost:8080/api-docs
  
---

## Authentication
All endpoints except ```/auth/register``` and ```/auth/login``` require a JWT token.

### Register a new user
POST ```/auth/register```
```
{
  "email": "user@example.com",
  "name": "Test User",
  "password": "Password123",
  "role": "USER"
}
```
Available roles:
- USER
- ORGANIZER
- (ADMIN cannot be created via API)

### Login
POST ```/auth/login```
```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123"
}
```
#### Response:
```
{
  "id": 1,
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "email": "user@example.com",
  "role": "USER"
}
```
### Using JWT in Swagger

In order to access protected enpoints after logging in, click **Authorize** (top-right corner of Swagger UI) and paste your token in the following format:
```
Bearer your_token_here
```

---

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register user |
| POST | `/auth/login` | Login user |

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/events` | Get all events  |
| GET | `/events/me` | Get my events |
| GET | `/events/{eventId}` | Get event |
| POST | `/events` | Create event |
| PATCH | `/events/{eventId}` | Update event |
| DELETE | `/events/{eventId}` | Delete event |

### Venues

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/venues` | Get all venues |
| GET | `/venues/{venueId}` | Get venue |
| POST | `/venues` | Create venue |

### Tickets

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/events/{eventId}/tickets` | Get event tickets |
| GET | `/events/{eventId}/tickets/available` | Get available tickets |
| POST | `/tickets` | Generate ticket |

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bookings` | Get my bookings |
| GET | `/bookings/{bookingId}` | Get booking |
| POST | `/bookings` | Create booking |
| DELETE | `/bookings/{bookingId}` | Cancel booking |

### Payments

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/payments` | Get all payments |
| POST | `/payments` | Process payment |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/me` | Get profile |
| GET | `/users/me/bookings` | Get my bookings |
| PUT | `/users/me` | Update my profile |
| PUT | `/users/{userId}` | Update user profile (ADMIN)|

---

## Testing

This project includes unit tests for **service layer logic** using:

- JUnit 5 for test structure
- Mockito for mocking dependencies

### Test Coverage

All major service classes are fully tested (**both success and failure cases**):

**1) BookingServiceTest** - Covers booking lifecycle logic and validation rules.

**2) EventServiceTest** - Covers event management rules, capacity constraints, and authorization logic.

**3) PaymentServiceTest** - Covers payment processing, validation, and status transitions.

**4) TicketServiceTest** - Covers ticket generation and availability logic

**5) VenueServiceTest** - Covers venue management and access control rules
    
### Run Tests

Run all tests using Maven:

```
mvn clean install
```
or
```
mvn test
```
---

## Author
#### Takundanashe Rateiwa

Computer Engineering Student | Vistula University

GitHub: [@trateiwa1](https://github.com/trateiwa1)

## License
This project is open-source and available for educational and portfolio purposes.
