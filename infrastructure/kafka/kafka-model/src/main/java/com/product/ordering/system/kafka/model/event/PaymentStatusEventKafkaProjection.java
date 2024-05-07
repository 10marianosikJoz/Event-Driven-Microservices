package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.PaymentMessageProjection;

import java.time.Instant;

public class PaymentStatusEventKafkaProjection extends MessageKafkaProjection<PaymentMessageProjection> {

    public PaymentStatusEventKafkaProjection(final PaymentMessageProjection paymentMessageProjection,
                                             final String itemId,
                                             final Instant createdAt) {

        super(itemId, createdAt, paymentMessageProjection);
    }

    public PaymentStatusEventKafkaProjection() {}
}
