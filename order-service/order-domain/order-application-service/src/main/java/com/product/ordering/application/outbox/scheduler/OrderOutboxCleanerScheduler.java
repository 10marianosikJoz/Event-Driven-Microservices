package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import com.product.ordering.system.saga.SagaStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class OrderOutboxCleanerScheduler implements OutboxScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderOutboxCleanerScheduler.class);

    private final OrderOutboxRepository orderOutboxRepository;

    OrderOutboxCleanerScheduler(OrderOutboxRepository orderOutboxRepository) {
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        LOGGER.info("Deleting order outbox messages - started");

        orderOutboxRepository.deleteByOutboxStatusAndSagaStatus(OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);

        LOGGER.info("Deleting order outbox messages - finished");
    }
}
