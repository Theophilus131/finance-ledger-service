package com.financeledger.financeledger.controller;

import com.financeledger.financeledger.dto.request.InvoiceRequest;
import com.financeledger.financeledger.dto.response.InvoiceResponse;
import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.service.InvoiceService;
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
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Invoice management")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoice(
            @PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all invoices for an account")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByAccount(
            @PathVariable UUID accountId) {
        return ResponseEntity.ok(
                invoiceService.getInvoicesByAccount(accountId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update invoice status")
    public ResponseEntity<InvoiceResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam Invoice.InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.updateStatus(id, status));
    }

}
