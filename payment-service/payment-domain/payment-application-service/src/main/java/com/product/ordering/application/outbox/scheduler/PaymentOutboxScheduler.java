package com.product.ordering.application.outbox.scheduler;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.outbox.scheduler.OutboxScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentStatusMessagePublisher paymentStatusMessagePublisher;
    private final PaymentOutboxRepository paymentOutboxRepository;

    PaymentOutboxScheduler(final PaymentStatusMessagePublisher paymentStatusMessagePublisher,
                           final PaymentOutboxRepository paymentOutboxRepository) {

        this.paymentStatusMessagePublisher = paymentStatusMessagePublisher;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
               initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        var paymentOutboxMessages = paymentOutboxRepository.findByMessageTypeAndOutboxStatus(PaymentOutboxMessage.class,
                                                                                             OutboxStatus.STARTED);

        if (!paymentOutboxMessages.isEmpty()) {
            paymentOutboxMessages.forEach(it -> paymentStatusMessagePublisher.publish(it, this::saveOutboxMessage));
        }
    }

    private void saveOutboxMessage(PaymentOutboxMessage paymentOutboxMessage) {
        paymentOutboxRepository.save(paymentOutboxMessage);
    }
}
