package com.financeledger.financeledger.controller;

import com.financeledger.financeledger.dto.request.PaymentRequest;
import com.financeledger.financeledger.dto.response.PaymentResponse;
import com.financeledger.financeledger.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Capture a payment (idempotent)")
    public ResponseEntity<PaymentResponse> capturePayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.capturePayment(request));
    }

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get all payments for an invoice")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvoice(
            @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByInvoice(invoiceId));
    }
}
