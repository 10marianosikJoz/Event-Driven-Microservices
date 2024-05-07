package com.product.ordering.application.exception;

import com.product.ordering.domain.exception.DomainException;

public class WarehouseApplicationServiceException extends DomainException {

    public WarehouseApplicationServiceException(String message) {
        super(message);
    }
}
