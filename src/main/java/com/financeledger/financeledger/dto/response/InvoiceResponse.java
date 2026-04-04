package com.financeledger.financeledger.dto.response;

import com.financeledger.financeledger.entity.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class InvoiceResponse {
    private UUID id;
    private String invoiceNumber;
    private Invoice.InvoiceStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
