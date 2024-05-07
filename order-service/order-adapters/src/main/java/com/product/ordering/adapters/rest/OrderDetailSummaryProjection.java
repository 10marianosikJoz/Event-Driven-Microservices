package com.product.ordering.adapters.rest;

import com.product.ordering.domain.valueobject.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
record OrderDetailSummaryProjection(UUID orderId,
                                    UUID customerId,
                                    UUID warehouseId,
                                    DeliveryAddressProjection deliveryAddress,
                                    Currency currency,
                                    Set<OrderItemProjection> orderItems,
                                    PaymentMethod paymentMethod,
                                    DeliveryMethod deliveryMethod,
                                    BigDecimal price,
                                    OrderStatus orderStatus,
                                    List<String> failureMessages) {

    record DeliveryAddressProjection(UUID id,
                                     String street,
                                     String postalCode,
                                     String city
    ) {}

    record OrderItemProjection(UUID productId,
                               Integer quantity,
                               BigDecimal price,
                               BigDecimal totalPrice) {}
}
