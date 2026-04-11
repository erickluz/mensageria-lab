package org.erick.notificationconsumer.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

import org.erick.notificationconsumer.messaging.status.PaymentStatus;

public record PaymentProcessedEvent(
        String eventId,
        String orderId,
        String customerId,
        BigDecimal amount,
        PaymentStatus status,
        Instant processedAt
) {
}
