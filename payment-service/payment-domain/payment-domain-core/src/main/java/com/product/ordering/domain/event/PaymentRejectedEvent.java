package com.product.ordering.domain.event;

import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.entity.Payment;

import java.time.Instant;
import java.util.List;

public final class PaymentRejectedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentEvent> paymentRejectedEventDomainEventPublisher;

    public PaymentRejectedEvent(final Payment payment,
                                final Instant createdAt,
                                final List<String> failureMessages,
                                DomainEventPublisher<PaymentEvent> paymentRejectedEventDomainEventPublisher) {

        super(payment, createdAt, failureMessages);
        this.paymentRejectedEventDomainEventPublisher = paymentRejectedEventDomainEventPublisher;
    }

    @Override
    public void run() {
        paymentRejectedEventDomainEventPublisher.publish(this);
    }
}
