package com.product.ordering.application.outbox.repository;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.outbox.model.OutboxStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository {

    <G extends PaymentOutboxMessage> void save(G outboxMessage);

    <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus);

    void deleteByOutboxStatus(OutboxStatus outboxStatus);

    <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(Class<G> outboxMessageClazz,
                                                                                                         UUID sagaId,
                                                                                                         PaymentStatus paymentStatus,
                                                                                                         OutboxStatus outboxStatus);
}
