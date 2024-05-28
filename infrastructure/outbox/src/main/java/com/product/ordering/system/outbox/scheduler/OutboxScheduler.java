package com.product.ordering.system.outbox.scheduler;

public interface OutboxScheduler {

    void processOutboxMessage();
}
