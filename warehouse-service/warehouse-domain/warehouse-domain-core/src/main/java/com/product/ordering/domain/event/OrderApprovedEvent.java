package com.product.ordering.domain.event;

import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.entity.OrderProcessed;

import java.time.Instant;

public final class OrderApprovedEvent extends OrderApprovalEvent {

    private final DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher;

    public OrderApprovedEvent(OrderProcessed orderProcessed,
                              WarehouseId warehouseId,
                              Instant createdAt,
                              DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher) {

        super(orderProcessed, warehouseId, createdAt);
        this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderApprovedEventDomainEventPublisher.publish(this);
    }
}
