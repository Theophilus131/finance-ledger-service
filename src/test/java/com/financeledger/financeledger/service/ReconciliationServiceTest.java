package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.response.ReconciliationResponse;
import com.financeledger.financeledger.entity.BankStatement;
import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.entity.Payment;
import com.financeledger.financeledger.repository.BankStatementRepository;
import com.financeledger.financeledger.repository.PaymentRepository;
import com.financeledger.financeledger.repository.ReconciliationItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {


    @Mock
    BankStatementRepository bankStatementRepository;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    ReconciliationItemRepository reconciliationItemRepository;

    @InjectMocks
    ReconciliationService reconciliationService;

    @Test
    @DisplayName("Should detect duplicate payment anomaly")
    void shouldDetectDuplicatePayment() {
        UUID statementId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-001")
                .total(new BigDecimal("100000"))
                .build();
        invoice.setId(UUID.randomUUID());

        Payment p1 = Payment.builder()
                .invoice(invoice)
                .amount(new BigDecimal("100000"))
                .method("BANK_TRANSFER")
                .status(Payment.PaymentStatus.COMPLETED)
                .idempotencyKey("KEY-001")
                .build();
        p1.setId(UUID.randomUUID());

        Payment p2 = Payment.builder()
                .invoice(invoice)
                .amount(new BigDecimal("100000"))
                .method("BANK_TRANSFER")
                .status(Payment.PaymentStatus.COMPLETED)
                .idempotencyKey("KEY-002")
                .build();
        p2.setId(UUID.randomUUID());

        BankStatement statement = BankStatement.builder()
                .bankName("Access Bank")
                .accountNumber("0123456789")
                .statementDate(LocalDate.now())
                .openingBalance(BigDecimal.ZERO)
                .closingBalance(new BigDecimal("200000"))
                .build();


        when(bankStatementRepository.findById(statementId))
                .thenReturn(Optional.of(statement));
        when(paymentRepository.findByStatus(
                Payment.PaymentStatus.COMPLETED))
                .thenReturn(List.of(p1, p2));
        when(reconciliationItemRepository.saveAll(anyList()))
                .thenAnswer(i -> i.getArgument(0));

        ReconciliationResponse response =
                reconciliationService.reconcile(statementId);

        assertThat(response.getAnomalyCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should detect outlier payment anomaly")
    void shouldDetectOutlierPayment() {
        UUID statementId = UUID.randomUUID();

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-002")
                .total(new BigDecimal("100000"))
                .build();
        invoice.setId(UUID.randomUUID());

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(new BigDecimal("500000"))
                .method("BANK_TRANSFER")
                .status(Payment.PaymentStatus.COMPLETED)
                .idempotencyKey("KEY-003")
                .build();
        payment.setId(UUID.randomUUID());

        BankStatement statement = BankStatement.builder()
                .bankName("GTBank")
                .accountNumber("9876543210")
                .statementDate(LocalDate.now())
                .openingBalance(BigDecimal.ZERO)
                .closingBalance(new BigDecimal("500000"))
                .build();

        when(bankStatementRepository.findById(statementId))
                .thenReturn(Optional.of(statement));
        when(paymentRepository.findByStatus(
                Payment.PaymentStatus.COMPLETED))
                .thenReturn(List.of(payment));
        when(reconciliationItemRepository.saveAll(anyList()))
                .thenAnswer(i -> i.getArgument(0));

        ReconciliationResponse response =
                reconciliationService.reconcile(statementId);

        assertThat(response.getAnomalyCount()).isEqualTo(1);
        assertThat(response.getItems().get(0).getAnomalyType())
                .isEqualTo("OUTLIER");
    }


}