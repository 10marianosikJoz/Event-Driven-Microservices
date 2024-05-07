package com.product.ordering.application.command.projection;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CancelPaymentCommand(UUID orderId,
                                   UUID customerId,
                                   BigDecimal price) {}
