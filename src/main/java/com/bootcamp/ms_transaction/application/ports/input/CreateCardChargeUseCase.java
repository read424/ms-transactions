package com.bootcamp.ms_transaction.application.ports.input;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface CreateCardChargeUseCase {
    Mono<Transaction> createCardCharge(String creditId, Double amount, String description);
}
