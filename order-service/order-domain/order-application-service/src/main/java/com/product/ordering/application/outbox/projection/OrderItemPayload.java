package com.product.ordering.application.outbox.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemPayload(@JsonProperty String orderItemId,
                               @JsonProperty Integer quantity,
                               @JsonProperty String productId,
                               @JsonProperty BigDecimal price,
                               @JsonProperty BigDecimal subtotal) {}