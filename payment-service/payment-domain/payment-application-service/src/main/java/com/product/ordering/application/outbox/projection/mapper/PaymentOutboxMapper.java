package com.product.ordering.application.outbox.projection.mapper;

import com.product.ordering.application.outbox.projection.*;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import com.product.ordering.system.kafka.model.projection.PaymentMessageProjection;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class PaymentOutboxMapper {

    public PaymentCancelledOutboxMessage mapPaymentEventToPaymentCancelledOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        var payment = paymentEvent.payment();
        var paymentCancelledOutboxMessage = new PaymentCancelledOutboxMessage();

        paymentCancelledOutboxMessage.setId(UUID.randomUUID());
        paymentCancelledOutboxMessage.setSagaId(sagaId);
        paymentCancelledOutboxMessage.setCreatedAt(payment.createdAt());
        paymentCancelledOutboxMessage.setProcessedAt(payment.createdAt());
        paymentCancelledOutboxMessage.setPayload(preparePaymentStatusEventPayload(payment));
        paymentCancelledOutboxMessage.setAggregateId(payment.id().value());
        paymentCancelledOutboxMessage.setPaymentStatus(payment.paymentStatus());
        paymentCancelledOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);

        return paymentCancelledOutboxMessage;
    }

    public PaymentCompletedOutboxMessage mapPaymentEventToPaymentCompletedOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        var payment = paymentEvent.payment();
        var paymentCompletedOutboxMessage = new PaymentCompletedOutboxMessage();

        paymentCompletedOutboxMessage.setId(UUID.randomUUID());
        paymentCompletedOutboxMessage.setSagaId(sagaId);
        paymentCompletedOutboxMessage.setCreatedAt(payment.createdAt());
        paymentCompletedOutboxMessage.setProcessedAt(payment.createdAt());
        paymentCompletedOutboxMessage.setPayload(preparePaymentStatusEventPayload(payment));
        paymentCompletedOutboxMessage.setAggregateId(payment.id().value());
        paymentCompletedOutboxMessage.setPaymentStatus(payment.paymentStatus());
        paymentCompletedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);

        return paymentCompletedOutboxMessage;
    }

    public PaymentRejectedOutboxMessage mapPaymentEventToPaymentRejectedOutboxMessage(PaymentEvent paymentEvent, UUID sagaId) {
        var payment = paymentEvent.payment();
        var paymentRejectedOutboxMessage = new PaymentRejectedOutboxMessage();

        paymentRejectedOutboxMessage.setId(UUID.randomUUID());
        paymentRejectedOutboxMessage.setSagaId(sagaId);
        paymentRejectedOutboxMessage.setCreatedAt(payment.createdAt());
        paymentRejectedOutboxMessage.setProcessedAt(payment.createdAt());
        paymentRejectedOutboxMessage.setPayload(preparePaymentStatusEventPayload(payment));
        paymentRejectedOutboxMessage.setAggregateId(payment.id().value());
        paymentRejectedOutboxMessage.setPaymentStatus(payment.paymentStatus());
        paymentRejectedOutboxMessage.setOutboxStatus(OutboxStatus.STARTED);

        return paymentRejectedOutboxMessage;
    }

    private PaymentStatusEventPayload preparePaymentStatusEventPayload(Payment payment) {
        return PaymentStatusEventPayload.builder()
                .paymentId(payment.id().value().toString())
                .createdAt(payment.createdAt())
                .orderId(payment.orderId().value().toString())
                .customerId(payment.customerId().value().toString())
                .paymentStatus(payment.paymentStatus().name())
                .price(payment.price().amount())
                .build();
    }

    public PaymentStatusEventKafkaProjection mapPaymentOutboxMessageToPaymentStatusEventKafkaProjection(PaymentOutboxMessage paymentOutboxMessage) {
        var paymentStatusEventPayload = (PaymentStatusEventPayload) paymentOutboxMessage.getPayload();
        var paymentMessageProjection = mapPaymentStatusEventPayloadToPaymentMessageProjection(paymentStatusEventPayload);

        return new PaymentStatusEventKafkaProjection(paymentMessageProjection, paymentStatusEventPayload.paymentId(), paymentOutboxMessage.getCreatedAt(), paymentOutboxMessage.getSagaId().toString());
    }

    private PaymentMessageProjection mapPaymentStatusEventPayloadToPaymentMessageProjection(PaymentStatusEventPayload paymentStatusEventPayload) {
        return PaymentMessageProjection.builder()
                .paymentId(paymentStatusEventPayload.paymentId())
                .orderId(paymentStatusEventPayload.orderId())
                .customerId(paymentStatusEventPayload.customerId())
                .price(paymentStatusEventPayload.price())
                .paymentStatus(paymentStatusEventPayload.paymentStatus())
                .failureMessages(new ArrayList<>(Arrays.asList(paymentStatusEventPayload.failureMessages())))
                .build();
    }
}
