package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.command.projection.OrderItemProjection;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.system.kafka.model.projection.OrderMessageProjection;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class InputMessageKafkaMapper {

    OrderPaidEvent mapOrderPaidEventKafkaProjectionToOrderPaidEvent(OrderPaidEventKafkaProjection orderPaidEventKafkaProjection) {
        var orderMessageProjection = orderPaidEventKafkaProjection.getData();

        return OrderPaidEvent.builder()
                .orderId(orderMessageProjection.orderId())
                .warehouseId(orderMessageProjection.warehouseId())
                .price(orderMessageProjection.price())
                .createdAt(orderMessageProjection.createdAt())
                .orderStatus(orderMessageProjection.orderStatus())
                .orderItem(mapOrderMessageProjectionToOrderItemProjection(orderMessageProjection))
                .build();
    }

    private List<OrderItemProjection> mapOrderMessageProjectionToOrderItemProjection(OrderMessageProjection orderMessageProjection) {
        return orderMessageProjection.orderItems().stream()
                                                  .map(it -> new OrderItemProjection(it.orderItemId(),
                                                                              it.productId(),
                                                                              it.price(),
                                                                              it.quantity()))
                                                  .toList();
    }
}
