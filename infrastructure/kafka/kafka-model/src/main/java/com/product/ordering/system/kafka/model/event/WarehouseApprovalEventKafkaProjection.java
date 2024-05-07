package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.WarehouseApprovalMessageProjection;

import java.time.Instant;

public class WarehouseApprovalEventKafkaProjection extends MessageKafkaProjection<WarehouseApprovalMessageProjection> {

    public WarehouseApprovalEventKafkaProjection(final WarehouseApprovalMessageProjection warehouseApprovalMessageProjection,
                                                 final String itemId,
                                                 final Instant createdAt) {

        super(itemId, createdAt, warehouseApprovalMessageProjection);
    }

    public WarehouseApprovalEventKafkaProjection() {}
}
