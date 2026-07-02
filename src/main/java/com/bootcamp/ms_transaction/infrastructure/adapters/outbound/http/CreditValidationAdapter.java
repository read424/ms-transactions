package com.bootcamp.ms_transaction.infrastructure.adapters.outbound.http;

import com.bootcamp.ms_transaction.application.ports.output.CreditValidationPort;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.http.dto.ChargeRequest;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.http.dto.CreditClientResponse;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.http.dto.PaymentRequest;
import com.bootcamp.ms_transaction.infrastructure.adapters.outbound.persistence.CreditSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditValidationAdapter implements CreditValidationPort {
  private final WebClient.Builder webClientBuilder;

  @Value("${credit-service.url:http://ms-credits}")
  private String creditServiceUrl;

  @Override
  public Mono<CreditSnapshot> validateForPayment(String creditId) {
    return fetchCredit(creditId)
        .map(this::toSnapshot);
  }

  @Override
  public Mono<CreditSnapshot> validateForCharge(String creditId, Double amount) {
    return fetchCredit(creditId)
        .flatMap(credit -> credit.getAvailableCredit() >= amount
            ? Mono.just(toSnapshot(credit))
            : Mono.error(new RuntimeException("Insufficient available credit for creditId: " + creditId)));
  }

  @Override
  public Mono<Void> applyPayment(String creditId, Double amount) {
    return webClientBuilder.build().put()
        .uri("{baseUrl}/credits/{id}/payment", creditServiceUrl, creditId)
        .bodyValue(PaymentRequest.builder()
            .creditId(creditId)
            .amount(amount)
            .build())
        .retrieve()
        .bodyToMono(Void.class);
  }

  @Override
  public Mono<Void> applyCharge(String creditId, Double amount) {
    return webClientBuilder.build().put()
        .uri("{baseUrl}/credits/{id}/charge", creditServiceUrl, creditId)
        .bodyValue(ChargeRequest.builder()
            .creditId(creditId)
            .amount(amount)
            .build())
        .retrieve()
        .bodyToMono(Void.class);
  }

  private Mono<CreditClientResponse> fetchCredit(String creditId) {
    return webClientBuilder.build().get()
        .uri("{baseUrl}/credits/{id}", creditServiceUrl, creditId)
        .retrieve()
        .onStatus(HttpStatus.NOT_FOUND::equals,
            res -> Mono.error(new RuntimeException("Credit not found: " + creditId)))
        .bodyToMono(CreditClientResponse.class);
  }

  private CreditSnapshot toSnapshot(CreditClientResponse response) {
    return CreditSnapshot.builder()
        .creditId(response.getId())
        .creditType(response.getCreditType())
        .availableCredit(response.getAvailableCredit())
        .build();
  }
}
