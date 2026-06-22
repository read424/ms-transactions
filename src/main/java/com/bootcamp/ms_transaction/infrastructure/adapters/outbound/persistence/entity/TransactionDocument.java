package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.entity;

import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDocument {
    @Id
    private String id;
    private TransactionType type;
    private String productId;
    private Double amount;
    private TransactionStatus status;
    private String description;
    private LocalDateTime createdAt;
}
