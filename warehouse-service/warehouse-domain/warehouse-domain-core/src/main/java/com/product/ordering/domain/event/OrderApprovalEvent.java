package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.time.Instant;

public abstract sealed class OrderApprovalEvent implements DomainEvent<OrderApprovalEvent> permits OrderApprovedEvent,
                                                                                                   OrderRejectedEvent,
                                                                                                   OrderProcessedEvent {

    private final OrderProcessed orderProcessed;
    private final WarehouseId warehouseId;
    private final Instant createdAt;

    OrderApprovalEvent(final OrderProcessed orderProcessed,
                       final WarehouseId warehouseId,
                       final Instant createdAt) {

        this.orderProcessed = orderProcessed;
        this.warehouseId = warehouseId;
        this.createdAt = createdAt;
    }

    public OrderProcessed orderProcessed() {
        return orderProcessed;
    }

    public WarehouseId warehouseId() {
        return warehouseId;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
