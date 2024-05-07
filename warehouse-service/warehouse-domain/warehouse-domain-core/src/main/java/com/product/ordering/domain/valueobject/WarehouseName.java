package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;

public record WarehouseName(String name) {

    public WarehouseName {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(DomainConstants.INCORRECT_WAREHOUSE_NAME);
        }
    }
}
