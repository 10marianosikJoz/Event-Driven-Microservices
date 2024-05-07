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
        var orderMessageDto = orderPaidEventKafkaProjection.getData();

        return OrderPaidEvent.builder()
                .orderId(orderMessageDto.orderId())
                .warehouseId(orderMessageDto.warehouseId())
                .price(orderMessageDto.price())
                .createdAt(orderMessageDto.createdAt())
                .orderStatus(orderMessageDto.orderStatus())
                .orderItem(mapOrderMessageProjectionToOrderItemProjection(orderMessageDto))
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
