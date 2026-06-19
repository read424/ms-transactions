package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceChangeRequest {
  private Double delta;
}
