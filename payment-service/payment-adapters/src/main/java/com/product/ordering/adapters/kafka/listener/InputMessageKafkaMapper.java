package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class InputMessageKafkaMapper {

    CompletePaymentCommand mapOrderEventKafkaProjectionToCompletePaymentCommand(OrderCreatedEventKafkaProjection orderCreatedEventKafkaProjection) {
        var orderMessageProjection = orderCreatedEventKafkaProjection.getData();

        return CompletePaymentCommand.builder()
                .paymentId(UUID.fromString(orderCreatedEventKafkaProjection.getMessageId()))
                .orderId(UUID.fromString(orderMessageProjection.orderId()))
                .customerId(UUID.fromString(orderMessageProjection.customerId()))
                .sagaId(UUID.fromString(orderCreatedEventKafkaProjection.getSagaId()))
                .price(orderMessageProjection.price())
                .build();
    }

    CancelPaymentCommand mapOrderEventKafkaProjectionToCancelPaymentCommand(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        var orderMessageProjection = orderCancellingEventKafkaProjection.getData();

        return CancelPaymentCommand.builder()
                .paymentId(UUID.fromString(orderCancellingEventKafkaProjection.getMessageId()))
                .orderId(UUID.fromString(orderMessageProjection.orderId()))
                .customerId(UUID.fromString(orderMessageProjection.customerId()))
                .sagaId(UUID.fromString(orderCancellingEventKafkaProjection.getSagaId()))
                .price(orderMessageProjection.price())
                .build();
    }
}
