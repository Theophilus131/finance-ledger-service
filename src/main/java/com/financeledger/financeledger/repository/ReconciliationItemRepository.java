package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.ReconciliationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReconciliationItemRepository extends JpaRepository<ReconciliationItem, UUID> {

    List<ReconciliationItem> findByStatementId(UUID statementId);
    List<ReconciliationItem> findByStatus(ReconciliationItem.ReconciliationStatus status);

}
