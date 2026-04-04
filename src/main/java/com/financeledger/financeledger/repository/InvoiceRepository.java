package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByIdempotencyKey(String idempotencyKey);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByAccountId(UUID accountId);
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
}
