# Equipment Rental

A learning project for an equipment rental backend built with Java and Spring Boot. The application is split into independent services for accounts, equipment reservations, payments, and delivery. PostgreSQL, Apache Kafka, and Redis are used as infrastructure components.

> **Project status:** this is a work in progress. The README describes the source code and Docker Compose configuration currently present in the repository; it does not imply that every integration or production concern is complete.

## Architecture

```text
                         ┌──────────────────────┐
                         │     Account Service  │
                         │       :8084          │
                         └──────────┬───────────┘
                                    │
                                    │ JWT authentication
                                    │
┌──────────────────────┐  HTTP  ┌───▼──────────────────┐
│    Payment Service   │◄───────│   Equipment Service  │
│       :8082          │        │        :8081         │
└──────────┬───────────┘        └──────────┬───────────┘
           │                               │
           │          Kafka events         │
           └──────────────┬────────────────┘
                          ▼
                 ┌───────────────────┐
                 │  Delivery Service │
                 │       :8083       │
                 └───────────────────┘

 Infrastructure: PostgreSQL :5432 | Kafka :9092 | Redis :6379
```

The diagram shows the service boundaries and the intended communication paths represented in the code. It is not a deployment or security guarantee.

## Services

| Service | Port | Responsibility |
|---|---:|---|
| Account Service | 8084 | Account registration and lookup; login and JWT creation; authenticated current-user endpoint |
| Equipment Service | 8081 | Equipment and category management; reservation creation, retrieval, cancellation and payment orchestration |
| Payment Service | 8082 | Payment record creation |
| Delivery Service | 8083 | Processes reservation-paid Kafka events and creates delivery records |
| combine-libs | — | Shared DTOs and Kafka event records; not a runnable service |

## Tech stack

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security and OAuth2 Resource Server (Account Service)
- PostgreSQL 17
- Apache Kafka 4.1.0
- Redis 7 and Spring Cache
- MapStruct 1.6.3
- Lombok
- Maven
- Docker and Docker Compose

## Communication

- Equipment Service calls Payment Service over HTTP using Spring RestClient.
- Kafka event contracts are placed in `combine-libs`.
- Delivery Service listens for the `reservations.events` event topic.
- Equipment Service listens for delivery assignment events on `delivery.topic`.
- Equipment Service uses Redis as its cache provider.

## Running locally with Docker Compose

### Prerequisites

- Docker Desktop with Docker Compose
- Java 21 and Maven if you want to build the services locally

### 1. Configure the JWT secret

Create a `.env` file in the repository root (next to `compose.yml`) and add a Base64-encoded secret value:

```env
JWT_SECRET=YOUR_BASE64_SECRET
```

Generate a random 32-byte secret in PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
$rng.Dispose()
```

Copy the generated value after `JWT_SECRET=`. Do not commit `.env` or share the secret. The repository's Compose configuration expects this variable for Account Service.

### 2. Build the Maven modules

Run this command from the repository root in PowerShell:

```powershell
.\account-service\mvnw.cmd -f .\pom.xml clean package -DskipTests
```

The command uses the Maven Wrapper located in `account-service` and builds the modules listed in the root `pom.xml`.

### 3. Build and start the containers

```powershell
docker compose build
docker compose up -d
```

Check container status:

```powershell
docker compose ps
```

### 4. Stop the application

```powershell
docker compose down
```

This stops and removes the Compose containers and network. The named PostgreSQL volume is retained. To delete the database volume as well, Docker Compose supports `docker compose down -v`; **this permanently deletes the persisted local database data**, so do not run it unless that is intended.

## Logs and troubleshooting

View logs for all services:

```powershell
docker compose logs -f
```

View logs for one service:

```powershell
docker compose logs -f account-service
```

Replace `account-service` with `equipment-service`, `payment-service`, `delivery-service`, `postgres`, `kafka`, or `redis` as needed. Stop following logs with `Ctrl+C`; this does not stop the containers.

Show the last 100 lines:

```powershell
docker compose logs --tail=100 equipment-service
```

Check whether a container exited:

```powershell
docker compose ps -a
```

## API overview

The following routes are declared by the controllers in the current source tree. Request bodies are DTOs; consult the corresponding DTO classes for required fields and validation rules.

### Account Service — `http://localhost:8084`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/account` | Create an account |
| GET | `/api/account?login={login}` | Find an account by login |
| GET | `/api/account/all` | Get all accounts |
| DELETE | `/api/account/delete` | Delete an account (request body required) |
| POST | `/api/auth/login` | Authenticate and receive a JWT |
| GET | `/api/auth/me` | Get information from the authenticated JWT |

### Equipment Service — `http://localhost:8081`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/equipments` | Create equipment |
| GET | `/api/equipments` | Get all equipment |
| GET | `/api/equipments/{id}` | Get equipment by ID |
| POST | `/api/equipments/deleteAll` | Delete all equipment records |
| POST | `/api/category` | Create a category |
| POST | `/api/reservations` | Create a reservation |
| GET | `/api/reservations` | Get reservations |
| GET | `/api/reservations/{id}` | Get a reservation by ID |
| POST | `/api/reservations/cancel/{id}` | Cancel a reservation |
| POST | `/api/reservations/pay/{id}` | Initiate payment for a reservation (request body required) |
| POST | `/api/reservations/delete/{id}` | Delete a reservation |
| POST | `/api/reservations/delete` | Delete all reservations |

### Payment Service — `http://localhost:8082`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payment` | Create a payment record |

### Delivery Service — `http://localhost:8083`

No REST controller is currently present in the inspected source tree. The service processes Kafka events.

> **Authentication note:** Account Service exposes login and a JWT-protected `/api/auth/me` endpoint. Do not assume that the other services validate this JWT: their current source/configuration should be reviewed and secured before treating the system as production-ready.

## Project structure

```text
.
├── account-service/                 # Account and authentication service
├── equipment-service/
│   └── demo/                        # Equipment and reservation service
├── payment-service/
│   └── demo/                        # Payment service
├── delivery-service/
│   └── demo/                        # Delivery service
├── combine-libs/                    # Shared DTOs and Kafka event contracts
├── docker/
│   └── postgres/init.sql             # Creates the account database
├── compose.yml                       # Local infrastructure and service orchestration
└── pom.xml                           # Maven reactor modules
```

## Current limitations / next steps

- Add API documentation (for example, OpenAPI/Swagger) and example request/response payloads.
- Add integration tests covering reservation, payment, and delivery event flows.
- Configure and verify JWT validation and authorization consistently across services where required.
- Review Kafka delivery guarantees, retries, and dead-letter handling.
- Replace development database credentials with secure configuration for non-local environments.
- Add database migrations instead of relying on Hibernate schema auto-update for production.
- Add health checks and readiness checks for application containers.

## Disclaimer

This repository is a learning project and is not presented as production-ready software. Configuration values and credentials in the Compose file are intended only for local development and should be changed before any real deployment.
