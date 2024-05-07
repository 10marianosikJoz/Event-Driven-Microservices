package com.product.ordering.application.query;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BillfoldProjectionView(UUID billfoldId,
                                     UUID customerId,
                                     BigDecimal totalBillfoldAmount) {}
