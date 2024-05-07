package com.product.ordering.system.kafka.model.projection;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
public record OrderItemMessageProjection(String orderItemId,
                                         String productId,
                                         Integer quantity,
                                         BigDecimal price) implements Serializable {}
