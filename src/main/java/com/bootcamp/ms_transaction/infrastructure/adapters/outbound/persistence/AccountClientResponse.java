package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountClientResponse {
  private String id;
  private String accountType;
  private Double currentBalance;
  private Boolean isActive;
  private Integer currentMonthMovements;
  private Integer monthlyMovementLimit;
  private Integer allowedDayOfMonth;

  public boolean isActive() {
    return Boolean.TRUE.equals(isActive);
  }
}
