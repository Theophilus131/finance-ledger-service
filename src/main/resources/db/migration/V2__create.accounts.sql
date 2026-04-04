CREATE TABLE accounts (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          code VARCHAR(20) NOT NULL UNIQUE,
                          name VARCHAR(100) NOT NULL,
                          type VARCHAR(20) NOT NULL,
                          currency VARCHAR(10) NOT NULL DEFAULT 'NGN',
                          balance NUMERIC(19,4) NOT NULL DEFAULT 0.0000,
                          created_at TIMESTAMP NOT NULL DEFAULT NOW()
);