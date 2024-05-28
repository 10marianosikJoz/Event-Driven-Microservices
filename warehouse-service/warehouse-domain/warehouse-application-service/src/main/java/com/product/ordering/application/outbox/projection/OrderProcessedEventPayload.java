package com.product.ordering.application.outbox.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.product.ordering.system.outbox.model.OutboxPayload;
import lombok.Builder;

@Builder
public record OrderProcessedEventPayload(@JsonProperty String orderId,
                                         @JsonProperty String warehouseId,
                                         @JsonProperty String orderApprovalStatus,
                                         @JsonProperty String failureMessages) implements OutboxPayload {}
