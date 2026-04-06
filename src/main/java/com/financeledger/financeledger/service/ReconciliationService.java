package com.financeledger.financeledger.service;


import com.financeledger.financeledger.dto.request.BankStatementRequest;
import com.financeledger.financeledger.dto.response.BankStatementResponse;
import com.financeledger.financeledger.dto.response.ReconciliationResponse;
import com.financeledger.financeledger.entity.BankStatement;
import com.financeledger.financeledger.entity.Payment;
import com.financeledger.financeledger.entity.ReconciliationItem;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.BankStatementRepository;
import com.financeledger.financeledger.repository.PaymentRepository;
import com.financeledger.financeledger.repository.ReconciliationItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private final BankStatementRepository bankStatementRepository;
    private final PaymentRepository paymentRepository;
    private final ReconciliationItemRepository reconciliationItemRepository;

    @Transactional
    public BankStatementResponse importStatement(BankStatementRequest request) {
        BankStatement statement = BankStatement.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .statementDate(request.getStatementDate())
                .openingBalance(request.getOpeningBalance())
                .closingBalance(request.getClosingBalance())
                .build();

        statement = bankStatementRepository.save(statement);
        return mapStatementToResponse(statement);
    }

    @Transactional
    public ReconciliationResponse reconcile(UUID statementId) {
        BankStatement statement = bankStatementRepository
                .findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BankStatement", statementId.toString()));

        List<Payment> payments = paymentRepository
                .findByStatus(Payment.PaymentStatus.COMPLETED);

        List<ReconciliationItem> items = new ArrayList<>();

        for (Payment payment : payments) {
            ReconciliationItem item = ReconciliationItem.builder()
                    .payment(payment)
                    .statement(statement)
                    .status(ReconciliationItem.ReconciliationStatus.UNMATCHED)
                    .reconciledAt(java.time.LocalDateTime.now())
                    .build();

            detectAnomalies(payment, payments, item);

            if (item.getAnomalyType() != null) {
                item.setStatus(
                        ReconciliationItem.ReconciliationStatus.ANOMALY);
            } else {
                item.setStatus(
                        ReconciliationItem.ReconciliationStatus.MATCHED);
            }

            items.add(item);
        }

        List<ReconciliationItem> saved =
                reconciliationItemRepository.saveAll(items);

        return buildReconciliationResponse(statementId, saved);
    }

    private void detectAnomalies(Payment payment,
                                 List<Payment> allPayments, ReconciliationItem item) {

        // Rule 1: Negative or zero amount
        if (payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            item.setAnomalyType("NEGATIVE_BALANCE");
            item.setAnomalyReason(
                    "Payment amount is zero or negative: "
                            + payment.getAmount());
            return;
        }

        // Rule 2: Duplicate payment (same invoice, same amount)
        long duplicates = allPayments.stream()
                .filter(p -> !p.getId().equals(payment.getId()))
                .filter(p -> p.getInvoice().getId()
                        .equals(payment.getInvoice().getId()))
                .filter(p -> p.getAmount()
                        .compareTo(payment.getAmount()) == 0)
                .count();

        if (duplicates > 0) {
            item.setAnomalyType("DUPLICATE");
            item.setAnomalyReason(
                    "Duplicate payment detected for invoice: "
                            + payment.getInvoice().getInvoiceNumber());
            return;
        }

        // Rule 3: Outlier — amount exceeds invoice total by 20%
        BigDecimal invoiceTotal = payment.getInvoice().getTotal();
        BigDecimal threshold = invoiceTotal.multiply(
                BigDecimal.valueOf(1.2));

        if (payment.getAmount().compareTo(threshold) > 0) {
            item.setAnomalyType("OUTLIER");
            item.setAnomalyReason(
                    "Payment amount " + payment.getAmount()
                            + " exceeds 120% of invoice total " + invoiceTotal);
        }
    }

    private ReconciliationResponse buildReconciliationResponse(
            UUID statementId, List<ReconciliationItem> items) {

        long matched = items.stream()
                .filter(i -> i.getStatus() ==
                        ReconciliationItem.ReconciliationStatus.MATCHED)
                .count();
        long unmatched = items.stream()
                .filter(i -> i.getStatus() ==
                        ReconciliationItem.ReconciliationStatus.UNMATCHED)
                .count();
        long anomalies = items.stream()
                .filter(i -> i.getStatus() ==
                        ReconciliationItem.ReconciliationStatus.ANOMALY)
                .count();

        List<ReconciliationResponse.ReconciliationItemResponse> itemResponses =
                items.stream().map(i ->
                        ReconciliationResponse.ReconciliationItemResponse.builder()
                                .id(i.getId())
                                .paymentId(i.getPayment().getId())
                                .status(i.getStatus())
                                .anomalyType(i.getAnomalyType())
                                .anomalyReason(i.getAnomalyReason())
                                .build()
                ).collect(Collectors.toList());

        return ReconciliationResponse.builder()
                .statementId(statementId)
                .totalItems(items.size())
                .matchedCount((int) matched)
                .unmatchedCount((int) unmatched)
                .anomalyCount((int) anomalies)
                .items(itemResponses)
                .build();
    }

    private BankStatementResponse mapStatementToResponse(
            BankStatement statement) {
        return BankStatementResponse.builder()
                .id(statement.getId())
                .bankName(statement.getBankName())
                .accountNumber(statement.getAccountNumber())
                .statementDate(statement.getStatementDate())
                .openingBalance(statement.getOpeningBalance())
                .closingBalance(statement.getClosingBalance())
                .importedAt(statement.getImportedAt())
                .build();
    }

}
