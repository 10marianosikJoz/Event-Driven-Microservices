package com.product.ordering.application.command.projection;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderPaidEvent(String orderId,
                             String warehouseId,
                             String sagaId,
                             BigDecimal price,
                             List<OrderItemProjection> orderItem,
                             String orderStatus,
                             Instant createdAt) {}
