package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.valueobject.DeliveryAddress;
import com.product.ordering.system.kafka.model.projection.DeliveryAddressMessageProjection;
import com.product.ordering.system.kafka.model.projection.OrderItemMessageProjection;
import com.product.ordering.system.kafka.model.projection.OrderMessageProjection;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class OutputMessageKafkaMapper {

    OrderCancellingEventKafkaProjection mapOrderCancellingEventToOrderCancellingEventKafkaDto(OrderCancellingEvent orderCancellingEvent) {
        var order = orderCancellingEvent.getOrder();
        var orderDto = prepareOrderMessageDto(order);

        return new OrderCancellingEventKafkaProjection(orderDto, orderDto.orderId(), orderCancellingEvent.createdAt());
    }

    OrderPaidEventKafkaProjection mapOrderPaidEventToOrderPaidEventKafkaDto(OrderPaidEvent orderPaidEvent) {
        var order = orderPaidEvent.getOrder();
        var orderDto = prepareOrderMessageDto(order);

        return new OrderPaidEventKafkaProjection(orderDto, orderDto.orderId(), orderPaidEvent.createdAt());
    }

    OrderCreatedEventKafkaProjection mapOrderCreatedEventToOrderCreatedEventKafkaDto(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();
        var orderDto = prepareOrderMessageDto(order);

        return new OrderCreatedEventKafkaProjection(orderDto, orderDto.orderId(), orderCreatedEvent.createdAt());
    }

    private OrderMessageProjection prepareOrderMessageDto(Order order) {
        return OrderMessageProjection.builder()
                .orderId(order.id().value().toString())
                .customerId(order.customerId().value().toString())
                .warehouseId(order.warehouseId().value().toString())
                .deliveryAddress(mapDeliveryAddressToDeliveryAddressMessageDto(order.deliveryAddress()))
                .currency(order.currency().name())
                .orderItems(mapOrderItemToOrderItemDto(order))
                .paymentMethod(order.paymentMethod().name())
                .deliveryMethod(order.deliveryMethod().name())
                .price(order.price().amount())
                .orderStatus(order.orderStatus().name())
                .failureMessages(order.failureMessages())
                .createdAt(Instant.now())
                .build();
    }

    private DeliveryAddressMessageProjection mapDeliveryAddressToDeliveryAddressMessageDto(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressMessageProjection(deliveryAddress.id().toString(),
                                                    deliveryAddress.street(),
                                                    deliveryAddress.postalCode(),
                                                    deliveryAddress.city());
    }

    private Set<OrderItemMessageProjection> mapOrderItemToOrderItemDto(Order order) {
        return order.orderItems().stream()
                .map(it -> new OrderItemMessageProjection(it.id().toString(),
                                                          it.product().id().value().toString(),
                                                          it.quantity().quantity(),
                                                          it.subtotal().amount()))
                .collect(Collectors.toSet());
    }
}


