package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;

import java.util.function.Consumer;

public interface OrderApprovedMessagePublisher {

    void publish(OrderProcessedOutboxMessage orderOutboxMessage, Consumer<OrderProcessedOutboxMessage> outboxCallback);
}
