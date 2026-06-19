package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest;

import com.bootcamp.ms_transaction.domain.model.Transaction;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionType;
import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.models.TransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionDtoMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "productId", target = "productId")
  @Mapping(source = "amount", target = "amount")
  @Mapping(source = "description", target = "description", qualifiedByName = "mapDescription")
  @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapDateTime")
  @Mapping(ignore = true, target = "type")
  @Mapping(ignore = true, target = "status")
  TransactionResponse toResponse(Transaction transaction);

  @Named("mapDescription")
  default JsonNullable<String> mapDescription(String description) {
    return description == null ? JsonNullable.undefined() : JsonNullable.of(description);
  }

  @Named("mapDateTime")
  default OffsetDateTime mapDateTime(LocalDateTime dateTime) {
    return OffsetDateTime.of(dateTime.toLocalDate(), dateTime.toLocalTime(), ZoneOffset.UTC);
  }
}
