package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderProcessedOutboxRepository;
import com.product.ordering.application.ports.output.publisher.OrderApprovedMessagePublisher;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderProcessedOutboxScheduler implements OutboxScheduler {

    private final OrderProcessedOutboxRepository orderProcessedOutboxRepository;
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;

    OrderProcessedOutboxScheduler(final OrderProcessedOutboxRepository orderProcessedOutboxRepository,
                                  final OrderApprovedMessagePublisher orderApprovedMessagePublisher) {

        this.orderProcessedOutboxRepository = orderProcessedOutboxRepository;
        this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
    }

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${warehouse-service.outbox-scheduler-fixed-rate}",
               initialDelayString = "${warehouse-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var orderOutboxMessages = orderProcessedOutboxRepository.findByTypeAndOutboxStatus(OrderProcessedOutboxMessage.class, OutboxStatus.STARTED);

        orderOutboxMessages.forEach(it -> orderApprovedMessagePublisher.publish(it, this::saveOutboxMessage));
    }

    public void saveOutboxMessage(OrderProcessedOutboxMessage outboxMessage) {
        orderProcessedOutboxRepository.save(outboxMessage);
    }
}
