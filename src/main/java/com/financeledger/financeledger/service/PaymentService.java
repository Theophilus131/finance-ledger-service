package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.PaymentRequest;
import com.financeledger.financeledger.dto.response.PaymentResponse;
import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.entity.Payment;
import com.financeledger.financeledger.exception.BadRequestException;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.InvoiceRepository;
import com.financeledger.financeledger.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public PaymentResponse capturePayment(PaymentRequest request) {
        // Idempotency check - return existing payment if already processed
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            Payment existingPayment = existing.get();

            auditLogService.log(
                    "system",
                    "PAYMENT_CAPTURED",
                    "Payment",
                    existingPayment.getId(),
                    Map.of(
                            "amount", existingPayment.getAmount().toString(),
                            "method", existingPayment.getMethod(),
                            "status", existingPayment.getStatus().toString()
                    )
            );

            return mapToResponse(existingPayment);
        }

        // Validate invoice exists
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice", request.getInvoiceId().toString()));

        if (invoice.getStatus() == Invoice.InvoiceStatus.CANCELLED) {
            throw new BadRequestException("Cannot pay a cancelled invoice");
        }

        // Build and save the new payment
        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(Payment.PaymentStatus.COMPLETED)
                .idempotencyKey(request.getIdempotencyKey())
                .gatewayRef(request.getGatewayRef())
                .paidAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice status based on total paid
        BigDecimal totalPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(request.getAmount());

        if (totalPaid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
        } else {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        auditLogService.log(
                "system",
                "PAYMENT_CAPTURED",
                "Payment",
                savedPayment.getId(),
                Map.of(
                        "amount", savedPayment.getAmount().toString(),
                        "method", savedPayment.getMethod(),
                        "status", savedPayment.getStatus().toString()
                )
        );

        return mapToResponse(savedPayment);
    }

    public List<PaymentResponse> getPaymentsByInvoice(UUID invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .invoiceId(payment.getInvoice().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .gatewayRef(payment.getGatewayRef())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}