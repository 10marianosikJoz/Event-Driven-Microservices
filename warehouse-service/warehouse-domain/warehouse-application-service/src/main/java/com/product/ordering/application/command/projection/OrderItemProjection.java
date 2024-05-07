package com.product.ordering.application.command.projection;

import java.math.BigDecimal;

public record OrderItemProjection(String orderItemId,
                                  String productId,
                                  BigDecimal price,
                                  Integer quantity) {}
