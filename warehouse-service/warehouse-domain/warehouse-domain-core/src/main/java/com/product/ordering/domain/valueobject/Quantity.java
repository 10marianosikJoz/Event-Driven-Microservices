package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;

public record Quantity(Integer amount) {

    public Quantity {
        if (amount < 0) {
            throw new IllegalArgumentException(DomainConstants.ORDER_INCORRECT_PRODUCT_QUANTITY_VALUE);
        }
    }
}
