package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.OrderPaidOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.publisher.OrderPaidEventPublisher;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import com.product.ordering.system.saga.SagaStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderPaidOutboxScheduler implements OutboxScheduler {

    private final OrderPaidEventPublisher orderPaidEventPublisher;
    private final OrderOutboxRepository orderOutboxRepository;

    OrderPaidOutboxScheduler(final OrderPaidEventPublisher orderPaidEventPublisher,
                             final OrderOutboxRepository orderOutboxRepository) {

        this.orderPaidEventPublisher = orderPaidEventPublisher;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
               initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var orderPaidMessages = orderOutboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderPaidOutboxMessage.class,
                                                                                                    OutboxStatus.STARTED,
                                                                                                    SagaStatus.PROCESSING);

        if (!orderPaidMessages.isEmpty()) {
            orderPaidMessages.forEach(it -> orderPaidEventPublisher.publish(it, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderPaidOutboxMessage orderPaidOutboxMessage) {
        orderOutboxRepository.save(orderPaidOutboxMessage);
    }
}
