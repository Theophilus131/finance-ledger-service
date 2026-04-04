package com.financeledger.financeledger.dto.response;

import com.financeledger.financeledger.entity.JournalEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @AllArgsConstructor
@NoArgsConstructor
public class JournalEntryResponse {
    private UUID id;
    private UUID accountId;
    private JournalEntry.EntryType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime entryDate;
    private LocalDateTime createdAt;

}
