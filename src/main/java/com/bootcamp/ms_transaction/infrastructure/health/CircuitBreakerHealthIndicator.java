package com.bootcamp.ms_transaction.infrastructure.health;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("circuitBreakerHealth")
public class CircuitBreakerHealthIndicator implements HealthIndicator {
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  public CircuitBreakerHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public Health health() {
    CircuitBreaker accountValidationCb = circuitBreakerRegistry
        .find("account-validation")
        .orElse(null);

    if (accountValidationCb == null) {
      return Health.unknown()
          .withDetail("message", "Circuit Breaker 'account-validation' not found")
          .build();
    }

    CircuitBreaker.State state = accountValidationCb.getState();

    Health.Builder builder = new Health.Builder();

    if (state == CircuitBreaker.State.CLOSED) {
      builder.up();
    } else if (state == CircuitBreaker.State.HALF_OPEN) {
      builder.status("DEGRADED");
    } else {
      builder.down();
    }

    return builder
        .withDetail("service", "Account Validation Service")
        .withDetail("circuitBreakerState", state.toString())
        .withDetail("failureThreshold", "50%")
        .withDetail("slidingWindowSize", "10 calls")
        .withDetail("description", "Monitors connectivity to Account Service. " +
            "CLOSED=healthy, HALF_OPEN=recovering, OPEN=service down")
        .build();
  }
}
