package com.product.ordering.system.outbox.entity;

import com.product.ordering.system.outbox.exception.OutboxException;
import com.product.ordering.system.outbox.model.OutboxMessage;
import com.product.ordering.system.outbox.model.OutboxPayload;
import org.springframework.beans.factory.annotation.Autowired;

public class OutboxEntityMapper<E extends OutboxEntity> {

    @Autowired
    private OutboxPayloadHelper outboxPayloadHelper;

    public <P extends OutboxPayload, G extends OutboxMessage> G mapOutboxEntityToOutboxMessage(E outboxEntity, Class<G> outboxMessageClazz) {
        G outboxMessage;
        Class<P> payloadClazz;

        try {
            outboxMessage = outboxMessageClazz.getDeclaredConstructor().newInstance();
            payloadClazz = (Class<P>) Class.forName(outboxEntity.getPayloadType());
        } catch (Exception e) {
            throw new OutboxException("Unable to map: " + outboxEntity.getClass().getName() + " to: " + outboxMessageClazz.getName(), e);
        }

        outboxMessage.setId(outboxEntity.getId());
        outboxMessage.setSagaId(outboxEntity.getSagaId());
        outboxMessage.setCreatedAt(outboxEntity.getCreatedAt());
        outboxMessage.setProcessedAt(outboxEntity.getProcessedAt());
        outboxMessage.setPayload(outboxPayloadHelper.extractMessagePayloadFromJsonNode(outboxEntity.getPayload(), payloadClazz));
        outboxMessage.setAggregateId(outboxEntity.getAggregateId());
        outboxMessage.setOutboxStatus(outboxEntity.getOutboxStatus());
        outboxMessage.setVersion(outboxEntity.getVersion());

        return outboxMessage;
    }

    public <G extends OutboxMessage> E mapOutboxMessageToOutboxEntity(G outboxMessage, Class<E> entityOutboxClazz) {
        E entity;

        try {
            entity = entityOutboxClazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new OutboxException("Unable to map: " + entityOutboxClazz.getName() + " to: " + outboxMessage.getClass().getName(), e);
        }

        entity.setId(outboxMessage.getId());
        entity.setSagaId(outboxMessage.getSagaId());
        entity.setCreatedAt(outboxMessage.getCreatedAt());
        entity.setProcessedAt(outboxMessage.getProcessedAt());
        entity.setPayload(outboxPayloadHelper.mapMessagePayloadToJsonNode(outboxMessage.getPayload()));
        entity.setPayloadType(outboxMessage.getPayload().getClass().getName());
        entity.setAggregateId(outboxMessage.getAggregateId());
        entity.setOutboxStatus(outboxMessage.getOutboxStatus());
        entity.setVersion(outboxMessage.getVersion());

        return entity;
    }
}
