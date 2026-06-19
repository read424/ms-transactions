package com.bootcamp.ms_transaction.domain.model;

import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Transaction {
    private final String id;
    private final TransactionType type;

    /** accountId for DEPOSIT/WITHDRAWAL, creditId for CREDIT_PAYMENT/CARD_CHARGE. */
    private final String productId;

    private final Double amount;
    private final TransactionStatus status;
    private final String description;
    private final LocalDateTime createdAt;
}
