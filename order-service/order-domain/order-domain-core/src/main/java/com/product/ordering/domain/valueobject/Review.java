package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.DomainException;

public record Review(int rating, String description) {

    public Review {
        if (rating < 0) {
            throw new DomainException(DomainConstants.ORDER_PRODUCT_INCORRECT_RATING_NUMBER);
        }

        if (description.isBlank()) {
            throw new DomainException(DomainConstants.ORDER_PRODUCT_DESCRIPTION_MISSED);
        }
    }
}
