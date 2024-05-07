package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Payment;

import java.time.Instant;
import java.util.List;

public abstract sealed class PaymentEvent implements DomainEvent<Payment> permits PaymentCancelledEvent,
                                                                                  PaymentCompletedEvent,
                                                                                  PaymentRejectedEvent {
    private final Payment payment;
    private final Instant createdAt;
    private final List<String> failureMessages;

   PaymentEvent(final Payment payment,
                final Instant createdAt,
                final List<String> failureMessages) {

        this.payment = payment;
        this.createdAt = createdAt;
        this.failureMessages = failureMessages;
    }

    public Payment payment() {
        return payment;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public List<String> failureMessages() {
        return failureMessages;
    }
}
