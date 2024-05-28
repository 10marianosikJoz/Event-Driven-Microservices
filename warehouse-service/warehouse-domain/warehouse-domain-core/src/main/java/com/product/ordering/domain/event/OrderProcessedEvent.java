package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.time.Instant;

public final class OrderProcessedEvent extends OrderApprovalEvent {

    public OrderProcessedEvent(final OrderProcessed orderProcessed,
                               final WarehouseId warehouseId,
                               final Instant createdAt) {

        super(orderProcessed, warehouseId, createdAt);
    }
}
