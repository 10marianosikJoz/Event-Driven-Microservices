package com.product.ordering.application.command.projection;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Builder
public record OrderItemCommand(@NotNull Long orderItemId,
                               @NotNull UUID productId,
                               @NotNull Integer quantity,
                               @NotNull BigDecimal price,
                               @NotNull BigDecimal subtotal) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (OrderItemCommand) o;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
