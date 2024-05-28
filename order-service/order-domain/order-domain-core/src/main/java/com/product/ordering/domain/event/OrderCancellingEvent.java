package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;

import java.time.Instant;

public final class OrderCancellingEvent extends OrderEvent {

    public OrderCancellingEvent(final Order order,
                                final Instant createdAt) {

        super(order, createdAt);
    }
}
