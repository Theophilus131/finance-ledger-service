package com.financeledger.financeledger.service;


import com.financeledger.financeledger.entity.Invoice;
import com.financeledger.financeledger.entity.Payment;
import com.financeledger.financeledger.entity.Receipt;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.InvoiceRepository;
import com.financeledger.financeledger.repository.PaymentRepository;
import com.financeledger.financeledger.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfReceiptService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;
    private final TemplateEngine templateEngine;

    private static final String RECEIPTS_DIR = "receipts/";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @Transactional
    public byte[] generateReceipt(UUID invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice", invoiceId.toString()));

        Payment payment = paymentRepository
                .findByInvoiceId(invoiceId)
                .stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No completed payment found for invoice: " + invoiceId));

        Context context = new Context();
        context.setVariable("receiptId", "REC-" + UUID.randomUUID()
                .toString().substring(0, 8).toUpperCase());
        context.setVariable("invoiceNumber", invoice.getInvoiceNumber());
        context.setVariable("subtotal", invoice.getSubtotal());
        context.setVariable("tax", invoice.getTax());
        context.setVariable("total", invoice.getTotal());
        context.setVariable("paymentMethod", payment.getMethod());
        context.setVariable("gatewayRef",
                payment.getGatewayRef() != null
                        ? payment.getGatewayRef() : "N/A");
        context.setVariable("paidAt",
                payment.getPaidAt() != null
                        ? payment.getPaidAt().format(FORMATTER) : "N/A");
        context.setVariable("generatedAt",
                java.time.LocalDateTime.now().format(FORMATTER));

        String html = templateEngine.process("receipt", context);
        byte[] pdfBytes = renderPdf(html);

        saveReceiptRecord(invoice, pdfBytes);

        return pdfBytes;
    }

    private byte[] renderPdf(String html) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void saveReceiptRecord(Invoice invoice, byte[] pdfBytes) {
        try {
            Files.createDirectories(Paths.get(RECEIPTS_DIR));
            String fileName = "receipt-" + invoice.getInvoiceNumber()
                    + "-" + System.currentTimeMillis() + ".pdf";
            String filePath = RECEIPTS_DIR + fileName;
            Files.write(Paths.get(filePath), pdfBytes);

            Receipt receipt = Receipt.builder()
                    .invoice(invoice)
                    .filePath(filePath)
                    .build();
            receiptRepository.save(receipt);
        } catch (IOException e) {
            log.error("Could not save receipt file: {}", e.getMessage());
        }
    }

}
