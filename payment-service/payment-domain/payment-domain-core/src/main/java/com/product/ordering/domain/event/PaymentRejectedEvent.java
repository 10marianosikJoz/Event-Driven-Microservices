package com.product.ordering.domain.event;

import com.product.ordering.domain.entity.Payment;

import java.time.Instant;
import java.util.List;

public final class PaymentRejectedEvent extends PaymentEvent {

    public PaymentRejectedEvent(final Payment payment,
                                final Instant createdAt,
                                final List<String> failureMessages) {

        super(payment, createdAt, failureMessages);
    }
}
