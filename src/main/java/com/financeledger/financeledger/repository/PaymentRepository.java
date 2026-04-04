package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
    List<Payment> findByInvoiceId(UUID invoiceId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
