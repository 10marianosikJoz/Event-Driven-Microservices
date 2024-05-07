package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.DomainException;

public record Description(String description) {

    public Description {
        if (description == null || description.isEmpty()) {
            throw new DomainException(DomainConstants.ORDER_PRODUCT_DESCRIPTION_MISSED);
        }
    }
}
