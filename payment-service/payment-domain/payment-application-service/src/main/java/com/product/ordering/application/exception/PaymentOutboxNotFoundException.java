package com.product.ordering.application.exception;

public class PaymentOutboxNotFoundException extends RuntimeException {

    public PaymentOutboxNotFoundException(String message) {
        super(message);
    }
}
