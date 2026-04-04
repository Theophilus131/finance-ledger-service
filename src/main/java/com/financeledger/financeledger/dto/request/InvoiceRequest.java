package com.financeledger.financeledger.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor
public class InvoiceRequest {
    @NotNull(message = "Account ID is required")
    private UUID accountId;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.01", message = "Subtotal must be greater than zero")
    private BigDecimal subtotal;

    private BigDecimal tax = BigDecimal.ZERO;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    private LocalDateTime dueDate;
}
