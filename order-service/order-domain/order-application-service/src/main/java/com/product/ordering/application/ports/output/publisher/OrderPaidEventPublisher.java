package com.product.ordering.application.ports.output.publisher;

import com.product.ordering.application.outbox.projection.OrderPaidOutboxMessage;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.system.outbox.publisher.OutboxPublisher;

public interface OrderPaidEventPublisher extends OutboxPublisher<OrderPaidOutboxMessage> {}
