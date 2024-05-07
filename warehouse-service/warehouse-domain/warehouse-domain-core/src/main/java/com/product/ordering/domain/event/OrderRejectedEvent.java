package com.product.ordering.domain.event;

import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.entity.OrderProcessed;

import java.time.Instant;

public final class OrderRejectedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher;
    private final String failureMessages;

    public OrderRejectedEvent(final OrderProcessed orderProcessed,
                              final WarehouseId warehouseId,
                              final Instant createdAt,
                              final DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher,
                              final String failureMessages) {

        super(orderProcessed, warehouseId, createdAt);
        this.orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher;
        this.failureMessages = failureMessages;
    }

    @Override
    public void run() {
        orderRejectedEventDomainEventPublisher.publish(this);
    }

    public String failureMessages() {
        return failureMessages;
    }
}
