package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.event.WarehouseApprovalEvent;
import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import org.springframework.stereotype.Component;

@Component
class InputMessageKafkaMapper {

    WarehouseApprovalEvent mapWarehouseApprovalEventKafkaProjectionToWarehouseApprovalEvent(WarehouseApprovalEventKafkaProjection warehouseApprovalEventKafkaProjection) {
        var warehouseApprovalDto = warehouseApprovalEventKafkaProjection.getData();

        return WarehouseApprovalEvent.builder()
                .id(warehouseApprovalEventKafkaProjection.getMessageId())
                .createdAt(warehouseApprovalEventKafkaProjection.createdAt())
                .orderId(warehouseApprovalDto.orderId())
                .warehouseId(warehouseApprovalDto.warehouseId())
                .orderApprovalStatus(OrderApprovalStatus.valueOf(warehouseApprovalDto.orderApprovalStatus()))
                .failureMessages(warehouseApprovalDto.failureMessages())
                .build();
    }

    PaymentStatusEvent mapPaymentStatusEventKafkaDtoToPaymentStatusEvent(PaymentStatusEventKafkaProjection paymentStatusEventKafkaProjection) {
        var paymentStatusDto = paymentStatusEventKafkaProjection.getData();

        return PaymentStatusEvent.builder()
                .id(paymentStatusEventKafkaProjection.getMessageId())
                .createdAt(paymentStatusEventKafkaProjection.createdAt())
                .orderId(paymentStatusDto.orderId())
                .paymentId(paymentStatusDto.paymentId())
                .customerId(paymentStatusDto.customerId())
                .paymentStatus(PaymentStatus.valueOf(paymentStatusDto.paymentStatus()))
                .price(paymentStatusDto.price())
                .failureMessages(paymentStatusDto.failureMessages())
                .build();

    }
}
