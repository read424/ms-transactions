package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.repository;

import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.entity.TransactionDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionMongoRepository extends ReactiveMongoRepository<TransactionDocument, String> {
    Flux<TransactionDocument> findByProductId(String productId);
}
