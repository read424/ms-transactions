package com.bootcamp.ms_transaction.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCompletedEvent {
    private String transactionId;
    private String type;
    private String sourceAccountId;
    private String destinationAccountId;
    private Double amount;
    private String status;
    private LocalDateTime completedAt;
}
