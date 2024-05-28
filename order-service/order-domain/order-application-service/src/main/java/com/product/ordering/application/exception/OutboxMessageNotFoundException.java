package com.product.ordering.application.exception;

public class OutboxMessageNotFoundException extends RuntimeException {

    public OutboxMessageNotFoundException(String message) {
        super(message);
    }
}
