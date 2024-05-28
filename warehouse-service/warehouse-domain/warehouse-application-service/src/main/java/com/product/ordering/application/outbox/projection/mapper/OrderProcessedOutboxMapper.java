package com.product.ordering.application.outbox.projection.mapper;

import com.product.ordering.application.outbox.projection.OrderProcessedEventPayload;
import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.event.OrderApprovalEvent;
import com.product.ordering.domain.event.OrderProcessedEvent;
import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import com.product.ordering.system.kafka.model.projection.WarehouseApprovalMessageProjection;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class OrderProcessedOutboxMapper {

    public OrderProcessedOutboxMessage mapOrderProcessedEventToOrderProcessedOutboxMessage(OrderApprovalEvent orderApprovalEvent, UUID sagaId) {
        var orderProcessed = orderApprovalEvent.orderProcessed();

        var orderProcessedOutboxMessage = new OrderProcessedOutboxMessage();
        orderProcessedOutboxMessage.setId(orderProcessed.id().value());
        orderProcessedOutboxMessage.setSagaId(sagaId);
        orderProcessedOutboxMessage.setCreatedAt(orderApprovalEvent.createdAt());
        orderProcessedOutboxMessage.setProcessedAt(orderApprovalEvent.createdAt());
        orderProcessedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);
        orderProcessedOutboxMessage.setAggregateId(orderProcessed.id().value());
        orderProcessedOutboxMessage.setOrderApprovalStatus(orderProcessed.orderApprovalStatus());

        setAppropriatePayload(orderApprovalEvent, orderProcessedOutboxMessage, orderProcessed);

        return orderProcessedOutboxMessage;
    }

    private void setAppropriatePayload(OrderApprovalEvent orderApprovalEvent,
                                       OrderProcessedOutboxMessage orderProcessedOutboxMessage,
                                       OrderProcessed orderProcessed) {

        if (orderApprovalEvent instanceof OrderRejectedEvent orderRejectedEvent) {
            orderProcessedOutboxMessage.setPayload(mapOrderRejectedEventToOrderProcessedEventPayload(orderRejectedEvent, orderProcessed));
        } else if (orderApprovalEvent instanceof OrderProcessedEvent){
            orderProcessedOutboxMessage.setPayload(mapOrderProcessedEventToOrderProcessedEventPayload(orderProcessed));
        } else {
            orderProcessedOutboxMessage.setPayload(mapOrderApprvoedEventToOrderProcessedEventPayload(orderProcessed));
        }
    }

    private OrderProcessedEventPayload mapOrderRejectedEventToOrderProcessedEventPayload(OrderRejectedEvent orderRejectedEvent, OrderProcessed orderProcessed) {
        return OrderProcessedEventPayload.builder()
                .orderId(orderProcessed.id().value().toString())
                .warehouseId(orderProcessed.warehouseId().value().toString())
                .orderApprovalStatus(orderProcessed.orderApprovalStatus().name())
                .failureMessages(orderRejectedEvent.failureMessages())
                .build();
    }

    private OrderProcessedEventPayload mapOrderProcessedEventToOrderProcessedEventPayload(OrderProcessed orderProcessed) {
        return OrderProcessedEventPayload.builder()
                .orderId(orderProcessed.id().value().toString())
                .warehouseId(orderProcessed.warehouseId().value().toString())
                .orderApprovalStatus(orderProcessed.orderApprovalStatus().name())
                .build();
    }

    private OrderProcessedEventPayload mapOrderApprvoedEventToOrderProcessedEventPayload(OrderProcessed orderProcessed) {
        return OrderProcessedEventPayload.builder()
                .orderId(orderProcessed.id().value().toString())
                .warehouseId(orderProcessed.warehouseId().value().toString())
                .orderApprovalStatus(orderProcessed.orderApprovalStatus().name())
                .build();
    }

    public WarehouseApprovalEventKafkaProjection mapOrderProcessedOutboxMessageToWarehouseApprovalEventKafkaProjection(OrderProcessedOutboxMessage orderProcessedOutboxMessage) {
        var orderProcessedEventPayload = (OrderProcessedEventPayload) orderProcessedOutboxMessage.getPayload();

        var warehouseApprovalMessageProjection = WarehouseApprovalMessageProjection.builder()
                .orderId(orderProcessedEventPayload.orderId())
                .warehouseId(orderProcessedEventPayload.warehouseId())
                .orderApprovalStatus(orderProcessedEventPayload.orderApprovalStatus())
                .failureMessages(new ArrayList<>(Arrays.asList(orderProcessedEventPayload.failureMessages())))
                .build();

        return new WarehouseApprovalEventKafkaProjection(warehouseApprovalMessageProjection,
                orderProcessedEventPayload.orderId(),
                orderProcessedOutboxMessage.getCreatedAt(),
                orderProcessedOutboxMessage.getSagaId().toString());
    }
}
