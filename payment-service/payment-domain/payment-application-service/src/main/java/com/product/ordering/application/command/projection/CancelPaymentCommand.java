package com.product.ordering.application.command.projection;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CancelPaymentCommand(UUID paymentId,
                                   UUID orderId,
                                   UUID customerId,
                                   UUID sagaId,
                                   BigDecimal price) {}
