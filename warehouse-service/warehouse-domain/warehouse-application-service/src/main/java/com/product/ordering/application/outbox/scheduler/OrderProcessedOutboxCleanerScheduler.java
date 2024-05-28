package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderProcessedOutboxRepository;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderProcessedOutboxCleanerScheduler implements OutboxScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderProcessedOutboxCleanerScheduler.class);

    private final OrderProcessedOutboxRepository orderProcessedOutboxRepository;

    OrderProcessedOutboxCleanerScheduler(final OrderProcessedOutboxRepository orderProcessedOutboxRepository) {
        this.orderProcessedOutboxRepository = orderProcessedOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        LOGGER.info("Deleting order outbox messages - started");

        orderProcessedOutboxRepository.deleteByTypeAndOutboxStatus(OrderProcessedOutboxMessage.class, OutboxStatus.COMPLETED);

        LOGGER.info("Deleting order outbox messages - finished");
    }
}
