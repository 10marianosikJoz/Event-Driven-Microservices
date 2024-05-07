package com.product.ordering.application.command.projection;

import com.product.ordering.domain.valueobject.OrderStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderResponse(UUID orderId,
                                  OrderStatus orderStatus) {}
