package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

import java.time.Instant;

public final class OrderCancellingEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCancellingEvent> orderCancellingEventDomainEventPublisher;

    public OrderCancellingEvent(final Order order,
                                final Instant createdAt,
                                final DomainEventPublisher<OrderCancellingEvent> orderCancellingEventDomainEventPublisher) {

        super(order, createdAt);
        this.orderCancellingEventDomainEventPublisher = orderCancellingEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderCancellingEventDomainEventPublisher.publish(this);
    }
}
