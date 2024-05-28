package com.product.ordering.system.outbox.publisher;

import com.product.ordering.system.outbox.model.OutboxMessage;

import java.util.function.Consumer;

public interface OutboxPublisher<T extends OutboxMessage> {

    void publish(T outboxMessage, Consumer<T> outboxCallback);
}
