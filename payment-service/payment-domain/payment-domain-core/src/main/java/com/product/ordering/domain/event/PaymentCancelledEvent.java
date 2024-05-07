package com.product.ordering.domain.event;

import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.entity.Payment;

import java.time.Instant;
import java.util.Collections;

public final class PaymentCancelledEvent extends PaymentEvent {

    private final DomainEventPublisher<PaymentEvent> paymentCancelledEventDomainEventPublisher;

    public PaymentCancelledEvent(final Payment payment,
                                 final Instant createdAt,
                                 final DomainEventPublisher<PaymentEvent> paymentCancelledEventDomainEventPublisher) {

        super(payment, createdAt, Collections.emptyList());
        this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
    }
    @Override
    public void run() {
        paymentCancelledEventDomainEventPublisher.publish(this);
    }
}
