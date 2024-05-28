package com.product.ordering.application.saga;

import com.product.ordering.application.exception.OutboxMessageNotFoundException;
import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.OrderOutboxMapper;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.saga.SagaStatus;
import com.product.ordering.system.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderPaymentSaga implements SagaStep<PaymentStatusEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentSaga.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final SagaStatusMapper sagaStatusMapper;
    private final OrderOutboxMapper orderOutboxMapper;

    OrderPaymentSaga(final OrderDomainService orderDomainService,
                     final OrderRepository orderRepository,
                     final OrderOutboxRepository orderOutboxRepository,
                     final SagaStatusMapper sagaStatusMapper,
                     final OrderOutboxMapper orderOutboxMapper) {

        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderOutboxRepository = orderOutboxRepository;
        this.sagaStatusMapper = sagaStatusMapper;
        this.orderOutboxMapper = orderOutboxMapper;
    }

    @Override
    @Transactional
    public void process(PaymentStatusEvent paymentStatusEvent) {
        LOGGER.info("SagaStep: Completing payment for order: {}.", paymentStatusEvent.orderId());

        var orderCreatedOutboxMessageOptional = orderOutboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderCreatedOutboxMessage.class,
                                                                                                              UUID.fromString(paymentStatusEvent.sagaId()),
                                                                                                              SagaStatus.STARTED);

        if (orderCreatedOutboxMessageOptional.isEmpty()) {
            LOGGER.info("Outbox message already processed. SagaId: {}", paymentStatusEvent.sagaId());
            return;
        }

        var orderPaidEvent = payOrder(paymentStatusEvent);

        LOGGER.info("OrderPaymentSaga: Order: {} is paid.", orderPaidEvent.getOrder().id().value());
        LOGGER.info("OrderPaymentSaga: Publishing OrderPaidEvent for order: {}.", orderPaidEvent.getOrder().id().value());

        var sagaStatus = sagaStatusMapper.mapOrderStatusToSagaStatus(orderPaidEvent.getOrder().orderStatus());

        updateOrderCreatedOutboxMessage(orderCreatedOutboxMessageOptional.get(), sagaStatus);

        updateOrderPaidOutboxMessage(paymentStatusEvent, orderPaidEvent);
    }

    private OrderPaidEvent payOrder(PaymentStatusEvent paymentStatusEvent) {
        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));
        var orderPaidEvent = orderDomainService.payOrder(order);
        orderRepository.save(order);

        return orderPaidEvent;
    }

    private void updateOrderCreatedOutboxMessage(OrderCreatedOutboxMessage orderCreatedOutboxMessage, SagaStatus sagaStatus) {
        orderCreatedOutboxMessage.setProcessedAt(Instant.now());
        orderCreatedOutboxMessage.setSagaStatus(sagaStatus);
        orderOutboxRepository.save(orderCreatedOutboxMessage);
    }

    private void updateOrderPaidOutboxMessage(PaymentStatusEvent paymentStatusEvent, OrderPaidEvent orderPaidEvent) {
        var orderPaidOutboxMessage = orderOutboxMapper.mapOrderPaidEventToOrderPaidOutboxMessage(orderPaidEvent,
                                                                                                 UUID.fromString(paymentStatusEvent.sagaId()));
        orderOutboxRepository.save(orderPaidOutboxMessage);
    }

    @Override
    @Transactional
    public void rollback(PaymentStatusEvent paymentStatusEvent) {
        LOGGER.info("SagaStep: Cancelling payment for order: {}.", paymentStatusEvent.orderId());

        var orderCreatedOutboxMessageOptional = orderOutboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderCreatedOutboxMessage.class,
                                                                                                              UUID.fromString(paymentStatusEvent.sagaId()),
                                                                                                              sagaStatusMapper.mapPaymentStatusToSagaStatus(paymentStatusEvent.paymentStatus()));

        if (orderCreatedOutboxMessageOptional.isEmpty()) {
            LOGGER.info("Outbox message already processed. saga id: {}", paymentStatusEvent.sagaId());
            return;
        }

        var order = cancelOrder(paymentStatusEvent);

        var sagaStatus = sagaStatusMapper.mapOrderStatusToSagaStatus(order.orderStatus());

        updateOrderCreatedOutboxMessage(orderCreatedOutboxMessageOptional.get(), sagaStatus);

        if (PaymentStatus.CANCELLED.equals(paymentStatusEvent.paymentStatus())) {
            updateOutboxCompensatingMessage(paymentStatusEvent, sagaStatus);
        }

        LOGGER.info("OrderPaymentSaga: Order: {} is cancelled.", order.id().value());
        LOGGER.info("OrderPaymentSaga: Order is roll backed with failure messages: {}.", paymentStatusEvent.failureMessages());
    }

    private Order cancelOrder(PaymentStatusEvent paymentStatusEvent) {
        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(paymentStatusEvent.orderId())));
        orderDomainService.cancelOrder(order, paymentStatusEvent.failureMessages());
        orderRepository.save(order);

        return order;
    }

    private void updateOutboxCompensatingMessage(PaymentStatusEvent paymentStatusEvent, SagaStatus sagaStatus) {
        var outboxCompensatingMessages = orderOutboxRepository.findBySagaIdAndSagaStatus(UUID.fromString(paymentStatusEvent.sagaId()),
                                                                                         SagaStatus.COMPENSATING);

        if (outboxCompensatingMessages.isEmpty()) {
            throw new OutboxMessageNotFoundException("Outbox messages not found in Saga status:" + SagaStatus.COMPENSATING.name());
        }

        outboxCompensatingMessages.forEach(it -> orderOutboxRepository.updateSagaStatusAndProcessAtById(sagaStatus,
                                                                                                        Instant.now(),
                                                                                                        it.getId()));
    }
}
