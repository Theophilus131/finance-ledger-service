CREATE TABLE bank_statements (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 bank_name VARCHAR(100) NOT NULL,
                                 account_number VARCHAR(50) NOT NULL,
                                 statement_date DATE NOT NULL,
                                 opening_balance NUMERIC(19,4) NOT NULL,
                                 closing_balance NUMERIC(19,4) NOT NULL,
                                 imported_at TIMESTAMP NOT NULL DEFAULT NOW()
);