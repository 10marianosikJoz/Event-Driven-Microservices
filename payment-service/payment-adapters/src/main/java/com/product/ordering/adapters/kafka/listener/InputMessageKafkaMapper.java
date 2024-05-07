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
        var orderMessageDto = orderCreatedEventKafkaProjection.getData();

        return CompletePaymentCommand.builder()
                .orderId(UUID.fromString(orderMessageDto.orderId()))
                .customerId(UUID.fromString(orderMessageDto.customerId()))
                .price(orderMessageDto.price())
                .build();
    }

    CancelPaymentCommand mapOrderEventKafkaProjectionToCancelPaymentCommand(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        var orderMessageDto = orderCancellingEventKafkaProjection.getData();

        return CancelPaymentCommand.builder()
                .orderId(UUID.fromString(orderMessageDto.orderId()))
                .customerId(UUID.fromString(orderMessageDto.customerId()))
                .price(orderMessageDto.price())
                .build();
    }
}
