package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.saga.OrderPaymentSaga;
import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
class PaymentStatusKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusKafkaEventListenerHelper.class);

    private final InputMessageKafkaMapper inputMessageKafkaMapper;
    private final OrderPaymentSaga orderPaymentSaga;

    PaymentStatusKafkaEventListenerHelper(final InputMessageKafkaMapper inputMessageKafkaMapper,
                                          final OrderPaymentSaga orderPaymentSaga) {

        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
        this.orderPaymentSaga = orderPaymentSaga;
    }

    void handlePaymentStatusEventData(List<PaymentStatusEventKafkaProjection> messages) {
        messages.forEach(it -> {
            try {
                determinePaymentStatus(it);
            } catch (OptimisticLockingFailureException e) {
                LOGGER.error("Optimistic locking exception PaymentStatusKafkaEventListenerHelper occurred. Order id: {}", it.getData().orderId());
            } catch (OrderNotFoundException e) {
                LOGGER.error("Order not found. Order id: {}", it.getData().orderId());
            }
        });
    }

    private void determinePaymentStatus(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
            var paymentStatusEvent = mapPaymentStatusEventKafkaProjectionToPaymentStatusEvent(paymentStatusEventKafkaProjection);

            verifyPaymentStatus(paymentStatusEvent);
    }

    private PaymentStatusEvent mapPaymentStatusEventKafkaProjectionToPaymentStatusEvent(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
        return inputMessageKafkaMapper.mapPaymentStatusEventKafkaProjectionToPaymentStatusEvent(paymentStatusEventKafkaProjection);
    }

    private void verifyPaymentStatus(PaymentStatusEvent paymentStatusEvent) {

        if (PaymentStatus.COMPLETED == paymentStatusEvent.paymentStatus()) {
            LOGGER.info("Conducting completing payment process for order: {}.", paymentStatusEvent.orderId());

            paymentCompleted(paymentStatusEvent);

        } else if (PaymentStatus.CANCELLED == paymentStatusEvent.paymentStatus() ||
                   PaymentStatus.REJECTED == paymentStatusEvent.paymentStatus() ||
                   PaymentStatus.FAILED == paymentStatusEvent.paymentStatus()) {
            LOGGER.info("Conducting cancelling payment process for order: {}.", paymentStatusEvent.orderId());

            paymentCancelled(paymentStatusEvent);
        }
    }

    private void paymentCompleted(PaymentStatusEvent paymentStatusEvent) {
        orderPaymentSaga.process(paymentStatusEvent);
    }

    private void paymentCancelled(PaymentStatusEvent paymentStatusEvent) {
        orderPaymentSaga.rollback(paymentStatusEvent);
    }
}
