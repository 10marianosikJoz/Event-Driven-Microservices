package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.WarehouseApplicationService;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderPaidKafkaEventListenerHelper {

    private final WarehouseApplicationService warehouseApplicationService;
    private final InputMessageKafkaMapper inputMessageKafkaMapper;

    OrderPaidKafkaEventListenerHelper(final WarehouseApplicationService warehouseApplicationService,
                                      final InputMessageKafkaMapper inputMessageKafkaMapper) {

        this.warehouseApplicationService = warehouseApplicationService;
        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
    }

    void handleOrderPaidEventData(List<OrderPaidEventKafkaProjection> messages) {
       messages.forEach(it -> warehouseApplicationService.verifyOrder(mapOrderPaidEventKafkaDtoToOrderPaidEvent(it)));
    }

    private OrderPaidEvent mapOrderPaidEventKafkaDtoToOrderPaidEvent(OrderPaidEventKafkaProjection orderPaidEventKafkaProjection) {
        return inputMessageKafkaMapper.mapOrderPaidEventKafkaProjectionToOrderPaidEvent(orderPaidEventKafkaProjection);
    }
}
