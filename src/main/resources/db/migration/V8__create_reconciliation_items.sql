CREATE TABLE reconciliation_items (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
                                      statement_id UUID NOT NULL REFERENCES bank_statements(id) ON DELETE CASCADE,
                                      status VARCHAR(20) NOT NULL DEFAULT 'UNMATCHED',
                                      anomaly_type VARCHAR(50),
                                      anomaly_reason TEXT,
                                      reconciled_at TIMESTAMP
);