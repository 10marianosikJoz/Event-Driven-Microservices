package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.saga.OrderApprovalSaga;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WarehouseApprovalKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseApprovalKafkaEventListenerHelper.class);

    private final InputMessageKafkaMapper inputMessageKafkaMapper;
    private final OrderApprovalSaga orderApprovalSaga;

    WarehouseApprovalKafkaEventListenerHelper(final InputMessageKafkaMapper inputMessageKafkaMapper,
                                              final OrderApprovalSaga orderApprovalSaga) {

        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
        this.orderApprovalSaga = orderApprovalSaga;
    }

    void handleWarehouseApprovalEventData(List<WarehouseApprovalEventKafkaProjection> messages) {
        messages.forEach(it -> {
            try {
                determineOrderApprovalStatus(it);
            } catch (OptimisticLockingFailureException e) {
                LOGGER.error("Optimistic locking exception in WarehouseApprovalKafkaEventListenerHelper occurred. Order id: {}.", it.getData().orderId());
            } catch (OrderNotFoundException e) {
                LOGGER.error("Order not found. Order id: {}.", it.getData().orderId());
            }
        });
    }

    private void determineOrderApprovalStatus(WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
            var warehouseApprovalEvent = mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(warehouseApprovalEventKafkaProjection);

            verifyOrderApprovalStatus(warehouseApprovalEvent);
    }

    private WarehouseApprovalEvent mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
        return inputMessageKafkaMapper.mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(warehouseApprovalEventKafkaProjection);
    }

    private void verifyOrderApprovalStatus(WarehouseApprovalEvent warehouseApprovalEvent) {
        if (OrderApprovalStatus.APPROVED == warehouseApprovalEvent.orderApprovalStatus()) {
            LOGGER.info("Conducting approval for Order with id: {}.", warehouseApprovalEvent.orderId());

            orderApproved(warehouseApprovalEvent);

        } else if (OrderApprovalStatus.REJECTED == warehouseApprovalEvent.orderApprovalStatus()) {
            LOGGER.info("Conduction rejection for Order with id: {}, failure messages: {}.",
                        warehouseApprovalEvent.orderId(),
                        warehouseApprovalEvent.failureMessages());

            orderRejected(warehouseApprovalEvent);
        }
    }

    private void orderApproved(WarehouseApprovalEvent warehouseApprovalEvent) {
        orderApprovalSaga.process(warehouseApprovalEvent);
    }

    private void orderRejected(WarehouseApprovalEvent warehouseApprovalEvent) {
        orderApprovalSaga.rollback(warehouseApprovalEvent);
    }
}
