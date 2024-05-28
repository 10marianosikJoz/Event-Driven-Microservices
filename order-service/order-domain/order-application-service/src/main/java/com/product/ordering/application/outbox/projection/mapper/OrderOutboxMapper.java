package com.product.ordering.application.outbox.projection.mapper;

import com.product.ordering.application.outbox.projection.*;
import com.product.ordering.application.saga.SagaStatusMapper;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.entity.OrderItem;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.valueobject.DeliveryAddress;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderOutboxMapper {

    private final SagaStatusMapper sagaStatusMapper;

    public OrderOutboxMapper(final SagaStatusMapper sagaStatusMapper) {
        this.sagaStatusMapper = sagaStatusMapper;
    }

    public OrderCancellingOutboxMessage mapOrderCancellingEventToOrderCancellingOutboxMessage(OrderCancellingEvent orderCancellingEvent, UUID sagaId) {
        var order = orderCancellingEvent.getOrder();
        var orderCancellingOutboxMessage = new OrderCancellingOutboxMessage();

        orderCancellingOutboxMessage.setId(UUID.randomUUID());
        orderCancellingOutboxMessage.setSagaId(sagaId);
        orderCancellingOutboxMessage.setCreatedAt(orderCancellingEvent.createdAt());
        orderCancellingOutboxMessage.setProcessedAt(orderCancellingEvent.createdAt());
        orderCancellingOutboxMessage.setPayload(mapOrderToOrderEventPayload(order));
        orderCancellingOutboxMessage.setAggregateId(order.id().value());
        orderCancellingOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderCancellingOutboxMessage.setSagaStatus(sagaStatusMapper.mapOrderStatusToSagaStatus(order.orderStatus()));

        return orderCancellingOutboxMessage;
    }

    public OrderPaidOutboxMessage mapOrderPaidEventToOrderPaidOutboxMessage(OrderPaidEvent orderPaidEvent, UUID sagaId) {
        var order = orderPaidEvent.getOrder();
        var orderPaidOutboxMessage = new OrderPaidOutboxMessage();

        orderPaidOutboxMessage.setId(UUID.randomUUID());
        orderPaidOutboxMessage.setSagaId(sagaId);
        orderPaidOutboxMessage.setCreatedAt(orderPaidEvent.createdAt());
        orderPaidOutboxMessage.setProcessedAt(orderPaidEvent.createdAt());
        orderPaidOutboxMessage.setPayload(mapOrderToOrderEventPayload(order));
        orderPaidOutboxMessage.setAggregateId(order.id().value());
        orderPaidOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderPaidOutboxMessage.setSagaStatus(sagaStatusMapper.mapOrderStatusToSagaStatus(order.orderStatus()));

        return orderPaidOutboxMessage;
    }

    public OrderCreatedOutboxMessage mapOrderCreatedEventToOrderCreatedOutboxMessage(OrderCreatedEvent orderCreatedEvent) {
        var order = orderCreatedEvent.getOrder();
        var orderCreatedOutboxMessage = new OrderCreatedOutboxMessage();

        orderCreatedOutboxMessage.setId(UUID.randomUUID());
        orderCreatedOutboxMessage.setSagaId(UUID.randomUUID());
        orderCreatedOutboxMessage.setCreatedAt(orderCreatedEvent.createdAt());
        orderCreatedOutboxMessage.setProcessedAt(orderCreatedEvent.createdAt());
        orderCreatedOutboxMessage.setPayload(mapOrderToOrderEventPayload(order));
        orderCreatedOutboxMessage.setAggregateId(order.id().value());
        orderCreatedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderCreatedOutboxMessage.setSagaStatus(sagaStatusMapper.mapOrderStatusToSagaStatus(order.orderStatus()));

        return orderCreatedOutboxMessage;
    }

    private OrderEventPayload mapOrderToOrderEventPayload(Order order) {
        return OrderEventPayload.builder()
                .orderId(order.id().value().toString())
                .customerId(order.customerId().value().toString())
                .warehouseId(order.warehouseId().value().toString())
                .price(order.price().amount())
                .orderStatus(order.orderStatus().name())
                .orderItems(mapOrderItemToOrderItemPayload(order.orderItems()))
                .deliveryAddress(mapDeliveryAddressToDeliveryAddressPayload(order.deliveryAddress()))
                .currency(order.currency().name())
                .paymentMethod(order.paymentMethod().name())
                .deliveryMethod(order.deliveryMethod().name())
                .build();
    }

    private List<OrderItemPayload> mapOrderItemToOrderItemPayload(Set<OrderItem> orderItems) {
        return orderItems.stream()
                         .map(it -> OrderItemPayload.builder()
                                 .quantity(it.quantity().quantity())
                                 .productId(it.product().id().value().toString())
                                 .price(it.price().amount())
                                 .subtotal(it.subtotal().amount())
                                 .build())
                        .collect(Collectors.toList());
    }

    private DeliveryAddressPayload mapDeliveryAddressToDeliveryAddressPayload(DeliveryAddress deliveryAddress) {
        return DeliveryAddressPayload.builder()
                .deliveryAddressId(deliveryAddress.id().toString())
                .street(deliveryAddress.street())
                .postalCode(deliveryAddress.postalCode())
                .city(deliveryAddress.city())
                .build();
    }
}
