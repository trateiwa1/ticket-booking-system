# Ticket Booking System REST API
## Table of Contents

- Features

- Tech Stack

- Project Architecture

- Getting Started

- API Documentation

- Authentication

- API Endpoints

- Testing

- Docker

- Project Structure

- Author

## Features

- User Authentication (JWT-based authentication with role-based access control)

- Event Management (Create, read, update, and delete events)

- Ticket Management (Generate tickets and track availability)

- Booking Management (Book tickets, view booking history, cancel bookings)

- Payment Processing (Process payments and confirm bookings)

- Venue Management (Manage venues and capacity)

- Role-Based Access (USER, ORGANIZER, and ADMIN roles with different permissions)

- API Documentation (Interactive Swagger UI for testing)

- Docker Support (Containerized application for easy deployment)

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Spring Security | 7.0.3 |
| Spring Data JPA | 4.0.3 |
| H2 Database | In-memory |
| JWT | 0.11.5 |
| Maven | 4.0.0 |
| Docker | Latest |
| Swagger/OpenAPI | 3.0.2 |
| Docker | Latest |
| Swagger/OpenAPI	| 3.0.2 |

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

## Getting Started
### Prerequisites
- Java 21 or higher
- Maven
- Docker (optional)

### Option 1: Run Locally
```
git clone https://github.com/trateiwa1/ticket-booking-system.git

cd ticket-booking-system

mvn clean package

mvn spring-boot:run
```

### Option 2: Run with Docker
```
mvn clean package

docker build -t ticket-booking-system .

docker run -p 8080:8080 ticket-booking-system
```
The application will start at http://localhost:8080

## API Documentation

Once running, access the API documentation:

- Swagger UI: http://localhost:8080/swagger-ui/index.html

- API Docs: http://localhost:8080/api-docs

- H2 Database Console: http://localhost:8080/h2-console
- 
  JDBC URL: jdbc:h2:mem:tbsdb

  Username: sa

  Password: (leave empty)

## Authentication
All endpoints except /auth/register and /auth/login require a JWT token.

### Register a new user
```
{
  "email": "user@example.com",
  "name": "Test User",
  "password": "Password123",
  "role": "USER"
}
```
##### Available roles: USER, ORGANIZER
##### Note: ADMIN role cannot be created through registration for security reasons.

### Login
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
After logging in, click **Authorize** (top-right corner of Swagger UI), paste your token in the following format:
```
Bearer your_token_here
```
Click **Authorize**, then close. You can now access protected endpoints.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login and get JWT token |

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/events` | Get all events  |
| GET | `/events/me` | Get my events |
| GET | `/events/{eventId}` | Get event by ID |
| POST | `/events` | Create new event |
| PATCH | `/events/{eventId}` | Update event |
| DELETE | `/events/{eventId}` | Delete event |

### Venues

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/venues` | Get all venues |
| GET | `/venues/{venueId}` | Get venue by ID |
| POST | `/venues` | Create venue |

### Tickets

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/events/{eventId}/tickets` | Get all tickets for event |
| GET | `/events/{eventId}/tickets/available` | Get available tickets |
| POST | `/tickets` | Generate ticket |

### Bookings

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/bookings` | Get my bookings |
| GET | `/bookings/{bookingId}` | Get booking by ID |
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
| GET | `/users/me` | Get my profile |
| GET | `/users/me/bookings` | Get my bookings |
| PUT | `/users/me` | Update my profile |
| PUT | `/users/{userId}` | Update user profile |

## Testing
Includes Spring Security tests and Mockito unit tests.
Run unit tests with Maven:
```
mvn test
```
Unit tests cover core business logic for the following services:

- BookingService – Booking creation and validation
- EventService – Event creation and validation
- PaymentService – Payment processing and failure scenarios
- TicketService – Ticket generation and authorization
- VenueService – Venue creation and retrieval

## Author
#### Takundanashe Rateiwa

Computer Engineering Student | Vistula University

GitHub: [@trateiwa1](https://github.com/trateiwa1)

## License
This project is open-source and available for educational and portfolio purposes.
