package com.product.ordering.application.query;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemProjectionView(Long orderItemId,
                                      UUID orderId,
                                      Integer quantity,
                                      UUID productId,
                                      BigDecimal price,
                                      BigDecimal subtotal) {}
