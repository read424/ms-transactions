package com.bootcamp.ms_transaction.application.ports.input;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface CreateWithdrawalUseCase {
    Mono<Transaction> createWithdrawal(String accountId, Double amount);
}
