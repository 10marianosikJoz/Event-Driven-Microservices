package com.product.ordering.application.outbox.repository;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.system.outbox.model.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface OrderProcessedOutboxRepository {

    <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage);

    <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus);

    <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClazz,
                                                                                        UUID sagaId,
                                                                                        OutboxStatus outboxStatus);

    <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus);
}
