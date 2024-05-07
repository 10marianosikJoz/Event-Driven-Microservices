package com.product.ordering.domain;

import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderCreatedEventMessagePublisherMock implements DomainEventPublisher<OrderCreatedEvent> {

    @Override
    public void publish(OrderCreatedEvent domainEvent) {

    }
}
