package com.financeledger.financeledger.dto.response;


import com.financeledger.financeledger.entity.ReconciliationItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReconciliationResponse {

    private UUID statementId;
    private int totalItems;
    private int matchedCount;
    private int unmatchedCount;
    private int anomalyCount;
    private List<ReconciliationItemResponse> items;

   @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReconciliationItemResponse {
        private UUID id;
        private UUID paymentId;
        private ReconciliationItem.ReconciliationStatus status;
        private String anomalyType;
        private String anomalyReason;
    }


}
