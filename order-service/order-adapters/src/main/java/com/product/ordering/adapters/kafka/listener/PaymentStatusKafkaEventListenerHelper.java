package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.ports.output.publisher.OrderPaidEventPublisher;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
class PaymentStatusKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusKafkaEventListenerHelper.class);

    private final InputMessageKafkaMapper inputMessageKafkaMapper;
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderPaidEventPublisher orderPaidEventPublisher;

    PaymentStatusKafkaEventListenerHelper(final InputMessageKafkaMapper inputMessageKafkaMapper,
                                          final OrderDomainService orderDomainService,
                                          final OrderRepository orderRepository,
                                          final OrderPaidEventPublisher orderPaidEventPublisher) {

        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderPaidEventPublisher = orderPaidEventPublisher;
    }

    void handlePaymentStatusEventData(List<PaymentStatusEventKafkaProjection> messages) {
        messages.forEach(this::determinePaymentStatus);
    }

    private void determinePaymentStatus(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
            var paymentStatusEvent = mapPaymentStatusEventKafkaDtoToPaymentStatusEvent(paymentStatusEventKafkaProjection);

            verifyPaymentStatus(paymentStatusEvent);
    }

    private PaymentStatusEvent mapPaymentStatusEventKafkaDtoToPaymentStatusEvent(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
        return inputMessageKafkaMapper.mapPaymentStatusEventKafkaDtoToPaymentStatusEvent(paymentStatusEventKafkaProjection);
    }

    private void verifyPaymentStatus(PaymentStatusEvent paymentStatusEvent) {

        if (PaymentStatus.COMPLETED == paymentStatusEvent.paymentStatus()) {
            LOGGER.info("Order with id: {} is completed", paymentStatusEvent.orderId());

            var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));
            var orderPaidEvent = orderDomainService.payOrder(order, orderPaidEventPublisher);
            orderRepository.save(order);
            orderPaidEvent.run();

        } else if (PaymentStatus.CANCELLED == paymentStatusEvent.paymentStatus() ||
                   PaymentStatus.REJECTED == paymentStatusEvent.paymentStatus() ||
                   PaymentStatus.FAILED == paymentStatusEvent.paymentStatus()) {
            LOGGER.info("Order with id: {} is cancelled, failure messages: {}",
                        paymentStatusEvent.orderId(),
                        paymentStatusEvent.failureMessages());
        }
    }
}
