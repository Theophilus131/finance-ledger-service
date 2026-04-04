CREATE TABLE receipts (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          invoice_id UUID NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                          file_path VARCHAR(255) NOT NULL,
                          generated_at TIMESTAMP NOT NULL DEFAULT NOW()
);