package com.product.ordering.system;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.domain.valueobject.PaymentId;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.outbox.model.OutboxStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class InMemoryPaymentOutboxRepository implements PaymentOutboxRepository {

    private final Map<UUID, PaymentOutboxMessage> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public <G extends PaymentOutboxMessage> void save(G outboxMessage) {
        DATABASE.put(outboxMessage.getId(), outboxMessage);
    }

    @Override
    public <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        return DATABASE.values().stream()
                                .filter(outboxMessageClazz::isInstance)
                                .map(outboxMessageClazz::cast)
                                .filter(it -> it.getOutboxStatus().equals(outboxStatus))
                                .collect(Collectors.toList());
    }

    @Override
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        DATABASE.values().removeIf(it -> it.getOutboxStatus().equals(outboxStatus));
    }

    @Override
    public <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(Class<G> outboxMessageClazz,
                                                                                                                UUID sagaId,
                                                                                                                PaymentStatus paymentStatus,
                                                                                                                OutboxStatus outboxStatus) {
        return DATABASE.values().stream()
                .anyMatch(it -> outboxMessageClazz.isInstance(it)
                        && Objects.equals(it.getSagaId(), sagaId)
                        && Objects.equals(it.getPaymentStatus(), paymentStatus)
                        && Objects.equals(it.getOutboxStatus(), outboxStatus));
    }

    Optional<PaymentOutboxMessage> findLastByPaymentId(PaymentId paymentId) {
        return DATABASE.values().stream()
                                .filter(it -> it.getAggregateId().equals(paymentId.value()))
                                .max(Comparator.comparing(PaymentOutboxMessage::getCreatedAt));
    }
}
