# finance-ledger-service


> A production-grade finance API built with Spring Boot 3, PostgreSQL, JWT SecurityAnd Docker


## 🗄️ Entity Relationship Explanation

The system is built around 9 core entities that work together
to deliver a complete finance ledger and reconciliation system.

### Users & Accounts
A **User** is the top-level entity representing anyone who
accesses the system. Each user can own multiple **Accounts**.
An account represents a financial ledger account with a type
(ASSET, LIABILITY, EQUITY, REVENUE, or EXPENSE), a currency,
and a running balance.

### Accounts & Journal Entries
Every **Account** has many **Journal Entries**. Each journal
entry records a single DEBIT or CREDIT movement against an
account. This is the foundation of the double-entry bookkeeping
system — every financial transaction creates at least one debit
and one credit entry, ensuring the ledger always balances.

### Accounts & Invoices
An **Account** can have many **Invoices** raised against it.
An invoice represents an amount owed, with a subtotal, tax,
and total. Each invoice carries a unique idempotency key to
prevent duplicate invoice creation.

### Invoices & Payments
An **Invoice** can be settled by one or more **Payments**.
Each payment records the amount paid, the method, and a
gateway transaction reference. Payments are idempotent —
submitting the same payment twice returns the original result
without creating a duplicate. When the total payments equal
the invoice total, the invoice status automatically updates
to-PAID.

### Invoices & Receipts
When an invoice is paid, a **Receipt** is generated as a PDF
document. Each receipt belongs to one invoice and stores the
file path of the generated PDF for download.

### Bank Statements & Reconciliation Items
A **Bank Statement** represents an imported bank file with an
opening and closing balance. Each statement is reconciled
against completed payments using **Reconciliation Items**.
Each reconciliation item links one payment to one bank
statement and records whether the payment was MATCHED,
UNMATCHED, or flagged as an ANOMALY (duplicate, negative
balance, or outlier).

### Users & Audit Logs
Every significant action in the system creates an **Audit Log**
entry linked to the user who performed it. Each log records the
action type, the affected entity type and ID, and a flexible
JSON metadata field for additional context. This provides a
full tamper-evident trail of all system activity.




## 🚀 Live API
Base URL: https://finance-ledger-service.onrender.com/swagger-ui.html
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


## Presentation
[View Presentation Slides](./screenshots/finance-ledger-presentation.pdf)

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
