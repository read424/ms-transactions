package com.bootcamp.ms_transaction.application.ports.output;

import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.AccountSnapshot;
import reactor.core.publisher.Mono;

public interface AccountValidationPort {
    /**
     * Validates that an account exists, is active, and the withdrawal
     * does not violate balance, monthly limit, or fixed-term day rules.
     * Emits the account's current data if valid, or an error otherwise.
     */
    Mono<AccountSnapshot> validateForWithdrawal(String accountId, Double amount);

    /** Validates that an account exists and is active for a deposit. */
    Mono<AccountSnapshot> validateForDeposit(String accountId);

    /** Requests Account Service to apply the balance change. */
    Mono<Void> applyBalanceChange(String accountId, Double delta);
}
