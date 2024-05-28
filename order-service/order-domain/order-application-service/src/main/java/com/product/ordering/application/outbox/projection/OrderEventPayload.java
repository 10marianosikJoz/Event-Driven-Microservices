package com.product.ordering.application.outbox.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.product.ordering.system.outbox.model.OutboxPayload;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record OrderEventPayload(@JsonProperty String orderId,
                                @JsonProperty String customerId,
                                @JsonProperty String warehouseId,
                                @JsonProperty BigDecimal price,
                                @JsonProperty String orderStatus,
                                @JsonProperty List<OrderItemPayload> orderItems,
                                @JsonProperty DeliveryAddressPayload deliveryAddress,
                                @JsonProperty String currency,
                                @JsonProperty String paymentMethod,
                                @JsonProperty String deliveryMethod,
                                @JsonProperty String failureMessages) implements OutboxPayload {}
