package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.domain.event.OrderApprovedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {}
