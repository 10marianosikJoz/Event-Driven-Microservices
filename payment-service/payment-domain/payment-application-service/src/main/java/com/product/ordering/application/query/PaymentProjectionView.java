package com.product.ordering.application.query;

import com.product.ordering.domain.valueobject.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record PaymentProjectionView(UUID paymentId,
                                    UUID customerId,
                                    BigDecimal price,
                                    ZonedDateTime createdAt,
                                    PaymentStatus paymentStatus) {}
