CREATE TABLE journal_entries (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
                                 type VARCHAR(10) NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
                                 amount NUMERIC(19,4) NOT NULL CHECK (amount > 0),
                                 description TEXT,
                                 ref_type VARCHAR(50),
                                 ref_id UUID,
                                 entry_date TIMESTAMP NOT NULL DEFAULT NOW(),
                                 created_at TIMESTAMP NOT NULL DEFAULT NOW()
);