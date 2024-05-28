package com.product.ordering.application.command.projection;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CompletePaymentCommand(UUID paymentId,
                                     UUID orderId,
                                     UUID sagaId,
                                     UUID customerId,
                                     BigDecimal price) {}
