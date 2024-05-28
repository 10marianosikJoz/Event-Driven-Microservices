package com.product.ordering.application;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderProcessedOutboxRepository;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.outbox.model.OutboxStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class InMemoryOrderProcessedOutboxRepository implements OrderProcessedOutboxRepository {

    private final Map<UUID, OrderProcessedOutboxMessage> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage) {
        DATABASE.put(orderOutboxMessage.getId(), orderOutboxMessage);
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        return DATABASE.values().stream()
                                .filter(outboxMessageClazz::isInstance)
                                .map(outboxMessageClazz::cast)
                                .filter(it -> it.getOutboxStatus().equals(outboxStatus))
                                .collect(Collectors.toList());
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClazz,
                                                                                               UUID sagaId,
                                                                                               OutboxStatus outboxStatus) {
        return DATABASE.values().stream()
                .anyMatch(it -> outboxMessageClazz.isInstance(it)
                        && Objects.equals(it.getSagaId(), sagaId)
                        && Objects.equals(it.getOutboxStatus(), outboxStatus));
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        DATABASE.values().removeIf(it -> outboxMessageClazz.isInstance(it) && it.getOutboxStatus().equals(outboxStatus));
    }

    Optional<OrderProcessedOutboxMessage> findByOrderProcessedId(OrderId orderId) {
        return DATABASE.values().stream()
                                .filter(it -> it.getAggregateId().equals(orderId.value()))
                                .map(OrderProcessedOutboxMessage.class::cast)
                                .findFirst();
    }
}
