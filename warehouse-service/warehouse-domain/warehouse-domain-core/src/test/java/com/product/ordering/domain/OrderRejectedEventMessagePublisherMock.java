package com.product.ordering.domain;

import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderRejectedEventMessagePublisherMock implements DomainEventPublisher<OrderRejectedEvent> {

    @Override
    public void publish(OrderRejectedEvent domainEvent) {

    }
}
