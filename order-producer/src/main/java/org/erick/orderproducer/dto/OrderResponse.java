package org.erick.orderproducer.dto;

import java.math.BigDecimal;

import org.erick.orderproducer.messaging.status.OrderStatus;

public record OrderResponse(
        String orderId,
        String eventId,
        String customerId,
        BigDecimal amount,
        OrderStatus status,
        String message
) {
}
