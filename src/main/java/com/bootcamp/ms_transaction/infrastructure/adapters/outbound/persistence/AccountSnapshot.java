package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSnapshot {
  private String accountId;
  private String accountType;
  private Double currentBalance;
}
