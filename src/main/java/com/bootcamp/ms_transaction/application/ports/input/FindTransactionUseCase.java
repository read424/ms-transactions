package com.bootcamp.ms_transaction.application.ports.input;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindTransactionUseCase {
    Mono<Transaction> findById(String id);
    Flux<Transaction> findByProductId(String productId);
}
