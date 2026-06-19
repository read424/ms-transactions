package com.bootcamp.ms_transaction.application.ports.output;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionRepositoryPort {
    Mono<Transaction> save(Transaction transaction);

    Mono<Transaction> findById(String id);

    Flux<Transaction> findByProductId(String productId);
}
