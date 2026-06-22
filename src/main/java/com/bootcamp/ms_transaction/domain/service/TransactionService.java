package com.bootcamp.ms_transaction.domain.service;

import com.bootcamp.ms_transaction.application.ports.input.*;
import com.bootcamp.ms_transaction.application.ports.output.AccountValidationPort;
import com.bootcamp.ms_transaction.application.ports.output.CreditValidationPort;
import com.bootcamp.ms_transaction.application.ports.output.TransactionRepositoryPort;
import com.bootcamp.ms_transaction.domain.model.Transaction;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionStatus;
import com.bootcamp.ms_transaction.domain.model.enums.TransactionType;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.messaging.DepositPendingEvent;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.messaging.KafkaProducer;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Orchestrates transaction creation: validates against the owning service
 * (Account or Credit), persists the movement, and requests the balance
 * update. Uses pure Project Reactor — no RxJava bridging needed since
 * WebFlux, MongoDB Reactive, and WebClient all speak Reactor natively.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements
        CreateDepositUseCase,
        CreateWithdrawalUseCase,
        CreateCreditPaymentUseCase,
        CreateCardChargeUseCase,
        FindTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final AccountValidationPort accountValidationPort;
    private final CreditValidationPort creditValidationPort;
    private final KafkaProducer kafkaProducer;

    /**
     * Validates the account is active, persists the deposit, then
     * requests Account Service to increase the balance.
     */
    @Override
    @CircuitBreaker(name = "account-validation", fallbackMethod = "createDepositFallback")
    @TimeLimiter(name = "account-validation")
    public Mono<Transaction> createDeposit(String accountId, Double amount) {
        log.info("Processing deposit accountId={} amount={}", accountId, amount);

        return accountValidationPort.validateForDeposit(accountId)
                .flatMap(snapshot -> persistTransaction(
                        TransactionType.DEPOSIT, accountId, amount, null))
                .flatMap(transaction ->
                        accountValidationPort.applyBalanceChange(accountId, amount)
                                .thenReturn(transaction))
                .doOnSuccess(t -> log.info("Deposit completed txId={}", t.getId()))
                .doOnError(e -> log.warn("Deposit rejected accountId={} reason={}",
                        accountId, e.getMessage()));
    }

    /**
     * Validates balance, monthly limit (SAVINGS), and allowed day (FIXED_TERM)
     * against Account Service before persisting the withdrawal.
     */
    @CircuitBreaker(name = "account-validation")
    @TimeLimiter(name = "account-validation")
    public Mono<Transaction> createWithdrawal(String accountId, Double amount) {
        log.info("Processing withdrawal accountId={} amount={}", accountId, amount);

        return accountValidationPort.validateForWithdrawal(accountId, amount)
                .flatMap(snapshot -> persistTransaction(
                        TransactionType.WITHDRAWAL, accountId, amount, null))
                .flatMap(transaction ->
                        accountValidationPort.applyBalanceChange(accountId, -amount)
                                .thenReturn(transaction))
                .doOnSuccess(t -> log.info("Withdrawal completed txId={}", t.getId()))
                .doOnError(e -> log.warn("Withdrawal rejected accountId={} reason={}",
                        accountId, e.getMessage()));
    }

    /**
     * Validates the credit exists, persists the payment, then requests
     * Credit Service to apply it (reduces balance for loans, increases
     * availableCredit for cards — Credit Service decides which).
     */
    @CircuitBreaker(name = "account-validation")
    @TimeLimiter(name = "account-validation")
    public Mono<Transaction> createCreditPayment(String creditId, Double amount) {
        log.info("Processing credit payment creditId={} amount={}", creditId, amount);

        return creditValidationPort.validateForPayment(creditId)
                .flatMap(snapshot -> persistTransaction(
                        TransactionType.CREDIT_PAYMENT, creditId, amount, null))
                .flatMap(transaction ->
                        creditValidationPort.applyPayment(creditId, amount)
                                .thenReturn(transaction))
                .doOnSuccess(t -> log.info("Credit payment completed txId={}", t.getId()))
                .doOnError(e -> log.warn("Credit payment rejected creditId={} reason={}",
                        creditId, e.getMessage()));
    }

    /**
     * Validates the charge does not exceed availableCredit before
     * persisting and applying it.
     */
    @CircuitBreaker(name = "account-validation")
    @TimeLimiter(name = "account-validation")
    public Mono<Transaction> createCardCharge(String creditId, Double amount, String description) {
        log.info("Processing card charge creditId={} amount={}", creditId, amount);

        return creditValidationPort.validateForCharge(creditId, amount)
                .flatMap(snapshot -> persistTransaction(
                        TransactionType.CARD_CHARGE, creditId, amount, description))
                .flatMap(transaction ->
                        creditValidationPort.applyCharge(creditId, amount)
                                .thenReturn(transaction))
                .doOnSuccess(t -> log.info("Card charge completed txId={}", t.getId()))
                .doOnError(e -> log.warn("Card charge rejected creditId={} reason={}",
                        creditId, e.getMessage()));
    }

    @Override
    public Mono<Transaction> findById(String id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Flux<Transaction> findByProductId(String productId) {
        return transactionRepository.findByProductId(productId);
    }

    private Mono<Transaction> persistTransaction(
            TransactionType type, String productId, Double amount, String description) {
        Transaction transaction = Transaction.builder()
                .type(type)
                .productId(productId)
                .amount(amount)
                .description(description)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }

    public Mono<Transaction> createDepositFallback(String accountId, Double amount,
            CallNotPermittedException ex) {
        log.warn("Circuit breaker open for createDeposit accountId={}, saving as PENDING and publishing to Kafka",
                accountId);
        return persistTransaction(TransactionType.DEPOSIT, accountId, amount, null)
                .flatMap(transaction -> {
                    Transaction pendingTransaction = transaction.toBuilder()
                            .status(TransactionStatus.PENDING)
                            .build();
                    return transactionRepository.save(pendingTransaction);
                })
                .flatMap(transaction -> {
                    DepositPendingEvent event = DepositPendingEvent.builder()
                            .transactionId(transaction.getId())
                            .accountId(accountId)
                            .amount(amount)
                            .timestamp(LocalDateTime.now())
                            .build();
                    return kafkaProducer.sendDepositPending(event)
                            .thenReturn(transaction);
                });
    }
}
