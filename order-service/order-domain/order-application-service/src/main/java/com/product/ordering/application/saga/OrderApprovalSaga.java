package com.product.ordering.application.saga;

import com.product.ordering.application.ports.output.publisher.OrderCancellingEventPublisher;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.event.EmptyEvent;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.saga.SagaStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderApprovalSaga implements SagaStep<WarehouseApprovalEvent, EmptyEvent, OrderCancellingEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApprovalSaga.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderCancellingEventPublisher orderCancellingEventPublisher;

    public OrderApprovalSaga(final OrderDomainService orderDomainService,
                             final OrderRepository orderRepository,
                             final OrderCancellingEventPublisher orderCancellingEventPublisher) {

        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderCancellingEventPublisher = orderCancellingEventPublisher;
    }

    @Override
    public EmptyEvent process(WarehouseApprovalEvent warehouseApprovalEvent) {
        LOGGER.info("SagaStep: Approving order: {}.", warehouseApprovalEvent.orderId());

        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(warehouseApprovalEvent.orderId())));

        orderDomainService.approveOrder(order);

        orderRepository.save(order);

        LOGGER.info("OrderApprovalSaga: Order: {}, is approved.", order.id());

        return EmptyEvent.INSTANCE;
    }

    @Override
    public OrderCancellingEvent rollback(WarehouseApprovalEvent warehouseApprovalEvent) {
        LOGGER.info("SagaStep: Cancelling order: {}.", warehouseApprovalEvent.orderId());

        var order = orderRepository.fetchOrder(new OrderId(UUID.fromString(warehouseApprovalEvent.orderId())));

        var orderCancellingEvent = orderDomainService.initializeCancelling(order, warehouseApprovalEvent.failureMessages(), orderCancellingEventPublisher);

        orderRepository.save(order);

        LOGGER.info("OrderApprovalSaga: Order: {}, is cancelling.", order.id());
        LOGGER.info("OrderApprovalSaga: Publishing OrderCancellingEvent for order: {} with failure messages: {}",
                    order.id().value(),
                    warehouseApprovalEvent.failureMessages());

        orderCancellingEvent.run();

        return orderCancellingEvent;
    }
}
