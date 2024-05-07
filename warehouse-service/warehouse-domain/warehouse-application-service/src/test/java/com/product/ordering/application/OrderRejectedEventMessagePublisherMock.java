package com.product.ordering.application;

import com.product.ordering.application.ports.output.publisher.OrderRejectedMessagePublisher;
import com.product.ordering.domain.event.OrderRejectedEvent;

class OrderRejectedEventMessagePublisherMock implements OrderRejectedMessagePublisher {

    @Override
    public void publish(OrderRejectedEvent domainEvent) {

    }
}
