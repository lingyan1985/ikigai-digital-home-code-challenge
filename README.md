
# 🏦 XA Bank - Time Deposit Refactoring Kata

This project implements a refactored version of a **Time Deposit system** following **Hexagonal Architecture** principles.  
It exposes RESTful endpoints to manage time deposit balances and retrieve account information, preserving the original business logic.

---

## 🚀 Overview

The goal of this kata is to refactor existing domain logic into a clean, extensible architecture while implementing the following functionalities:

- Update all time deposit balances based on interest rules.
- Retrieve all time deposits with their associated withdrawals.
- Preserve existing domain logic in `TimeDeposit` and `TimeDepositCalculator`.

---

## Table of Contents
- [Architecture](#architecture)
- [Business Rules](#business-rules)
- [Assumptions](#assumptions)
- [Project Layout](#project-layout)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [One Time Only Setup](#one-time-only-setup)
    - [Run PostgreSQL](#run-postgresql)
    - [Run the Application](#run-the-application)
    - [OpenAPI / Swagger](#openapi--swagger)
- [API Endpoints](#api-endpoints)
- [Database Schema](#database-schema)

---

## Architecture

**Hexagonal Architecture (Ports & Adapters)**

- **Domain**: `TimeDeposit`, `TimeDepositCalculator` (unchanged), interest policy seam (for future extensions)
- **Application**: `TimeDepositService` orchestrates use cases
- **Adapters**
    - **Primary (driving)**: REST controller (`TimeDepositController`)
    - **Secondary (driven)**: JPA repository adapter (`TimeDepositRepositoryAdapter`)
- **Infrastructure**: Spring Data JPA, Flyway, PostgreSQL

This keeps business logic independent of frameworks and makes interest policy evolution low-risk.

---

## Business Rules

Preserved behavior (from original code):

The existing business rules are **preserved exactly as before**:

| Plan Type | Interest Rate | Conditions |
|------------|----------------|-------------|
| **Basic**   | 1% annual (≈0.083% monthly) | Applies after 30 days |
| **Student** | 3% annual (≈0.25% monthly)  | Applies after 30 days, but not after 1 year |
| **Premium** | 5% annual (≈0.416% monthly) | Applies after 45 days |
| **Global Rule** | — | No interest for the first 30 days on any plan |


Interest is calculated monthly: `balance * annualRate / 12`, then rounded to cents (`HALF_UP`) and added to balance.

Interest is computed monthly via the existing `TimeDepositCalculator.updateBalance(List<TimeDeposit>)`.

---

## Assumptions

These clarify decisions where the original brief allowed flexibility:

1. **Days field semantics**: `days` means *elapsed days since account opening* (not days remaining or term length). We do not mutate `days` in the API; it’s treated as input state.
2. **Accrual model**: Monthly **simple** interest (non-compounding inside the month). One application per `/recalculate` call. Rounding to 2 decimals with `HALF_UP`.
3. **Withdrawals**: Present for reporting; **withdrawals do not adjust interest** or balance before recalculation in this version (no average daily balance). Future work may change this.
4. **Idempotency**: Calling `/recalculate` multiple times applies monthly interest each time (equivalent to running accrual batches).
5. **Validation**: The kata says exception handling isn’t required; we keep endpoints simple and assume valid data. Minor bean validation can be added without changing endpoint shapes.
6. **Only two endpoints**: Exactly as required. No extra endpoints were added.
7. **Persistence identifiers**: `id` values are provided/seeded for kata simplicity (no auto increment necessary, but can be enabled).

Assumptions are documented to help reviewers understand behavior without reading implementation details.

---

## Project Layout

time-deposit-kata/
├─ pom.xml
├─ src/main/java/org/ikigaidigital
│ ├─ app/ # Primary (driving) adapters
│ │ ├─ web/TimeDepositController.java
│ │ └─ web/TimeDepositResponse.java
│ ├─ application/ # Use cases (application services)
│ │ └─ TimeDepositService.java
│ ├─ domain/ # Domain model + policies
│ │ └─ PlanType.java
│ ├─ domain/ # Domain model + policies
│ │ ├─ TimeDeposit.java # Existing class (unchanged)
│ │ ├─ Withdrawal.java
│ │ ├─ TimeDepositCalculator.java
│ │ ├─ interest/
│ │ │ ├─ InterestPolicy.java
│ │ │ ├─ BasicInterestPolicy.java
│ │ │ ├─ StudentInterestPolicy.java
│ │ │ ├─ NoopInterestPolicy.java
│ │ │ ├─ PremiumInterestPolicy.java
│ │ │ └─ InterestPolicyRegistry.java
│ ├─ infrastructure/ # Secondary (driven) adapters
│ │ ├─ persistence/jpa/
│ │ │ ├─ TimeDepositEntity.java
│ │ │ ├─ WithdrawalEntity.java
│ │ │ ├─ TimeDepositJpaRepository.java
│ │ │ ├─ WithdrawalJpaRepository.java
│ │ └─ persistence/TimeDepositRepositoryAdapter.java
│ ├─ ports/
│ │ └─ TimeDepositRepository.java
│ └─ TimeDepositApp.java
├─ src/main/resources/
│ ├─ application.yml
│ └─ db/migration/V1__init.sql # Flyway schema
├─ src/test/java/... # Integration tests with Testcontainers
├─ docker-compose.yml
└─ README.md

---

## Getting Started

### Prerequisites
- JDK 17+
- Maven 3.9+
- Docker (for PostgreSQL via docker-compose)

### One Time Only Setup
1. Install Docker Desktop
2. Add postgres container
   Run following command to add the postgres container:
   docker run --name xa-postgres -e POSTGRES_PASSWORD=xa -e POSTGRES_USER=xa -e POSTGRES_DB=xa -p 5432:5432 -d postgres:16

### Run PostgreSQL
From project root:

docker compose up -d

### Run the Application
From project root:
mvn clean spring-boot:run

### OpenAPI / Swagger
Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html

## API Endpoints

| Method | Endpoint | Description |
|---------|-----------|-------------|
| `POST` | `/api/v1/time-deposits/recalculate` | Updates balances of all time deposits using the original `TimeDepositCalculator` logic |
| `GET`  | `/api/v1/time-deposits` | Retrieves all time deposits with fields: `id`, `planType`, `balance`, `days`, and `withdrawals` |

### **Response Schema (GET)**
```json
[
  {
    "id": 1,
    "planType": "basic",
    "balance": 1010.00,
    "days": 60,
    "withdrawals": [
      { "id": 10, "amount": 50.0, "date": "2025-10-04" }
    ]
  }
]
```

## Database Schema

Two database tables are defined via **Flyway migration** (`V1__init.sql`):

### **Tables**
#### `time_deposits`
| Column | Type | Description |
|---------|------|-------------|
| `id` | INT | Primary key |
| `plan_type` | VARCHAR | Plan type (`basic`, `student`, `premium`) |
| `days` | INT | Days since deposit |
| `balance` | DECIMAL | Current balance |

#### `withdrawals`
| Column | Type | Description |
|---------|------|-------------|
| `id` | INT | Primary key |
| `time_deposit_id` | INT | FK → `time_deposits.id` |
| `amount` | DECIMAL | Withdrawal amount |
| `date` | DATE | Withdrawal date |

---


