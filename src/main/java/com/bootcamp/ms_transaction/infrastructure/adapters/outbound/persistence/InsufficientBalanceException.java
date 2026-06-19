package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

public class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException(String accountId) {
    super("Insufficient balance for account: " + accountId);
  }
}
