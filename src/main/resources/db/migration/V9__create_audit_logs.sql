CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            user_id UUID REFERENCES users(id) ON DELETE SET NULL,
                            action VARCHAR(100) NOT NULL,
                            entity_type VARCHAR(50) NOT NULL,
                            entity_id UUID,
                            metadata JSONB,
                            created_at TIMESTAMP NOT NULL DEFAULT NOW()
);