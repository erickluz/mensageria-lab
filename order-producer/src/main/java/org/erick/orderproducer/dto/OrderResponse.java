package org.erick.orderproducer.dto;

import java.math.BigDecimal;

import org.erick.shared.status.OrderStatus;

public record OrderResponse(
        String orderId,
        String eventId,
        String customerId,
        BigDecimal amount,
        OrderStatus status,
        String message
) {
}
