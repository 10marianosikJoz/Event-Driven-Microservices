package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Order;

import java.time.Instant;

public final class OrderPaidEvent extends OrderEvent {

    public OrderPaidEvent(final Order order,
                          final Instant createdAt) {

        super(order, createdAt);
    }
}
