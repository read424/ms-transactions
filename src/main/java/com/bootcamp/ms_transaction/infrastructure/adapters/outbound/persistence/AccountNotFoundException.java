package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(String accountId) {
    super("Account not found: " + accountId);
  }
}
