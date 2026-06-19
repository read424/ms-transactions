package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

public class AccountInactiveException extends RuntimeException {
  public AccountInactiveException(String accountId) {
    super("Account is inactive: " + accountId);
  }
}
