package com.product.ordering.domain;

import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

class OrderPaidEventMessagePublisherMock implements DomainEventPublisher<OrderPaidEvent> {

    @Override
    public void publish(OrderPaidEvent domainEvent) {

    }
}
