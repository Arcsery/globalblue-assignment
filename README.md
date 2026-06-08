# VAT Refund Calculation System

Full-stack web application for registering purchases, retrieving all purchases for a user, and calculating VAT refund summaries based on business rules.

## Objective

The application allows users to:

1. Register purchases
2. Retrieve all purchases linked to a user email address
3. Calculate VAT amount and refundable VAT based on purchase category rules

## Tech Stack

### Backend

* Java 21
* Spring Boot 4
* RESTful API
* Spring Data JPA
* PostgreSQL
* Maven
* OpenAPI / Swagger UI
* JUnit 5 / Mockito

### Frontend

* Angular 21
* Angular HTTP Client
* Angular Material UI components
* Reactive Forms
* Angular Signals
* SCSS

### Infrastructure

* Docker
* Docker Compose

## Running the Application with Docker

From the project root directory:

```bash
docker compose up --build
```

This starts:

| Service    | URL / Port                            |
| ---------- | ------------------------------------- |
| Frontend   | http://localhost:4200                 |
| Backend    | http://localhost:8080                 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| PostgreSQL | localhost:5433                        |

To stop the application:

```bash
docker compose down
```

To stop the application and remove the database volume:

```bash
docker compose down -v
```

## Running Locally Without Docker

### 1. Start PostgreSQL

```bash
docker compose up db -d
```

Database connection:

```text
Host: localhost
Port: 5433
Database: vat_refund_db
Username: admin
Password: admin
```

### 2. Start Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

### 3. Start Frontend

```bash
cd frontend
npm install
npm start
```

Frontend runs on:

```text
http://localhost:4200
```

## API Documentation

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Create a Purchase

```http
POST /api/purchases
```

Request body:

```json
{
  "userEmail": "user@example.com",
  "productName": "Laptop",
  "category": "Electronics",
  "netAmount": 1000,
  "vatRate": 0.27,
  "purchaseDate": "2026-06-01"
}
```

Example curl:

```bash
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -d '{
    "userEmail": "user@example.com",
    "productName": "Laptop",
    "category": "Electronics",
    "netAmount": 1000,
    "vatRate": 0.27,
    "purchaseDate": "2026-06-01"
  }'
```

Successful response status:

```text
201 Created
```

### Retrieve Purchases and VAT Summary

```http
GET /api/purchases/{userEmail}
```

Example curl:

```bash
curl http://localhost:8080/api/purchases/user@example.com
```

Example response:

```json
{
  "userEmail": "user@example.com",
  "totalNetAmount": 2000,
  "totalVat": 540,
  "totalRefundableVat": 405,
  "currency": "EUR",
  "purchases": [
    {
      "productName": "Laptop",
      "category": "ELECTRONICS",
      "netAmount": 1000,
      "vatRate": 0.27,
      "vatAmount": 270,
      "refund": 270
    },
    {
      "productName": "Jacket",
      "category": "CLOTHING",
      "netAmount": 1000,
      "vatRate": 0.27,
      "vatAmount": 270,
      "refund": 135
    }
  ]
}
```

## Business Rules

VAT amount is calculated from the net amount:

```text
VAT amount = netAmount * vatRate
```

Refund is calculated from VAT only:

```text
Refund = VAT amount * refund percentage
```

Refund percentages:

| Category    | Refund percentage |
| ----------- | ----------------: |
| Electronics |              100% |
| Clothing    |               50% |
| Food        |                0% |

Supported VAT rates:

```text
0.27
0.18
0.05
```

Currency:

```text
EUR
```

## Validation

The backend validates:

* valid email format
* required product name
* required category
* supported category
* positive net amount
* supported VAT rate
* required purchase date
* purchase date cannot be in the future

The frontend also validates the form before submitting to the backend.

## Frontend Features

The frontend contains:

* purchase registration dialog
* input validation including email format
* Angular Material datepicker with future dates disabled
* user email search
* VAT summary card
* Angular Material table for per-purchase breakdown
* frontend-side paginator for the table
* toast notifications for success and error messages

## Architecture Decisions

The backend follows a layered architecture:

```text
Controller -> Service -> Repository -> Database
```

Package responsibilities:

```text
controller  -> REST endpoints
service     -> business logic and VAT/refund calculation
repository  -> database access with Spring Data JPA
domain      -> JPA entity and domain enums
dto         -> request and response models
exception   -> centralized error handling
config      -> CORS and application configuration
business    -> fixed business policy values
```

The frontend follows a component-based structure:

```text
model       -> TypeScript interfaces, enums and constants
service     -> API calls and shared application services
shared      -> reusable utilities and toast component
pages       -> page-level components
header      -> toolbar and add purchase action
```

## Error Handling

The backend uses centralized exception handling with `@RestControllerAdvice`.

Example validation error response:

```json
{
  "timestamp": "2026-06-08T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "messages": [
    "purchaseDate: Purchase date cannot be in the future"
  ]
}
```

The frontend displays errors using a shared toast notification service and inline form validation messages.

## Logging

The backend uses structured application logging for request handling, business operations, validation failures and unexpected errors.

## Unit Tests

Backend unit tests cover:

* Electronics refund calculation
* Clothing refund calculation
* Food refund calculation
* unsupported VAT rate validation
* VAT summary calculation

Run backend tests:

```bash
cd backend
./mvnw test
```

Frontend build check:

```bash
cd frontend
npm install
npm run build
```

## Assumptions Made

* Currency is fixed to EUR because the assignment response example uses EUR and no currency conversion requirement was specified.
* Users are identified only by email address. No separate `users` table was introduced.
* Purchase categories are implemented as enum-based business rules because the assignment defines a small fixed set of categories.
* VAT rates are implemented as fixed supported values.
* VAT amount and refund are calculated dynamically in the service layer and are not stored in the database.
* Backend-side pagination was not added because the assignment explicitly asks to retrieve all purchases for a user.
* Frontend-side table pagination is used only for displaying the returned purchase list.
* The purchase date must be today or a past date. Future purchases are rejected.

## Trade-offs

A separate database table for category refund percentages was not introduced. For this assignment, the refund rules are static and small. If refund rules became configurable, country-specific or date-dependent, they could be extracted into a dedicated database table or rule service.

The application uses `spring.jpa.hibernate.ddl-auto=update` for easier local and Docker-based setup. In a production system, database migrations with Flyway or Liquibase would be preferred.

The backend returns all purchases for a user as required by the assignment. For larger datasets, backend-side pagination could be added while keeping the summary calculation global.
