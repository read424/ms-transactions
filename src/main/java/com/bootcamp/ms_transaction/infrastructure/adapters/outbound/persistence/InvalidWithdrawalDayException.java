package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

public class InvalidWithdrawalDayException extends RuntimeException {
  public InvalidWithdrawalDayException(String accountId) {
    super("Invalid withdrawal day for fixed-term account: " + accountId);
  }
}
