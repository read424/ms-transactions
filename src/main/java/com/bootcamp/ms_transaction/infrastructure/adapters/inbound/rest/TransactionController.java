package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest;

import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.api.TransactionsApi;
import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.models.*;
import com.bootcamp.ms_transaction.application.ports.input.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionsApi {

    private final CreateDepositUseCase createDepositUseCase;
    private final CreateWithdrawalUseCase createWithdrawalUseCase;
    private final CreateCreditPaymentUseCase createCreditPaymentUseCase;
    private final CreateCardChargeUseCase createCardChargeUseCase;
    private final FindTransactionUseCase findTransactionUseCase;
    private final TransactionDtoMapper mapper;

    @Override
    public Mono<ResponseEntity<TransactionResponse>> createDeposit(
            Mono<DepositRequest> depositRequest,
            ServerWebExchange exchange) {
        return depositRequest.flatMap(request ->
                createDepositUseCase.createDeposit(request.getAccountId(), request.getAmount())
                        .map(transaction -> {
                            HttpStatus status = transaction.getStatus().name().equals("PENDING")
                                    ? HttpStatus.ACCEPTED
                                    : HttpStatus.CREATED;
                            return ResponseEntity.status(status)
                                    .body(mapper.toResponse(transaction));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> createWithdrawal(
            Mono<WithdrawalRequest> withdrawalRequest,
            ServerWebExchange exchange) {
        return withdrawalRequest.flatMap(request ->
                createWithdrawalUseCase.createWithdrawal(request.getAccountId(), request.getAmount())
                        .map(transaction -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(mapper.toResponse(transaction)))
        );
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> createCreditPayment(
            Mono<CreditPaymentRequest> creditPaymentRequest,
            ServerWebExchange exchange) {
        return creditPaymentRequest.flatMap(request ->
                createCreditPaymentUseCase.createCreditPayment(request.getCreditId(), request.getAmount())
                        .map(transaction -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(mapper.toResponse(transaction)))
        );
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> createCardCharge(
            Mono<CardChargeRequest> cardChargeRequest,
            ServerWebExchange exchange) {
        return cardChargeRequest.flatMap(request ->
                createCardChargeUseCase.createCardCharge(request.getCreditId(), request.getAmount(), request.getDescription())
                        .map(transaction -> ResponseEntity.status(HttpStatus.CREATED)
                                .body(mapper.toResponse(transaction)))
        );
    }

    @Override
    public Mono<ResponseEntity<TransactionResponse>> findTransactionById(
            String id,
            ServerWebExchange exchange) {
        return findTransactionUseCase.findById(id)
                .map(transaction -> ResponseEntity.ok(mapper.toResponse(transaction)));
    }

    @Override
    public Mono<ResponseEntity<Flux<TransactionResponse>>> findTransactionsByProduct(
            String productId,
            ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(
                findTransactionUseCase.findByProductId(productId)
                        .map(mapper::toResponse)
        ));
    }
}
