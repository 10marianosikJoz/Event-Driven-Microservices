package com.product.ordering.application;

import com.product.ordering.application.ports.output.publisher.OrderApprovedMessagePublisher;
import com.product.ordering.domain.event.OrderApprovedEvent;

class OrderApprovedEventMessagePublisherMock implements OrderApprovedMessagePublisher {

    @Override
    public void publish(OrderApprovedEvent domainEvent) {

    }
}
