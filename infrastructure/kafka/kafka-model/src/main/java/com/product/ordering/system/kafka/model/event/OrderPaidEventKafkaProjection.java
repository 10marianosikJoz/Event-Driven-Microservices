package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.OrderMessageProjection;

import java.time.Instant;

public class OrderPaidEventKafkaProjection extends MessageKafkaProjection<OrderMessageProjection> {

    public OrderPaidEventKafkaProjection(OrderMessageProjection orderMessageProjection,
                                         String itemId,
                                         Instant createdAt,
                                         String sagaId) {

        super(itemId, createdAt, orderMessageProjection, sagaId);
    }

    public OrderPaidEventKafkaProjection() {}
}
