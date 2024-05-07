package com.product.ordering.application.query;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryAddressProjectionView(UUID id,
                                            String street,
                                            String postalCode,
                                            String city) {}
