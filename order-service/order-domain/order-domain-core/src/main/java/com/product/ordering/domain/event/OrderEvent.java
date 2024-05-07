package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;

import java.time.Instant;

abstract sealed class OrderEvent implements DomainEvent<Order> permits OrderCancellingEvent,
                                                                       OrderPaidEvent,
                                                                       OrderCreatedEvent {

    private final Order order;
    private final Instant createdAt;

    OrderEvent(final Order order, final Instant createdAt) {
        this.order = order;
        this.createdAt = createdAt;
    }

    public Order getOrder() {
        return order;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
