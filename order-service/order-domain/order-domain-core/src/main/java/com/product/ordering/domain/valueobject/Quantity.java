package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.DomainException;

public record Quantity(int quantity) {

    public Quantity {
        if (quantity < 0) {
            throw new DomainException(DomainConstants.ORDER_INCORRECT_PRODUCT_QUANTITY_VALUE);
        }
    }
}
