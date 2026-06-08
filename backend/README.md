# Backend README

Spring Boot backend for the VAT Refund Calculation System.

## Tech Stack

* Java 21
* Spring Boot 4
* Spring Web
* Spring Data JPA
* PostgreSQL
* Maven
* OpenAPI / Swagger UI
* JUnit 5
* Mockito

## Running the Backend

### With Docker Compose

From the project root:

```bash
docker compose up --build backend
```

The backend is available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

### Locally

Start PostgreSQL first:

```bash
docker compose up db -d
```

Then start the backend:

```bash
cd backend
./mvnw spring-boot:run
```

## Database

Local PostgreSQL configuration:

```text
URL: jdbc:postgresql://localhost:5433/vat_refund_db
Username: admin
Password: admin
```

Docker internal backend configuration:

```text
URL: jdbc:postgresql://db:5432/vat_refund_db
Username: admin
Password: admin
```

## API Endpoints

### POST /api/purchases

Creates a new purchase.

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

Response status:

```text
201 Created
```

### GET /api/purchases/{userEmail}

Retrieves all purchases linked to the given user email and returns a VAT/refund summary.

Example:

```bash
curl http://localhost:8080/api/purchases/user@example.com
```

Response includes:

* all purchase records
* total net amount
* total VAT amount
* total refundable VAT
* per-purchase VAT amount and refund breakdown

## Business Rules

VAT amount:

```text
VAT amount = netAmount * vatRate
```

Refund amount:

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

Invalid input returns `400 Bad Request`.

## Error Handling

The backend uses centralized exception handling with `@RestControllerAdvice`.

Example error response:

```json
{
  "timestamp": "2026-06-08T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "messages": [
    "Unsupported VAT rate: 0.12. Supported VAT rates are: 0.27, 0.18, 0.05"
  ]
}
```

## Architecture

The backend uses layered architecture:

```text
Controller -> Service -> Repository -> Database
```

Package responsibilities:

```text
controller  -> REST API endpoints
service     -> business logic and calculations
repository  -> database access
domain      -> entity and domain enums
dto         -> request and response objects
exception   -> centralized error handling
config      -> CORS and application configuration
business    -> fixed business policy values
```

## Tests

Run tests:

```bash
./mvnw test
```

The unit tests cover:

* VAT and refund calculation for Electronics
* VAT and refund calculation for Clothing
* VAT and refund calculation for Food
* unsupported VAT rate validation
* VAT summary calculation

## Notes

The application uses JPA to create/update database tables automatically during development.

For production, database migrations with Flyway or Liquibase would be preferred.
