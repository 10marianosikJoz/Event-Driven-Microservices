package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.system.kafka.model.projection.PaymentMessageProjection;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OutputMessageKafkaMapper {

    PaymentStatusEventKafkaProjection mapPaymentEventToPaymentStatusEventKafkaDto(PaymentEvent paymentEvent) {
        var payment = paymentEvent.payment();
        var paymentDto = buildPaymentMessageDto(payment);

        return new PaymentStatusEventKafkaProjection(paymentDto, payment.id().toString(), payment.createdAt());
    }

    private PaymentMessageProjection buildPaymentMessageDto(Payment payment) {
        return PaymentMessageProjection.builder()
                .paymentId(payment.id().value().toString())
                .customerId(payment.customerId().value().toString())
                .orderId(payment.orderId().value().toString())
                .price(payment.price().amount())
                .createdAt(payment.createdAt())
                .paymentStatus(payment.paymentStatus().toString())
                .failureMessages(List.of())
                .build();
    }
}
