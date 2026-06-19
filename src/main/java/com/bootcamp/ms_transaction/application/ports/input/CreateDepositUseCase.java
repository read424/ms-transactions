package com.bootcamp.ms_transaction.application.ports.input;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface CreateDepositUseCase {
    Mono<Transaction> CreateDepositUseCase(String accountId, Double amount);
}
