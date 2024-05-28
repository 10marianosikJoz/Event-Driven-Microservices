package com.product.ordering.application.outbox.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record DeliveryAddressPayload(@JsonProperty String deliveryAddressId,
                                     @JsonProperty String street,
                                     @JsonProperty String postalCode,
                                     @JsonProperty String city) {}