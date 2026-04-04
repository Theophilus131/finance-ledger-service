package com.financeledger.financeledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="bank_statements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankStatement extends BaseEntity {

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private LocalDate statementDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal openingBalance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal closingBalance;

    @CreationTimestamp
    private LocalDateTime importedAt;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL)
    private List<ReconciliationItem> reconciliationItems;

}
