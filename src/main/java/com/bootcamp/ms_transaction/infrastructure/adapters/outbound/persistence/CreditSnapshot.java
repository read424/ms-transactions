package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditSnapshot {
  private String creditId;
  private String creditType;
  private Double availableCredit;
}
