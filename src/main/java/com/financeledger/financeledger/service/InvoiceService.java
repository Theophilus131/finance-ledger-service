package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.InvoiceRequest;
import com.financeledger.financeledger.dto.response.InvoiceResponse;
import com.financeledger.financeledger.entity.Account;
import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.AccountRepository;
import com.financeledger.financeledger.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;
    private final AuditLogService auditLogService;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        // Idempotency check - return existing invoice if already created
        Optional<Invoice> existing = invoiceRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account", request.getAccountId().toString()));

        BigDecimal tax = request.getTax() != null
                ? request.getTax() : BigDecimal.ZERO;
        BigDecimal total = request.getSubtotal().add(tax);
        String invoiceNumber = "INV-" + System.currentTimeMillis();

        Invoice invoice = Invoice.builder()
                .account(account)
                .invoiceNumber(invoiceNumber)
                .subtotal(request.getSubtotal())
                .tax(tax)
                .total(total)
                .idempotencyKey(request.getIdempotencyKey())
                .dueDate(request.getDueDate())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        auditLogService.log(
                "system",
                "INVOICE_CREATED",
                "Invoice",
                savedInvoice.getId(),
                Map.of(
                        "invoiceNumber", savedInvoice.getInvoiceNumber(),
                        "total", savedInvoice.getTotal().toString()
                )
        );

        return mapToResponse(savedInvoice);
    }

    public InvoiceResponse getInvoice(UUID id) {
        return invoiceRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice", id.toString()));
    }

    public List<InvoiceResponse> getInvoicesByAccount(UUID accountId) {
        return invoiceRepository.findByAccountId(accountId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse updateStatus(UUID id, Invoice.InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice", id.toString()));
        invoice.setStatus(status);
        return mapToResponse(invoiceRepository.save(invoice));
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus())
                .subtotal(invoice.getSubtotal())
                .tax(invoice.getTax())
                .total(invoice.getTotal())
                .dueDate(invoice.getDueDate())
                .createdAt(invoice.getCreatedAt())
                .build();
    }
}