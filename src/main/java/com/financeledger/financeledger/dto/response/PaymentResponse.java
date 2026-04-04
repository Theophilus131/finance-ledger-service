package com.financeledger.financeledger.dto.response;

import com.financeledger.financeledger.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder @Data @AllArgsConstructor @NoArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID invoiceId;
    private BigDecimal amount;
    private String method;
    private Payment.PaymentStatus status;
    private String gatewayRef;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}
