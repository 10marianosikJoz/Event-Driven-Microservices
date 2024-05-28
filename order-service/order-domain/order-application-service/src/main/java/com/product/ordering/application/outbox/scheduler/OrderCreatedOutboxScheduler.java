package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.publisher.OrderCreatedEventPublisher;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import com.product.ordering.system.saga.SagaStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderCreatedOutboxScheduler implements OutboxScheduler {

    private final OrderCreatedEventPublisher orderCreatedEventPublisher;
    private final OrderOutboxRepository orderOutboxRepository;

    OrderCreatedOutboxScheduler(final OrderCreatedEventPublisher orderCreatedEventPublisher,
                                final OrderOutboxRepository orderOutboxRepository) {

        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
               initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var orderCreatedMessages = orderOutboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderCreatedOutboxMessage.class,
                                                                                                       OutboxStatus.STARTED,
                                                                                                       SagaStatus.STARTED);

        if (!orderCreatedMessages.isEmpty()) {
            orderCreatedMessages.forEach(it -> orderCreatedEventPublisher.publish(it, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderCreatedOutboxMessage orderCreatedOutboxMessage) {
        orderOutboxRepository.save(orderCreatedOutboxMessage);
    }
}
