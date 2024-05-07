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

    OrderCancellingEventKafkaProjection mapOrderCancellingEventToOrderCancellingEventKafkaProjection(OrderCancellingEvent orderCancellingEvent) {
        var order = orderCancellingEvent.getOrder();
        var orderProjection = prepareOrderMessageProjection(order);

        return new OrderCancellingEventKafkaProjection(orderProjection, orderProjection.orderId(), orderCancellingEvent.createdAt());
    }

    OrderPaidEventKafkaProjection mapOrderPaidEventToOrderPaidEventKafkaProjection(OrderPaidEvent orderPaidEvent) {
        var order = orderPaidEvent.getOrder();
        var orderProjection = prepareOrderMessageProjection(order);

        return new OrderPaidEventKafkaProjection(orderProjection, orderProjection.orderId(), orderPaidEvent.createdAt());
    }

    OrderCreatedEventKafkaProjection mapOrderCreatedEventToOrderCreatedEventKafkaProjection(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();
        var orderProjection = prepareOrderMessageProjection(order);

        return new OrderCreatedEventKafkaProjection(orderProjection, orderProjection.orderId(), orderCreatedEvent.createdAt());
    }

    private OrderMessageProjection prepareOrderMessageProjection(Order order) {
        return OrderMessageProjection.builder()
                .orderId(order.id().value().toString())
                .customerId(order.customerId().value().toString())
                .warehouseId(order.warehouseId().value().toString())
                .deliveryAddress(mapDeliveryAddressToDeliveryAddressMessageProjection(order.deliveryAddress()))
                .currency(order.currency().name())
                .orderItems(mapOrderItemToOrderItemProjection(order))
                .paymentMethod(order.paymentMethod().name())
                .deliveryMethod(order.deliveryMethod().name())
                .price(order.price().amount())
                .orderStatus(order.orderStatus().name())
                .failureMessages(order.failureMessages())
                .createdAt(Instant.now())
                .build();
    }

    private DeliveryAddressMessageProjection mapDeliveryAddressToDeliveryAddressMessageProjection(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressMessageProjection(deliveryAddress.id().toString(),
                                                    deliveryAddress.street(),
                                                    deliveryAddress.postalCode(),
                                                    deliveryAddress.city());
    }

    private Set<OrderItemMessageProjection> mapOrderItemToOrderItemProjection(Order order) {
        return order.orderItems().stream()
                .map(it -> new OrderItemMessageProjection(it.id().toString(),
                                                          it.product().id().value().toString(),
                                                          it.quantity().quantity(),
                                                          it.subtotal().amount()))
                .collect(Collectors.toSet());
    }
}


