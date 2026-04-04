CREATE TABLE invoices (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                          invoice_number VARCHAR(50) NOT NULL UNIQUE,
                          status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                          subtotal NUMERIC(19,4) NOT NULL,
                          tax NUMERIC(19,4) NOT NULL DEFAULT 0.0000,
                          total NUMERIC(19,4) NOT NULL,
                          idempotency_key VARCHAR(100) NOT NULL UNIQUE,
                          due_date TIMESTAMP,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);