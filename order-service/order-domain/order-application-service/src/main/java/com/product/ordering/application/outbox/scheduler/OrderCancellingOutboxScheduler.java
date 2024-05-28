package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.OrderCancellingOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.publisher.OrderCancellingEventPublisher;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import com.product.ordering.system.saga.SagaStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderCancellingOutboxScheduler implements OutboxScheduler {

    private final OrderCancellingEventPublisher orderCancellingEventPublisher;
    private final OrderOutboxRepository orderOutboxRepository;

    OrderCancellingOutboxScheduler(final OrderCancellingEventPublisher orderCancellingEventPublisher,
                                   final OrderOutboxRepository orderOutboxRepository) {

        this.orderCancellingEventPublisher = orderCancellingEventPublisher;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
               initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var orderCancellingMessages = orderOutboxRepository.findByMessageTypeAndOutboxStatusAndSagaStatus(OrderCancellingOutboxMessage.class,
                                                                                                          OutboxStatus.STARTED,
                                                                                                          SagaStatus.COMPENSATING);

        if (!orderCancellingMessages.isEmpty()) {
            orderCancellingMessages.forEach(it -> orderCancellingEventPublisher.publish(it, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(OrderCancellingOutboxMessage orderCancellingOutboxMessage) {
        orderOutboxRepository.save(orderCancellingOutboxMessage);
    }
}
