package com.bootcamp.ms_transaction.application.ports.output;

import com.bootcamp.ms_transaction.domain.model.event.TransactionCompletedEvent;
import reactor.core.publisher.Mono;

public interface TransactionEventPublisherPort {
    Mono<Void> publishDepositCompleted(TransactionCompletedEvent event);
    Mono<Void> publishWithdrawalCompleted(TransactionCompletedEvent event);
    Mono<Void> publishTransferCompleted(TransactionCompletedEvent event);
    Mono<Void> publishCreditPaymentCompleted(TransactionCompletedEvent event);
}
