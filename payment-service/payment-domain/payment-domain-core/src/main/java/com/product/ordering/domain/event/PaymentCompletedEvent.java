package com.product.ordering.domain.event;

import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.entity.Payment;

import java.time.Instant;
import java.util.Collections;

public final class PaymentCompletedEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentEvent> paymentCompletedEventDomainEventPublisher;

    public PaymentCompletedEvent(final Payment payment,
                                 final Instant createdAt,
                                 final DomainEventPublisher<PaymentEvent> paymentCompletedEventDomainEventPublisher) {

        super(payment, createdAt, Collections.emptyList());
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    }

    @Override
    public void run() {
        paymentCompletedEventDomainEventPublisher.publish(this);
    }
}
