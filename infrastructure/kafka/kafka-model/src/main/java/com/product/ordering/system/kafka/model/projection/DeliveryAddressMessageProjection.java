package com.product.ordering.system.kafka.model.projection;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record DeliveryAddressMessageProjection(String deliveryAddressId,
                                               String street,
                                               String postalCode,
                                               String city) implements Serializable {}
