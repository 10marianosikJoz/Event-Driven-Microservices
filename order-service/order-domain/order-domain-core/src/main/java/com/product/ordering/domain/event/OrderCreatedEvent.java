package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

import java.time.Instant;

public final class OrderCreatedEvent extends OrderEvent {

    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher;

    public OrderCreatedEvent(final Order order,
                             final Instant createdAt,
                             final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {

        super(order, createdAt);
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderCreatedEventDomainEventPublisher.publish(this);
    }
}
