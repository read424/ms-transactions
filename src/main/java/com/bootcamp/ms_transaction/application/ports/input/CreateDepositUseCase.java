package com.bootcamp.ms_transaction.application.ports.input;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface CreateDepositUseCase {
    Mono<Transaction> createDeposit(String accountId, Double amount);
}
