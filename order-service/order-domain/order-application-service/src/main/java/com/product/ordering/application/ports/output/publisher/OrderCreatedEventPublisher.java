package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderCreatedEventPublisher extends DomainEventPublisher<OrderCreatedEvent> {}
