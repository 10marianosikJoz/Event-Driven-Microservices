package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderCancellingEventPublisher extends DomainEventPublisher<OrderCancellingEvent> {}
