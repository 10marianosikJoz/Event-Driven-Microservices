package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.DomainException;

import java.util.regex.Pattern;

public record Coupon(String format, Money money) {

    private static final String COUPON_FORMAT = "[a-z]";

    public Coupon {
        var pattern = Pattern.compile(COUPON_FORMAT);
        var matcher = pattern.matcher(format);

        if (!matcher.find()) {
            throw new DomainException(DomainConstants.ORDER_INCORRECT_COUPON_FORMAT);
        }
    }
}
