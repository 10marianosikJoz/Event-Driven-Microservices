package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {}
