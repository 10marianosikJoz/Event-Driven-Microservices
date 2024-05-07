package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.time.Instant;

public final class OrderProcessedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderProcessedEvent> orderProcessedEventDomainEventPublisher;

    public OrderProcessedEvent(final OrderProcessed orderProcessed,
                               final WarehouseId warehouseId,
                               final Instant createdAt,
                               final DomainEventPublisher<OrderProcessedEvent> orderProcessedEventDomainEventPublisher) {

        super(orderProcessed, warehouseId, createdAt);
        this.orderProcessedEventDomainEventPublisher = orderProcessedEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderProcessedEventDomainEventPublisher.publish(this);
    }
}
