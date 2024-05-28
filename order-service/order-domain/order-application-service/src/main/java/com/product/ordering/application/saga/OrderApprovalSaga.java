package com.product.ordering.application.saga;

import com.product.ordering.application.exception.OutboxMessageNotFoundException;
import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.application.outbox.projection.OrderPaidOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.OrderOutboxMapper;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.saga.SagaStatus;
import com.product.ordering.system.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderApprovalSaga implements SagaStep<WarehouseApprovalEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApprovalSaga.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository orderOutboxRepository;
    private final SagaStatusMapper sagaStatusMapper;
    private final OrderOutboxMapper orderOutboxMapper;

    public OrderApprovalSaga(final OrderDomainService orderDomainService,
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
    public void process(WarehouseApprovalEvent warehouseApprovalEvent) {
        LOGGER.info("SagaStep: Approving order: {}.", warehouseApprovalEvent.orderId());

        var orderPaidOutboxMessage = orderOutboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderPaidOutboxMessage.class,
                                                                                                   UUID.fromString(warehouseApprovalEvent.sagaId()),
                                                                                                   SagaStatus.PROCESSING);
        if (orderPaidOutboxMessage.isEmpty()) {
            LOGGER.info("Outbox message already processed. SagaId: {}", warehouseApprovalEvent.sagaId());
            return;
        }

        var order = approveOrder(warehouseApprovalEvent);

        var sagaStatus = sagaStatusMapper.mapOrderStatusToSagaStatus(order.orderStatus());

        updateOrderPaidOutboxMessage(orderPaidOutboxMessage.get(), sagaStatus);
        updateOrderCreatedOutboxMessage(warehouseApprovalEvent, sagaStatus);

        LOGGER.info("OrderApprovalSaga: Order: {}, is approved.", order.id().value());
    }

    private Order approveOrder(WarehouseApprovalEvent warehouseApprovalEvent) {
        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(warehouseApprovalEvent.orderId())));
        orderDomainService.approveOrder(order);
        orderRepository.save(order);

        return order;
    }

    private void updateOrderPaidOutboxMessage(OrderPaidOutboxMessage orderPaidOutboxMessage, SagaStatus sagaStatus) {
        orderPaidOutboxMessage.setProcessedAt(Instant.now());
        orderPaidOutboxMessage.setSagaStatus(sagaStatus);
        orderOutboxRepository.save(orderPaidOutboxMessage);
    }

    private void updateOrderCreatedOutboxMessage(WarehouseApprovalEvent warehouseApprovalEvent, SagaStatus sagaStatus) {
        var orderCreatedOutboxMessageFromDatabase = orderOutboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderCreatedOutboxMessage.class,
                UUID.fromString(warehouseApprovalEvent.sagaId()),
                SagaStatus.PROCESSING);

        if (orderCreatedOutboxMessageFromDatabase.isEmpty()) {
            throw new OutboxMessageNotFoundException("OrderCreatedOutboxMessage not found in Saga status:" + SagaStatus.PROCESSING.name());
        }

        var orderCreatedOutboxMessage = orderCreatedOutboxMessageFromDatabase.get();
        orderCreatedOutboxMessage.setProcessedAt(Instant.now());
        orderCreatedOutboxMessage.setSagaStatus(sagaStatus);

        orderOutboxRepository.save(orderCreatedOutboxMessage);
    }

    @Override
    public void rollback(WarehouseApprovalEvent warehouseApprovalEvent) {
        LOGGER.info("SagaStep: Cancelling order: {}.", warehouseApprovalEvent.orderId());

        var orderPaidOutboxMessage = orderOutboxRepository.findByMessageTypeAndSagaIdAndSagaStatus(OrderPaidOutboxMessage.class,
                                                                                                   UUID.fromString(warehouseApprovalEvent.sagaId()),
                                                                                                   SagaStatus.PROCESSING);

        if (orderPaidOutboxMessage.isEmpty()) {
            LOGGER.info("Outbox message already processed. SagaId: {}", warehouseApprovalEvent.sagaId());
            return;
        }

        var orderCancellingEvent = initializeOrderCancelling(warehouseApprovalEvent);

        LOGGER.info("OrderApprovalSaga: Order: {}, is cancelling.", orderCancellingEvent.getOrder().id().value());
        LOGGER.info("OrderApprovalSaga: Publishing OrderCancellingEvent for order: {} with failure messages: {}",
                    orderCancellingEvent.getOrder().id().value(),
                    warehouseApprovalEvent.failureMessages());

        var sagaStatus = sagaStatusMapper.mapOrderStatusToSagaStatus(orderCancellingEvent.getOrder().orderStatus());

        updateOrderPaidOutboxMessage(orderPaidOutboxMessage.get(), sagaStatus);

        updateOrderCancellingOutboxMessage(warehouseApprovalEvent, orderCancellingEvent);

        LOGGER.info("OrderApprovalSaga: Order: {}, is cancelled", orderCancellingEvent.getOrder().id().value());
    }

    private OrderCancellingEvent initializeOrderCancelling(WarehouseApprovalEvent warehouseApprovalEvent) {
        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(warehouseApprovalEvent.orderId())));
        var orderCancellingEvent = orderDomainService.initializeCancelling(order, warehouseApprovalEvent.failureMessages());

        orderRepository.save(order);

        return orderCancellingEvent;
    }

    private void updateOrderCancellingOutboxMessage(WarehouseApprovalEvent warehouseApprovalEvent, OrderCancellingEvent orderCancellingEvent) {
        var outboxCancellingOutboxMessage = orderOutboxMapper.mapOrderCancellingEventToOrderCancellingOutboxMessage(orderCancellingEvent,
                                                                                                                    UUID.fromString(warehouseApprovalEvent.sagaId()));
        orderOutboxRepository.save(outboxCancellingOutboxMessage);

    }
}
