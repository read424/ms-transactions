package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

public class MonthlyLimitExceededException extends RuntimeException {
  public MonthlyLimitExceededException(String accountId) {
    super("Monthly movement limit exceeded for account: " + accountId);
  }
}
