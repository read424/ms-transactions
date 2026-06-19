package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.messaging;

import com.bootcamp.ms_transaction.application.ports.output.TransactionRepositoryPort;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
  private final TransactionRepositoryPort transactionRepository;

  @KafkaListener(topics = "deposit.completed", groupId = "ms-transaction-group")
  public void onDepositCompleted(DepositCompletedEvent event) {
    log.info("Received deposit.completed event for transaction: {}", event.getTransactionId());
    transactionRepository.findById(event.getTransactionId()).flatMap(transaction -> {
      transaction.setStatus(TransactionStatus.COMPLETED);
      return transactionRepository.save(transaction);
    }).subscribe(
        saved -> log.info("Transaction {} updated to COMPLETED", saved.getId()),
        error -> log.error("Error updating transaction: {}", event.getTransactionId(), error)
    );
  }
}
