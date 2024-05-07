package com.product.ordering.application.ports.input.publisher;

import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

public interface PaymentStatusMessagePublisher extends DomainEventPublisher<PaymentEvent> {}
