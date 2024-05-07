package com.product.ordering.system.kafka.model.projection;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record WarehouseApprovalMessageProjection(String warehouseId,
                                                 String orderId,
                                                 String orderApprovalStatus,
                                                 List<String> failureMessages) implements Serializable {}
