package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.system.outbox.publisher.OutboxPublisher;

public interface OrderCreatedEventPublisher extends OutboxPublisher<OrderCreatedOutboxMessage> {}
