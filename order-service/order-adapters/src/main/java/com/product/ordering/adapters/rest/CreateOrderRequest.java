package com.product.ordering.adapters.rest;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

record CreateOrderRequest(@NotNull UUID customerId,
                          @NotNull UUID warehouseId,
                          @NotNull CreateOrderDeliveryAddressRequest deliveryAddress,
                          @NotNull String currency,
                          @NotNull Set<CreateOrderItemRequest> orderItems,
                          @NotNull String paymentMethod,
                          @NotNull String deliveryMethod,
                          @NotNull BigDecimal price,
                          @NotNull String coupon) {


    record CreateOrderItemRequest(@NotNull Long orderItemId,
                                  @NotNull UUID productId,
                                  @NotNull Integer quantity,
                                  @NotNull BigDecimal price,
                                  @NotNull BigDecimal subtotal) {}

    record CreateOrderDeliveryAddressRequest(@NotNull String street,
                                             @NotNull String postalCode,
                                             @NotNull String city) {}
}
