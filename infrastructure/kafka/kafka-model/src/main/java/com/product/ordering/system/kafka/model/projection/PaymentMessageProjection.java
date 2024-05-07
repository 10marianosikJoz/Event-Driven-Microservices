package com.product.ordering.system.kafka.model.projection;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record PaymentMessageProjection(String paymentId,
                                       String customerId,
                                       String orderId,
                                       BigDecimal price,
                                       Instant createdAt,
                                       String paymentStatus,
                                       List<String> failureMessages) implements Serializable {}
