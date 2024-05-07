package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
class WarehouseApprovalKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseApprovalKafkaEventListenerHelper.class);

    private final InputMessageKafkaMapper inputMessageKafkaMapper;
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher<OrderCancellingEvent> orderCancellingEventDomainEventPublisher;

    WarehouseApprovalKafkaEventListenerHelper(final InputMessageKafkaMapper inputMessageKafkaMapper,
                                              final OrderDomainService orderDomainService,
                                              final OrderRepository orderRepository,
                                              final DomainEventPublisher<OrderCancellingEvent> orderCancellingEventDomainEventPublisher) {

        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderCancellingEventDomainEventPublisher = orderCancellingEventDomainEventPublisher;
    }

    void handleWarehouseApprovalEventData(List<WarehouseApprovalEventKafkaProjection> messages) {
        messages.forEach(it -> {
            var orderId = it.getData().orderId();
            determineOrderApprovalStatus(orderId, it);
        });
    }

    private void determineOrderApprovalStatus(String orderId,
                                              WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
        try {
            var warehouseApprovalEvent = mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(warehouseApprovalEventKafkaProjection);

            verifyOrderApprovalStatus(warehouseApprovalEvent);

        } catch (OptimisticLockingFailureException e) {
            LOGGER.error("Optimistic locking exception in WarehouseApprovalKafkaEventListener for order id: {}", orderId);
        } catch (OrderNotFoundException e) {
            LOGGER.error("Order not found, order id: {}", orderId);
        }
    }

    private WarehouseApprovalEvent mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
        return inputMessageKafkaMapper.mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(warehouseApprovalEventKafkaProjection);
    }

    private void verifyOrderApprovalStatus(WarehouseApprovalEvent warehouseApprovalEvent) {
        if (OrderApprovalStatus.APPROVED == warehouseApprovalEvent.orderApprovalStatus()) {
            LOGGER.info("Conducting approval for Order with id: {}", warehouseApprovalEvent.orderId());

            var order = fetchOrder(warehouseApprovalEvent.orderId());

            orderDomainService.approveOrder(order);
            updateOrderStatus(order);

        } else if (OrderApprovalStatus.REJECTED == warehouseApprovalEvent.orderApprovalStatus()) {
            LOGGER.info("Conduction rejection for Order with id: {}, failure messages: {}",
                    warehouseApprovalEvent.orderId(), warehouseApprovalEvent.failureMessages());

            var order = fetchOrder(warehouseApprovalEvent.orderId());
            var failureMessages = new ArrayList<String>();

            orderDomainService.initializeCancelling(order, failureMessages, orderCancellingEventDomainEventPublisher);
            updateOrderStatus(order);
        }
    }

    private Order fetchOrder(String orderId) {
        return orderRepository.fetchOrder(new OrderId(UUID.fromString(orderId)));
    }

    private void updateOrderStatus(Order order) {
        orderRepository.save(order);
    }
}
