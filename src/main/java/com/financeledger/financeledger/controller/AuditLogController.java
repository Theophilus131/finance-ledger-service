package com.financeledger.financeledger.controller;


import com.financeledger.financeledger.entity.AuditLog;
import com.financeledger.financeledger.repository.UserRepository;
import com.financeledger.financeledger.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log management")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    @Operation(summary = "Get audit logs for logged in user")
    public ResponseEntity<List<AuditLog>> getMyLogs(
            @AuthenticationPrincipal String email) {
        UUID userId = userRepository.findByEmail(email)
                .get().getId();
        return ResponseEntity.ok(
                auditLogService.getLogsByUser(userId));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get audit logs for a specific entity")
    public ResponseEntity<List<AuditLog>> getEntityLogs(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(
                auditLogService.getLogsByEntity(entityType, entityId));
    }
}
