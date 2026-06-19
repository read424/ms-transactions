package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.response.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionDtoMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "type", target = "type")
  @Mapping(source = "productId", target = "productId")
  @Mapping(source = "amount", target = "amount")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "createdAt", target = "createdAt")
  TransactionResponse toResponse(Transaction transaction);
}
