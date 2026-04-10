package org.erick.orderproducer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        boolean simulatePaymentFailure
) {
}
