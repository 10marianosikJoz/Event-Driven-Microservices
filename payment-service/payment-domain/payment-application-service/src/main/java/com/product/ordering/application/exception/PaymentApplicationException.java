package com.product.ordering.application.exception;

public class PaymentApplicationException extends RuntimeException {

    public PaymentApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
