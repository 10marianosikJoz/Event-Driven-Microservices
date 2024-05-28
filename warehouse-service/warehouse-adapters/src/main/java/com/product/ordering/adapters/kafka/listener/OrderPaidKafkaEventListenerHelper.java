package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.WarehouseApplicationService;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.application.exception.WarehouseApplicationServiceException;
import com.product.ordering.domain.exception.WarehouseNotFoundException;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import org.postgresql.util.PSQLState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
class OrderPaidKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaidKafkaEventListenerHelper.class);

    private final WarehouseApplicationService warehouseApplicationService;
    private final InputMessageKafkaMapper inputMessageKafkaMapper;

    OrderPaidKafkaEventListenerHelper(final WarehouseApplicationService warehouseApplicationService,
                                      final InputMessageKafkaMapper inputMessageKafkaMapper) {

        this.warehouseApplicationService = warehouseApplicationService;
        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
    }

    void handleOrderPaidEventData(List<OrderPaidEventKafkaProjection> messages) {
        messages.forEach(it -> {
            try {
                warehouseApplicationService.verifyOrder(mapOrderPaidEventKafkaProjectionToOrderPaidEvent(it));

            } catch (DataAccessException e) {
                var sqlException = (SQLException) e.getRootCause();

                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    LOGGER.error("Unique constraint exception with sql state: {} " +
                            "in OrderPaidKafkaEventListenerHelper occurred. Order id: {}", sqlException.getSQLState(), it.getData().orderId());
                } else {
                    throw new WarehouseApplicationServiceException("DataAccessException in OrderPaidKafkaEventListenerHelper: " + e.getMessage());
                }
            } catch (WarehouseNotFoundException e) {
                LOGGER.error("Warehouse not found for order: {}", it.getData().orderId());
            }
        });
    }

    private OrderPaidEvent mapOrderPaidEventKafkaProjectionToOrderPaidEvent(OrderPaidEventKafkaProjection orderPaidEventKafkaProjection) {
        return inputMessageKafkaMapper.mapOrderPaidEventKafkaProjectionToOrderPaidEvent(orderPaidEventKafkaProjection);
    }
}
