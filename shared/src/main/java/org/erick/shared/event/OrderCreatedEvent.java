package org.erick.shared.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        String eventId,
        String orderId,
        String customerId,
        BigDecimal amount,
        Instant createdAt,
        boolean simulatePaymentFailure
) {
}
