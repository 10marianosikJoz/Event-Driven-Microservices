package com.product.ordering.application.command.projection;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderDeliveryAddressCommand(@NotNull @Max(value = 20) String street,
                                          @NotNull @Max(value = 20) String postalCode,
                                          @NotNull @Max(value = 20) String city) {}
