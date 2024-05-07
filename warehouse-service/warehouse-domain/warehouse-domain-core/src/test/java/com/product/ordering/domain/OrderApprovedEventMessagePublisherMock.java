package com.product.ordering.domain;

import com.product.ordering.domain.event.OrderApprovedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderApprovedEventMessagePublisherMock implements DomainEventPublisher<OrderApprovedEvent> {

    @Override
    public void publish(OrderApprovedEvent domainEvent) {

    }
}
