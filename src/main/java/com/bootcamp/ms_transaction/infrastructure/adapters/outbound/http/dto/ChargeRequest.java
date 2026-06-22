package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.http.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeRequest {
  private String customerId;
  private String creditId;
  private Double amount;
}
