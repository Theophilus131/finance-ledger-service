package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.InvoiceRequest;
import com.financeledger.financeledger.dto.response.InvoiceResponse;
import com.financeledger.financeledger.entity.Account;
import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.AccountRepository;
import com.financeledger.financeledger.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    InvoiceRepository invoiceRepository;
    @Mock
    AccountRepository accountRepository;
    @Mock AuditLogService auditLogService;

    @InjectMocks
    InvoiceService invoiceService;

    private Account mockAccount;

    @BeforeEach
    void setUp() {
        mockAccount = Account.builder()
                .code("ACC-001")
                .name("Test Account")
                .type(Account.AccountType.REVENUE)
                .currency("NGN")
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("Should create invoice successfully")
    void shouldCreateInvoiceSuccessfully() {
        InvoiceRequest request = new InvoiceRequest(
                UUID.randomUUID(),
                new BigDecimal("100000"),
                new BigDecimal("7500"),
                "IDEM-KEY-001",
                null
        );

        when(invoiceRepository.findByIdempotencyKey(anyString()))
                .thenReturn(Optional.empty());
        when(accountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(mockAccount));
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(i -> i.getArgument(0));

        InvoiceResponse response = invoiceService.createInvoice(request);

        assertThat(response.getSubtotal())
                .isEqualByComparingTo("100000");
        assertThat(response.getTotal())
                .isEqualByComparingTo("107500");
        assertThat(response.getStatus())
                .isEqualTo(Invoice.InvoiceStatus.PENDING);
    }

    @Test
    @DisplayName("Should return existing invoice for duplicate idempotency key")
    void shouldReturnExistingInvoiceForDuplicateKey() {
        Invoice existing = Invoice.builder()
                .invoiceNumber("INV-EXISTING")
                .idempotencyKey("IDEM-KEY-001")
                .subtotal(new BigDecimal("100000"))
                .tax(new BigDecimal("7500"))
                .total(new BigDecimal("107500"))
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        InvoiceRequest request = new InvoiceRequest(
                UUID.randomUUID(),
                new BigDecimal("100000"),
                new BigDecimal("7500"),
                "IDEM-KEY-001",
                null
        );

        when(invoiceRepository.findByIdempotencyKey("IDEM-KEY-001"))
                .thenReturn(Optional.of(existing));

        InvoiceResponse response = invoiceService.createInvoice(request);

        assertThat(response.getInvoiceNumber())
                .isEqualTo("INV-EXISTING");
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when account not found")
    void shouldThrowWhenAccountNotFound() {
        InvoiceRequest request = new InvoiceRequest(
                UUID.randomUUID(),
                new BigDecimal("100000"),
                BigDecimal.ZERO,
                "IDEM-KEY-002",
                null
        );

        when(invoiceRepository.findByIdempotencyKey(anyString()))
                .thenReturn(Optional.empty());
        when(accountRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> invoiceService.createInvoice(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");
    }
}
