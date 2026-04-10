package org.erick.shared.event;

import java.math.BigDecimal;
import java.time.Instant;

import org.erick.shared.status.PaymentStatus;

public record PaymentProcessedEvent(
        String eventId,
        String orderId,
        String customerId,
        BigDecimal amount,
        PaymentStatus status,
        Instant processedAt
) {
}
