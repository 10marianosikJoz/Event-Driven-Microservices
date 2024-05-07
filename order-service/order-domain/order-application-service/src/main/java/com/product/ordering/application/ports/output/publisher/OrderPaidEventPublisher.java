package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderPaidEventPublisher extends DomainEventPublisher<OrderPaidEvent> {}
