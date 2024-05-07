package com.product.ordering.system.kafka.model.projection;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Builder
public record OrderMessageProjection(String orderId,
                                     String customerId,
                                     String warehouseId,
                                     DeliveryAddressMessageProjection deliveryAddress,
                                     String currency,
                                     Set<OrderItemMessageProjection> orderItems,
                                     String paymentMethod,
                                     String deliveryMethod,
                                     BigDecimal price,
                                     String orderStatus,
                                     Instant createdAt,
                                     List<String> failureMessages) implements Serializable {}
