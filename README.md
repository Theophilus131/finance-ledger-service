# finance-ledger-service





> A production-grade finance API built with Spring Boot 3,
PostgreSQL, JWT Security and Docker




## 🚀 Live API
Base URL: https://finance-ledger-service.onrender.com
Swagger UI: http://localhost:8080/swagger-ui/index.html


Full documentation coming after deployment.



\## Tech Stack

\- Java 17

\- Spring Boot 3

\- PostgreSQL 15

\- Flyway

\- Docker & Docker compose

\- HMAC Webhook Verification

\- JWT + HMAC Security

\- OpenAPI/Swagger

\- Thymeleaf PDF Generation

\- Junit 5 + Mockito

### Prerequisites

- java 17+
- Maven
-Docker Desktop



\## Setup

Copy `application.yml.example` to `application.yml` and fill in your values.



Start the database:

\\```bash

docker-compose up -d

\\```



Run the app:

\\```bash

mvn spring-boot:run

\\```


open Swagger:
\```
http://localhost:8080/swagger-ui/index.html
\```


##  Entity Relationship Diagram
[See /screenshots/er-diagram.png]


##  Screenshots
See the /screenshots folder for:
- API response screenshots
- Database UI screenshots
- ER Diagram



##  API Endpoints

## API Endpoints

| Method | Endpoint                              | Description                  |
|--------|----------------------------------------|------------------------------|
| POST   | /api/auth/register                     | Register user                |
| POST   | /api/auth/login                        | Login and get JWT            |
| POST   | /api/accounts                          | Create account               |
| GET    | /api/accounts                          | Get my accounts              |
| POST   | /api/journal                           | Create journal entry         |
| GET    | /api/journal/trial-balance             | Get trial balance            |
| POST   | /api/invoices                          | Create invoice               |
| POST   | /api/payments                          | Capture payment              |
| GET    | /api/receipts/{id}/download            | Download PDF receipt         |
| POST   | /api/reconciliation/import             | Import bank statement        |
| POST   | /api/reconciliation/{id}/reconcile     | Run reconciliation           |
| GET    | /api/audit/me                          | Get my audit logs            |
