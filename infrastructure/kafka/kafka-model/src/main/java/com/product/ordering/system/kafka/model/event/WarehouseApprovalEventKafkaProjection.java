package com.product.ordering.system.kafka.model.event;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import com.product.ordering.system.kafka.model.projection.WarehouseApprovalMessageProjection;

import java.time.Instant;

public class WarehouseApprovalEventKafkaProjection extends MessageKafkaProjection<WarehouseApprovalMessageProjection> {

    public WarehouseApprovalEventKafkaProjection(WarehouseApprovalMessageProjection warehouseApprovalMessageProjection,
                                                 String itemId,
                                                 Instant createdAt,
                                                 String sagaId) {

        super(itemId, createdAt, warehouseApprovalMessageProjection, sagaId);
    }

    public WarehouseApprovalEventKafkaProjection() {}
}
