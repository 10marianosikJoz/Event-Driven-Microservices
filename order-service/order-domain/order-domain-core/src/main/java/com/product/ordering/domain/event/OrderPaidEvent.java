package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

import java.time.Instant;

public final class OrderPaidEvent extends OrderEvent {

    private final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher;

    public OrderPaidEvent(final Order order,
                          final Instant createdAt,
                          final DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {

        super(order, createdAt);
        this.orderPaidEventDomainEventPublisher = orderPaidEventDomainEventPublisher;
    }

    @Override
    public void run() {
        orderPaidEventDomainEventPublisher.publish(this);
    }
}
