package com.product.ordering.domain.event;

import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.entity.OrderProcessed;

import java.time.Instant;

public final class OrderRejectedEvent extends OrderApprovalEvent {

    private final String failureMessages;

    public OrderRejectedEvent(final OrderProcessed orderProcessed,
                              final WarehouseId warehouseId,
                              final Instant createdAt,
                              final String failureMessages) {

        super(orderProcessed, warehouseId, createdAt);
        this.failureMessages = failureMessages;
    }

    public String failureMessages() {
        return failureMessages;
    }
}
