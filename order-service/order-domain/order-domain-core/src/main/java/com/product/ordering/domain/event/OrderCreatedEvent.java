package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;

import java.time.Instant;

public final class OrderCreatedEvent extends OrderEvent {

    public OrderCreatedEvent(final Order order,
                             final Instant createdAt) {

        super(order, createdAt);
    }
}
