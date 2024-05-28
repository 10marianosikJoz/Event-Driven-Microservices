package com.product.ordering.application.outbox.repository;

import com.product.ordering.system.outbox.model.OutboxMessage;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.saga.SagaStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderOutboxRepository {

    void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    <G extends OutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> orderCreatedOutboxMessageClazz,
                                                                                    OutboxStatus outboxStatus,
                                                                                    SagaStatus... sagaStatus);

    void save(OutboxMessage outboxMessage);

    void updateSagaStatusAndProcessAtById(SagaStatus sagaStatus,
                                          Instant processedAt,
                                          UUID id);

    <G extends OutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClazz,
                                                                                  UUID sagaId,
                                                                                  SagaStatus... sagaStatus);

    List<OutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus);
}
