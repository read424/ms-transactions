package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.messaging;

import com.bootcamp.ms_transaction.application.ports.output.TransactionEventPublisherPort;
import com.bootcamp.ms_transaction.domain.model.event.TransactionCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTransactionEventPublisher implements TransactionEventPublisherPort {

    private final KafkaSender<String, Object> kafkaSender;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.transaction-deposit-completed}")
    private String depositCompletedTopic;

    @Value("${kafka.topics.transaction-withdrawal-completed}")
    private String withdrawalCompletedTopic;

    @Value("${kafka.topics.transaction-transfer-completed}")
    private String transferCompletedTopic;

    @Value("${kafka.topics.transaction-credit-payment-completed}")
    private String creditPaymentCompletedTopic;

    @Override
    public Mono<Void> publishDepositCompleted(TransactionCompletedEvent event) {
        log.info("Publishing DepositCompleted for transactionId: {}", event.getTransactionId());
        return sendMessage(depositCompletedTopic, event.getTransactionId(), event)
            .doOnSuccess(result -> log.info("DepositCompleted published successfully"))
            .doOnError(error -> log.error("Error publishing DepositCompleted", error))
            .then();
    }

    @Override
    public Mono<Void> publishWithdrawalCompleted(TransactionCompletedEvent event) {
        log.info("Publishing WithdrawalCompleted for transactionId: {}", event.getTransactionId());
        return sendMessage(withdrawalCompletedTopic, event.getTransactionId(), event)
            .doOnSuccess(result -> log.info("WithdrawalCompleted published successfully"))
            .doOnError(error -> log.error("Error publishing WithdrawalCompleted", error))
            .then();
    }

    @Override
    public Mono<Void> publishTransferCompleted(TransactionCompletedEvent event) {
        log.info("Publishing TransferCompleted for transactionId: {}", event.getTransactionId());
        return sendMessage(transferCompletedTopic, event.getTransactionId(), event)
            .doOnSuccess(result -> log.info("TransferCompleted published successfully"))
            .doOnError(error -> log.error("Error publishing TransferCompleted", error))
            .then();
    }

    @Override
    public Mono<Void> publishCreditPaymentCompleted(TransactionCompletedEvent event) {
        log.info("Publishing CreditPaymentCompleted for transactionId: {}", event.getTransactionId());
        return sendMessage(creditPaymentCompletedTopic, event.getTransactionId(), event)
            .doOnSuccess(result -> log.info("CreditPaymentCompleted published successfully"))
            .doOnError(error -> log.error("Error publishing CreditPaymentCompleted", error))
            .then();
    }

    private Mono<Void> sendMessage(String topic, String key, Object event) {
        return Mono.defer(() -> {
            SenderRecord<String, Object, Void> record = SenderRecord.create(topic, null, null, key, event, null);
            return kafkaSender.send(Mono.just(record)).then();
        });
    }
}
