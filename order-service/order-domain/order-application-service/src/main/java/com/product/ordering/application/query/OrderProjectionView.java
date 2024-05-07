package com.product.ordering.application.query;

import com.product.ordering.domain.valueobject.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record OrderProjectionView(UUID orderId,
                                  UUID customerId,
                                  UUID warehouseId,
                                  DeliveryAddressProjectionView deliveryAddress,
                                  Currency currency,
                                  Set<OrderItemProjectionView> orderItems,
                                  PaymentMethod paymentMethod,
                                  DeliveryMethod deliveryMethod,
                                  BigDecimal price,
                                  OrderStatus orderStatus,
                                  List<String> failureMessages) {}
