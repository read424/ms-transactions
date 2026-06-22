package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.mapper;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.entity.TransactionDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDocument toDocument(Transaction transaction);
    Transaction toDomain(TransactionDocument document);
}
