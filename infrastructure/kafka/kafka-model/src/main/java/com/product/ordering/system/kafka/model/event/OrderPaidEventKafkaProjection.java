package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.OrderMessageProjection;

import java.time.Instant;

public class OrderPaidEventKafkaProjection extends MessageKafkaProjection<OrderMessageProjection> {

    public OrderPaidEventKafkaProjection(final OrderMessageProjection orderMessageProjection,
                                         final String itemId,
                                         final Instant createdAt) {

        super(itemId, createdAt, orderMessageProjection);
    }

    public OrderPaidEventKafkaProjection() {}
}
