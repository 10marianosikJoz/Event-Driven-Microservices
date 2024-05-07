package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

import java.time.Instant;

public final class OrderCancellingEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCancellingEvent> orderCancelledEventDomainEventPublisher;

    public OrderCancellingEvent(final Order order,
                                final Instant createdAt,
                                final DomainEventPublisher<OrderCancellingEvent> orderCancelledEventDomainEventPublisher) {

        super(order, createdAt);
        this.orderCancelledEventDomainEventPublisher = orderCancelledEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderCancelledEventDomainEventPublisher.publish(this);
    }
}
