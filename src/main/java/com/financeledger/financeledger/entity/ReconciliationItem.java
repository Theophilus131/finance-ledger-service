package com.financeledger.financeledger.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="reconciliation_items")
@Data @NoArgsConstructor  @AllArgsConstructor @Builder
public class ReconciliationItem extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private BankStatement statement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReconciliationStatus status = ReconciliationStatus.UNMATCHED;

    private String anomalyType;

    private String anomalyReason;

    private LocalDateTime reconciledAt;

    public enum ReconciliationStatus {
        MATCHED, UNMATCHED, ANOMALY
    }
}
