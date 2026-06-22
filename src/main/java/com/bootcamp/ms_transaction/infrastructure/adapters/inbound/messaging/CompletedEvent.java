package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.messaging;

public interface CompletedEvent {
  String getTransactionId();
}
