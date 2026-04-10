package org.erick.paymentconsumer.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ProcessedEventRegistry {

    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean isProcessed(String eventId) {
        return processedEventIds.contains(eventId);
    }

    public void markProcessed(String eventId) {
        processedEventIds.add(eventId);
    }
}
