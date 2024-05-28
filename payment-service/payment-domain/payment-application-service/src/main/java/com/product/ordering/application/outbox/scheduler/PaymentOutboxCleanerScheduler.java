package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentOutboxCleanerScheduler.class);

    private final PaymentOutboxRepository paymentOutboxRepository;

    PaymentOutboxCleanerScheduler(final PaymentOutboxRepository paymentOutboxRepository) {
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        LOGGER.info("Deleting order outbox messages - started");

        paymentOutboxRepository.deleteByOutboxStatus(OutboxStatus.COMPLETED);

        LOGGER.info("Deleting order outbox messages - finished");
    }
}
