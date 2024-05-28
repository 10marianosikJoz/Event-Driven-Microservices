package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.outbox.projection.*;
import com.product.ordering.system.kafka.model.projection.DeliveryAddressMessageProjection;
import com.product.ordering.system.kafka.model.projection.OrderItemMessageProjection;
import com.product.ordering.system.kafka.model.projection.OrderMessageProjection;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class OutputMessageKafkaMapper {

    OrderCancellingEventKafkaProjection mapOrderCancellingOutboxMessageToOrderCancellingEventKafkaProjection(OrderCancellingOutboxMessage orderCancellingOutboxMessage) {
        var messagePayload = (OrderEventPayload) orderCancellingOutboxMessage.getPayload();
        var orderProjection = prepareOrderMessageProjection(messagePayload);

        return new OrderCancellingEventKafkaProjection(orderProjection,
                                                       orderProjection.orderId(),
                                                       orderCancellingOutboxMessage.getCreatedAt(),
                                                       orderCancellingOutboxMessage.getSagaId().toString());
    }

    OrderPaidEventKafkaProjection mapOrderPaidOutboxMessageToOrderPaidEventKafkaProjection(OrderPaidOutboxMessage orderPaidOutboxMessage) {
        var messagePayload = (OrderEventPayload) orderPaidOutboxMessage.getPayload();
        var orderProjection = prepareOrderMessageProjection(messagePayload);

        return new OrderPaidEventKafkaProjection(orderProjection,
                                                 orderProjection.orderId(),
                                                 orderPaidOutboxMessage.getCreatedAt(),
                                                 orderPaidOutboxMessage.getSagaId().toString());
    }

    OrderCreatedEventKafkaProjection mapOrderCreatedOutboxMessageToOrderCreatedEventKafkaProjection(OrderCreatedOutboxMessage orderCreatedOutboxMessage) {
        var messagePayload = (OrderEventPayload) orderCreatedOutboxMessage.getPayload();
        var orderProjection = prepareOrderMessageProjection(messagePayload);

        return new OrderCreatedEventKafkaProjection(orderProjection,
                                                    orderProjection.orderId(),
                                                    orderCreatedOutboxMessage.getCreatedAt(),
                                                    orderCreatedOutboxMessage.getSagaId().toString());
    }

    private OrderMessageProjection prepareOrderMessageProjection(OrderEventPayload orderEventPayload) {
        return OrderMessageProjection.builder()
                .orderId(orderEventPayload.orderId())
                .customerId(orderEventPayload.customerId())
                .warehouseId(orderEventPayload.warehouseId())
                .deliveryAddress(mapDeliveryAddressPayloadToDeliveryAddressMessageProjection(orderEventPayload.deliveryAddress()))
                .currency(orderEventPayload.currency())
                .orderItems(mapOrderItemPayloadToOrderItemProjection(orderEventPayload.orderItems()))
                .paymentMethod(orderEventPayload.paymentMethod())
                .deliveryMethod(orderEventPayload.deliveryMethod())
                .price(orderEventPayload.price().setScale(2, RoundingMode.HALF_EVEN))
                .orderStatus(orderEventPayload.orderStatus())
                .failureMessages(Arrays.asList(orderEventPayload.failureMessages()))
                .createdAt(Instant.now())
                .build();
    }

    private DeliveryAddressMessageProjection mapDeliveryAddressPayloadToDeliveryAddressMessageProjection(DeliveryAddressPayload deliveryAddressPayload) {
        return new DeliveryAddressMessageProjection(deliveryAddressPayload.deliveryAddressId(),
                                                    deliveryAddressPayload.street(),
                                                    deliveryAddressPayload.postalCode(),
                                                    deliveryAddressPayload.city());
    }

    private Set<OrderItemMessageProjection> mapOrderItemPayloadToOrderItemProjection(List<OrderItemPayload> orderItemPayload) {
        return orderItemPayload.stream()
                .map(it -> new OrderItemMessageProjection(it.orderItemId(),
                                                          it.productId(),
                                                          it.quantity(),
                                                          it.subtotal()))
                .collect(Collectors.toSet());
    }
}


