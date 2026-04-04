CREATE TABLE payments (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                          amount NUMERIC(19,4) NOT NULL CHECK (amount > 0),
                          method VARCHAR(30) NOT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                          idempotency_key VARCHAR(100) NOT NULL UNIQUE,
                          gateway_ref VARCHAR(100),
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);