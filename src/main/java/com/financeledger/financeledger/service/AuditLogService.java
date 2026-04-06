package com.financeledger.financeledger.service;


import com.financeledger.financeledger.entity.AuditLog;
import com.financeledger.financeledger.entity.User;
import com.financeledger.financeledger.repository.AuditLogRepository;
import com.financeledger.financeledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async
    public void log(String userEmail, String action,
                    String entityType, UUID entityId,
                    Map<String, Object> metadata) {
        try {
            User user = userRepository.findByEmail(userEmail)
                    .orElse(null);

            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .metadata(metadata)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    public List<AuditLog> getLogsByUser(UUID userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getLogsByEntity(
            String entityType, UUID entityId) {
        return auditLogRepository
                .findByEntityTypeAndEntityId(entityType, entityId);
    }

}
