package com.product.ordering.application.command.projection;

import com.product.ordering.domain.valueobject.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Builder
public record CreateOrderCommand(@NotNull UUID customerId,
                                 @NotNull UUID warehouseId,
                                 @NotNull BigDecimal price,
                                 @NotNull Currency currency,
                                 @NotNull PaymentMethod paymentMethod,
                                 @NotNull DeliveryMethod deliveryMethod,
                                 @NotNull Coupon coupon,
                                 @NotNull Set<OrderItemCommand> orderItems,
                                 @NotNull OrderDeliveryAddressCommand address,
                                 @NotNull OrderStatus orderStatus) {}
