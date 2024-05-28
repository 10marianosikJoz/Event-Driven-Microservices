package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.PaymentMessageProjection;

import java.time.Instant;

public class PaymentStatusEventKafkaProjection extends MessageKafkaProjection<PaymentMessageProjection> {

    public PaymentStatusEventKafkaProjection(PaymentMessageProjection paymentMessageProjection,
                                             String itemId,
                                             Instant createdAt,
                                             String sagaId) {

        super(itemId, createdAt, paymentMessageProjection, sagaId);
    }

    public PaymentStatusEventKafkaProjection() {}
}
