package com.product.ordering.system;

import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.domain.event.PaymentEvent;

class PaymentApprovalEventMessagePublisherMock implements PaymentStatusMessagePublisher {

    @Override
    public void publish(PaymentEvent domainEvent) {

    }
}
