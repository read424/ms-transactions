package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.messaging;

import com.bootcamp.ms_transaction.application.ports.output.TransactionRepositoryPort;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
  private final TransactionRepositoryPort transactionRepository;

  @KafkaListener(topics = "deposit.completed", groupId = "ms-transaction-group")
  public void onDepositCompleted(@Payload DepositCompletedEvent event) {
    log.info("Processing deposit.completed event for transaction: {}", event.getTransactionId());
    updateTransactionStatus(event, TransactionStatus.COMPLETED);
  }

  @KafkaListener(topics = "withdrawal.completed", groupId = "ms-transaction-group")
  public void onWithdrawalCompleted(@Payload WithdrawalCompletedEvent event) {
    log.info("Processing withdrawal.completed event for transaction: {}", event.getTransactionId());
    updateTransactionStatus(event, TransactionStatus.COMPLETED);
  }

  @KafkaListener(topics = "credit-payment.completed", groupId = "ms-transaction-group")
  public void onCreditPaymentCompleted(@Payload CreditPaymentCompletedEvent event) {
    log.info("Processing credit-payment.completed event for transaction: {}", event.getTransactionId());
    updateTransactionStatus(event, TransactionStatus.COMPLETED);
  }

  @KafkaListener(topics = "card-charge.completed", groupId = "ms-transaction-group")
  public void onCardChargeCompleted(@Payload CardChargeCompletedEvent event) {
    log.info("Processing card-charge.completed event for transaction: {}", event.getTransactionId());
    updateTransactionStatus(event, TransactionStatus.COMPLETED);
  }

  private void updateTransactionStatus(CompletedEvent event, TransactionStatus status) {
    transactionRepository.findById(event.getTransactionId())
        .flatMap(transaction -> {
          if (transaction.getStatus() == status) {
            log.debug("Transaction {} already has status {}, skipping update",
                transaction.getId(), status);
            return Mono.just(transaction);
          }
          return transactionRepository.save(transaction.toBuilder()
              .status(status)
              .build());
        })
        .doOnSuccess(saved -> log.info("✓ Transaction {} updated to {}",
            saved.getId(), status))
        .doOnError(error -> log.error("✗ Error updating transaction {}: {}",
            event.getTransactionId(), error.getMessage(), error))
        .subscribe();
  }
}
