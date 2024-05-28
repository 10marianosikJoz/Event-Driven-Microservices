package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import com.product.ordering.system.kafka.model.projection.PaymentMessageProjection;
import com.product.ordering.system.kafka.model.projection.WarehouseApprovalMessageProjection;
import org.springframework.stereotype.Component;

@Component
class InputMessageKafkaMapper {

    WarehouseApprovalEvent mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
        var warehouseApprovalProjection = warehouseApprovalEventKafkaProjection.getData();

        return WarehouseApprovalEvent.builder()
                .id(warehouseApprovalEventKafkaProjection.getMessageId())
                .createdAt(warehouseApprovalEventKafkaProjection.createdAt())
                .orderId(warehouseApprovalProjection.orderId())
                .sagaId(warehouseApprovalEventKafkaProjection.getSagaId())
                .warehouseId(warehouseApprovalProjection.warehouseId())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(warehouseApprovalProjection.orderApprovalStatus()))
                .failureMessages(warehouseApprovalProjection.failureMessages())
                .build();
    }

    PaymentStatusEvent mapPaymentStatusEventKafkaProjectionToPaymentStatusEvent(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
        var paymentStatusProjection = paymentStatusEventKafkaProjection.getData();

        return PaymentStatusEvent.builder()
                .id(paymentStatusEventKafkaProjection.getMessageId())
                .createdAt(paymentStatusEventKafkaProjection.createdAt())
                .orderId(paymentStatusProjection.orderId())
                .sagaId(paymentStatusEventKafkaProjection.getSagaId())
                .paymentId(paymentStatusProjection.paymentId())
                .customerId(paymentStatusProjection.customerId())
                .paymentStatus(PaymentStatus.valueOf(paymentStatusProjection.paymentStatus()))
                .price(paymentStatusProjection.price())
                .failureMessages(paymentStatusProjection.failureMessages())
                .build();

    }
}
