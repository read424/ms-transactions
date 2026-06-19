package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence;

import com.bootcamp.ms_transaction.application.ports.output.AccountValidationPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountValidationAdapter implements AccountValidationPort {
  private final WebClient.Builder webClientBuilder;

  @Override
  @CircuitBreaker(name = "account-validation", fallbackMethod = "validateForDepositFallback")
  @TimeLimiter(name = "account-validation")
  public Mono<AccountSnapshot> validateForDeposit(String accountId) {
    return fetchAccount(accountId).flatMap(account -> account.isActive()
        ? Mono.just(toSnapshot(account))
        : Mono.error(new AccountInactiveException(accountId)));
  }

  @Override
  @CircuitBreaker(name = "account-validation", fallbackMethod = "validateForWithdrawalFallback")
  @TimeLimiter(name = "account-validation")
  public Mono<AccountSnapshot> validateForWithdrawal(String accountId, Double amount) {
    return fetchAccount(accountId).flatMap(account -> {
      if (!account.isActive()) {
        return Mono.error(new AccountInactiveException(accountId));
      }
      if (account.getCurrentBalance() < amount) {
        return Mono.error(new InsufficientBalanceException(accountId));
      }
      if ("SAVINGS".equals(account.getAccountType())
          && account.getCurrentMonthMovements() >= account.getMonthlyMovementLimit()) {
        return Mono.error(new MonthlyLimitExceededException(accountId));
      }
      if ("FIXED_TERM".equals(account.getAccountType())
          && LocalDate.now().getDayOfMonth() != account.getAllowedDayOfMonth()) {
        return Mono.error(new InvalidWithdrawalDayException(accountId));
      }
      return Mono.just(toSnapshot(account));
    });
  }

  @Override
  @CircuitBreaker(name = "account-validation")
  @TimeLimiter(name = "account-validation")
  public Mono<Void> applyBalanceChange(String accountId, Double delta) {
    return webClientBuilder.build().put()
        .uri("http://ACCOUNT-SERVICE/accounts/{id}/balance", accountId)
        .bodyValue(new BalanceChangeRequest(delta)).retrieve().bodyToMono(Void.class);
  }

  private Mono<AccountClientResponse> fetchAccount(String accountId) {
    return webClientBuilder.build().get().uri("http://ACCOUNT-SERVICE/accounts/{id}", accountId)
        .retrieve().onStatus(HttpStatus.NOT_FOUND::equals,
            res -> Mono.error(new AccountNotFoundException(accountId)))
        .bodyToMono(AccountClientResponse.class);
  }

  private AccountSnapshot toSnapshot(AccountClientResponse response) {
    return AccountSnapshot.builder().accountId(response.getId())
        .accountType(response.getAccountType()).currentBalance(response.getCurrentBalance())
        .build();
  }

  public Mono<AccountSnapshot> validateForDepositFallback(String accountId,
      io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
    log.warn("Circuit breaker open for validateForDeposit, accountId: {}", accountId);
    return Mono.error(ex);
  }

  public Mono<AccountSnapshot> validateForWithdrawalFallback(String accountId, Double amount,
      io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
    log.warn("Circuit breaker open for validateForWithdrawal, accountId: {}", accountId);
    return Mono.error(ex);
  }
}
