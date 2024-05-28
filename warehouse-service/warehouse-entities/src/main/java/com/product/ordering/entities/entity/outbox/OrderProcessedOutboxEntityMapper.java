package com.product.ordering.entities.entity.outbox;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.system.outbox.entity.OutboxEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessedOutboxEntityMapper extends OutboxEntityMapper<OrderProcessedOutboxEntity> {

    public OrderProcessedOutboxEntity mapOrderProcessedOutboxMessageToOrderProcessedOutboxEntity(OrderProcessedOutboxMessage outboxMessage) {
        var orderProcessedOutboxEntity = super.mapOutboxMessageToOutboxEntity(outboxMessage, OrderProcessedOutboxEntity.class);
        orderProcessedOutboxEntity.setMessageType(OrderProcessedOutboxMessage.class.getSimpleName());
        orderProcessedOutboxEntity.setOrderApprovalStatus(outboxMessage.getOrderApprovalStatus());
        return orderProcessedOutboxEntity;
    }

    public <G extends OrderProcessedOutboxMessage> G mapOrderProcessedOutboxEntityToOrderProcessedOutboxMessage(OrderProcessedOutboxEntity outboxEntity, Class<G> outboxMessageClazz) {
        G outboxMessage = super.mapOutboxEntityToOutboxMessage(outboxEntity, outboxMessageClazz);
        outboxMessage.setOrderApprovalStatus(outboxEntity.getOrderApprovalStatus());
        return outboxMessage;
    }
}
