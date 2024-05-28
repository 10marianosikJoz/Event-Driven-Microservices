package com.product.ordering.system.outbox.exception;

public class OutboxException extends RuntimeException {

    public OutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
