package com.product.ordering.entities.entity.outbox;

import com.product.ordering.system.outbox.entity.OutboxEntityMapper;
import com.product.ordering.system.outbox.model.OutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxEntityMapper extends OutboxEntityMapper<OrderOutboxEntity> {

    public <G extends OutboxMessage> OrderOutboxEntity mapOrderOutboxMessageToOrderOutboxEntity(G outboxMessage) {
        var orderOutboxEntity = super.mapOutboxMessageToOutboxEntity(outboxMessage, OrderOutboxEntity.class);
        orderOutboxEntity.setProcessedAt(outboxMessage.getProcessedAt());
        orderOutboxEntity.setMessageType(outboxMessage.getClass().getSimpleName());
        orderOutboxEntity.setSagaStatus(outboxMessage.getSagaStatus());

        return orderOutboxEntity;
    }

    public <G extends OutboxMessage> G mapOrderOutboxEntityToOrderOutboxMessage(OrderOutboxEntity orderOutboxEntity, Class<G> outboxMessageClazz) {
        var outboxMessage = super.mapOutboxEntityToOutboxMessage(orderOutboxEntity, outboxMessageClazz);
        outboxMessage.setProcessedAt(orderOutboxEntity.getProcessedAt());
        outboxMessage.setSagaStatus(orderOutboxEntity.getSagaStatus());

        return outboxMessage;
    }
}
