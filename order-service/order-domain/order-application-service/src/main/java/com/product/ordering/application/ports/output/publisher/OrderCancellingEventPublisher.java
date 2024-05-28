package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.application.outbox.projection.OrderCancellingOutboxMessage;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.system.outbox.publisher.OutboxPublisher;

public interface OrderCancellingEventPublisher extends OutboxPublisher<OrderCancellingOutboxMessage> {}
