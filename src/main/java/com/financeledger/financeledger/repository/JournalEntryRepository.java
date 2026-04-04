package com.financeledger.financeledger.repository;

import com.financeledger.financeledger.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    List<JournalEntry> findByAccountId(UUID accountId);

    @Query("SELECT j FROM JournalEntry j WHERE j.account.id = :accountId " +
            "AND j.entryDate BETWEEN :start AND :end ORDER BY j.entryDate ASC")
    List<JournalEntry> findByAccountIdAndDateRange(
            UUID accountId, LocalDateTime start, LocalDateTime end);
}
