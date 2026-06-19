package com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest;

import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.request.*;
import com.bootcamp.ms_transaction.infrastructure.adapters.inbound.rest.response.*;
import com.bootcamp.ms_transaction.application.ports.input.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateDepositUseCase createDepositUseCase;
    private final CreateWithdrawalUseCase createWithdrawalUseCase;
    private final CreateCreditPaymentUseCase createCreditPaymentUseCase;
    private final CreateCardChargeUseCase createCardChargeUseCase;
    private final FindTransactionUseCase findTransactionUseCase;
    private final TransactionDtoMapper mapper;

    @PostMapping("/deposits")
    public Mono<org.springframework.http.ResponseEntity<TransactionResponse>> createDeposit(
            @Valid @RequestBody DepositRequest request) {
        return createDepositUseCase.createDeposit(request.getAccountId(), request.getAmount())
                .map(transaction -> {
                    HttpStatus status = transaction.getStatus().name().equals("PENDING")
                            ? HttpStatus.ACCEPTED
                            : HttpStatus.CREATED;
                    return org.springframework.http.ResponseEntity.status(status)
                            .body(mapper.toResponse(transaction));
                });
    }

    @PostMapping("/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> createWithdrawal(@Valid @RequestBody WithdrawalRequest request) {
        return createWithdrawalUseCase.createWithdrawal(request.getAccountId(), request.getAmount())
                .map(mapper::toResponse);
    }

    @PostMapping("/credit-payments")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> createCreditPayment(@Valid @RequestBody CreditPaymentRequest request) {
        return createCreditPaymentUseCase.createCreditPayment(request.getCreditId(), request.getAmount())
                .map(mapper::toResponse);
    }

    @PostMapping("/card-charges")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TransactionResponse> createCardCharge(@Valid @RequestBody CardChargeRequest request) {
        return createCardChargeUseCase
                .createCardCharge(request.getCreditId(), request.getAmount(), request.getDescription())
                .map(mapper::toResponse);
    }

    @GetMapping("/product/{productId}")
    public Flux<TransactionResponse> findByProduct(@PathVariable String productId) {
        return findTransactionUseCase.findByProductId(productId)
                .map(mapper::toResponse);
    }

    @GetMapping("/{id}")
    public Mono<TransactionResponse> findById(@PathVariable String id) {
        return findTransactionUseCase.findById(id)
                .map(mapper::toResponse);
    }
}
