package com.product.ordering.domain;

import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderCancelledEventMessagePublisherMock implements DomainEventPublisher<OrderCancellingEvent> {

    @Override
    public void publish(OrderCancellingEvent domainEvent) {

    }
}
