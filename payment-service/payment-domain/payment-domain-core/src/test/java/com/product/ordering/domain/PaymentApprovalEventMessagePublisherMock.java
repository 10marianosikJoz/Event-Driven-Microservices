package com.product.ordering.domain;

import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class PaymentApprovalEventMessagePublisherMock implements DomainEventPublisher<PaymentEvent> {

    @Override
    public void publish(PaymentEvent domainEvent) {

    }
}
