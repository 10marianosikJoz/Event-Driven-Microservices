package com.product.ordering.domain.event;

import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.entity.OrderProcessed;

import java.time.Instant;

public final class OrderApprovedEvent extends OrderApprovalEvent {

    public OrderApprovedEvent(OrderProcessed orderProcessed,
                              WarehouseId warehouseId,
                              Instant createdAt) {

        super(orderProcessed, warehouseId, createdAt);
    }
}
