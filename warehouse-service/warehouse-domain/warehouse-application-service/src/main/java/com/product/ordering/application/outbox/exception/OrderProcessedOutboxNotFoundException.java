package com.product.ordering.application.outbox.exception;

public class OrderProcessedOutboxNotFoundException extends RuntimeException {

    public OrderProcessedOutboxNotFoundException(String message) {
        super(message);
    }
}
