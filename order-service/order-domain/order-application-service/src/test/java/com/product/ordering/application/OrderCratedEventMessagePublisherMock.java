package com.product.ordering.application;

import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderCratedEventMessagePublisherMock implements DomainEventPublisher<OrderCreatedEvent> {

    @Override
    public void publish(OrderCreatedEvent domainEvent) {

    }
}
