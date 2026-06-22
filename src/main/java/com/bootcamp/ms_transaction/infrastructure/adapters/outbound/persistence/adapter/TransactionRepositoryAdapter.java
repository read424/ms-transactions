package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.adapter;

import com.bootcamp.ms_transaction.application.ports.output.TransactionRepositoryPort;
import com.bootcamp.ms_transaction.domain.model.Transaction;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.mapper.TransactionMapper;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.repository.TransactionMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {
    private final TransactionMongoRepository mongoRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        return mongoRepository.save(transactionMapper.toDocument(transaction))
                .map(transactionMapper::toDomain);
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return mongoRepository.findById(id)
                .map(transactionMapper::toDomain);
    }

    @Override
    public Flux<Transaction> findByProductId(String productId) {
        return mongoRepository.findByProductId(productId)
                .map(transactionMapper::toDomain);
    }
}
