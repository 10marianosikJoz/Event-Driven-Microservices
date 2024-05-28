package com.product.ordering.application;

import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.outbox.model.OutboxMessage;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.saga.SagaStatus;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class InMemoryOrderOutboxRepository implements OrderOutboxRepository {

    private final Map<UUID, OutboxMessage> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        var sagaStatusList = List.of(sagaStatus);

        DATABASE.entrySet().removeIf(it ->
                it.getValue().getOutboxStatus().equals(outboxStatus) && sagaStatusList.contains(it.getValue().getSagaStatus()));
    }

    @Override
    public <G extends OutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> outboxMessageClazz,
                                                                                           OutboxStatus outboxStatus,
                                                                                           SagaStatus... sagaStatus) {
        var sagaStatusList = List.of(sagaStatus);

        return DATABASE.values().stream()
                                .filter(outboxMessageClazz::isInstance)
                                .filter(it -> it.getOutboxStatus().equals(outboxStatus))
                                .filter(it -> sagaStatusList.contains(it.getSagaStatus()))
                                .map(outboxMessageClazz::cast)
                                .collect(Collectors.toList());
    }

    @Override
    public void save(OutboxMessage outboxMessage) {
        DATABASE.put(outboxMessage.getId(), outboxMessage);
    }

    @Override
    public void updateSagaStatusAndProcessAtById(SagaStatus sagaStatus, Instant processedAt, UUID id) {
        var outboxMessage = DATABASE.get(id);
        if (outboxMessage != null) {
            outboxMessage.setSagaStatus(sagaStatus);
            outboxMessage.setProcessedAt(processedAt);
        }
    }

    @Override
    public <G extends OutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClazz,
                                                                                         UUID sagaId,
                                                                                         SagaStatus... sagaStatus) {
        var sagaStatusList = List.of(sagaStatus);

        return DATABASE.values().stream()
                                .filter(outboxMessageClazz::isInstance)
                                .filter(it -> it.getSagaId().equals(sagaId))
                                .filter(it -> sagaStatusList.contains(it.getSagaStatus()))
                                .map(outboxMessageClazz::cast)
                                .findFirst();
    }

    @Override
    public List<OutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        var sagaStatusList = List.of(sagaStatus);
        return DATABASE.values().stream()
                                .filter(it -> it.getSagaId().equals(sagaId))
                                .filter(it -> sagaStatusList.contains(it.getSagaStatus()))
                                .collect(Collectors.toList());
    }

    public Optional<OrderCreatedOutboxMessage> findByOrderId(OrderId orderId) {
        return DATABASE.values().stream()
                                .filter(OrderCreatedOutboxMessage.class::isInstance)
                                .filter(it -> it.getAggregateId().equals(orderId.value()))
                                .map(OrderCreatedOutboxMessage.class::cast)
                                .findFirst();
    }
}
