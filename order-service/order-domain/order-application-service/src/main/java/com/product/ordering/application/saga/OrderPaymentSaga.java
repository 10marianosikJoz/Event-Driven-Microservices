package com.product.ordering.application.saga;

import com.product.ordering.application.ports.output.publisher.OrderPaidEventPublisher;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.event.EmptyEvent;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class OrderPaymentSaga implements SagaStep<PaymentStatusEvent, OrderPaidEvent, EmptyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentSaga.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderPaidEventPublisher orderPaidEventPublisher;

    public OrderPaymentSaga(final OrderDomainService orderDomainService,
                            final OrderRepository orderRepository,
                            final OrderPaidEventPublisher orderPaidEventPublisher) {

        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderPaidEventPublisher = orderPaidEventPublisher;
    }

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentStatusEvent paymentStatusEvent) {
        LOGGER.info("SagaStep: Completing payment for order: {}.", paymentStatusEvent.orderId());

        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));

        var orderPaidEvent = orderDomainService.payOrder(order, orderPaidEventPublisher);

        orderRepository.save(order);

        LOGGER.info("OrderPaymentSaga: Order: {} is paid.", order.id().value());
        LOGGER.info("OrderPaymentSaga: Publishing OrderPaidEvent for order: {}.", order.id().value());

        orderPaidEvent.run();

        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentStatusEvent paymentStatusEvent) {
        LOGGER.info("SagaStep: Cancelling payment for order: {}.", paymentStatusEvent.orderId());

        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));

        orderDomainService.cancelOrder(order, paymentStatusEvent.failureMessages());

        orderRepository.save(order);

        LOGGER.info("OrderPaymentSaga: Order: {} is cancelled.", order.id().value());
        LOGGER.info("OrderPaymentSaga: Order is roll backed with failure messages: {}.", paymentStatusEvent.failureMessages());

        return EmptyEvent.INSTANCE;
    }
}
