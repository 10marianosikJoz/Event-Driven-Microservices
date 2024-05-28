package com.product.ordering.application.outbox.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.product.ordering.system.outbox.model.OutboxPayload;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentStatusEventPayload(@JsonProperty String paymentId,
                                        @JsonProperty String customerId,
                                        @JsonProperty String orderId,
                                        @JsonProperty BigDecimal price,
                                        @JsonProperty Instant createdAt,
                                        @JsonProperty String paymentStatus,
                                        @JsonProperty String failureMessages) implements OutboxPayload {}
