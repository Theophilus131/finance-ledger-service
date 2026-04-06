package com.financeledger.financeledger.controller;


import com.financeledger.financeledger.service.PdfReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Tag(name = "Receipts", description = "PDF Receipt generation")
public class ReceiptController {
    private final PdfReceiptService pdfReceiptService;

    @GetMapping("/{invoiceId}/download")
    @Operation(summary = "Download PDF receipt for a paid invoice")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable UUID invoiceId) throws Exception {
        byte[] pdf = pdfReceiptService.generateReceipt(invoiceId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=receipt-" + invoiceId + ".pdf")
                .body(pdf);
    }
}
