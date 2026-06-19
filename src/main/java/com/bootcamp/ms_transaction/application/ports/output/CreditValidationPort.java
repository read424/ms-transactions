package com.bootcamp.ms_transaction.application.ports.output;

import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.CreditSnapshot;
import reactor.core.publisher.Mono;

public interface CreditValidationPort {
    /** Validates that a credit product exists, for payment processing. */
    Mono<CreditSnapshot> validateForPayment(String creditId);

    /** Validates that a card charge does not exceed availableCredit. */
    Mono<CreditSnapshot> validateForCharge(String creditId, Double amount);

    /** Requests Credit Service to apply the payment. */
    Mono<Void> applyPayment(String creditId, Double amount);

    /** Requests Credit Service to apply the charge. */
    Mono<Void> applyCharge(String creditId, Double amount);
}
