package com.product.ordering.application.ports.input.publisher;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;

import java.util.function.Consumer;

public interface PaymentStatusMessagePublisher {

    void publish(PaymentOutboxMessage orderOutboxMessage, Consumer<PaymentOutboxMessage> outboxCallback);
}
