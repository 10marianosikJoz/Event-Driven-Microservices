package com.product.ordering.entities.entity.outbox;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.system.outbox.entity.OutboxEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class PaymentOutboxEntityMapper extends OutboxEntityMapper<PaymentOutboxEntity> {

    public PaymentOutboxEntity mapPaymentOutboxMessageToPaymentOutboxEntity(PaymentOutboxMessage paymentOutboxMessage) {
        var paymentOutboxEntity = super.mapOutboxMessageToOutboxEntity(paymentOutboxMessage, PaymentOutboxEntity.class);
        paymentOutboxEntity.setMessageType(PaymentOutboxMessage.class.getSimpleName());
        paymentOutboxEntity.setPaymentStatus(paymentOutboxMessage.getPaymentStatus());

        return paymentOutboxEntity;
    }

    public <G extends PaymentOutboxMessage> G mapPaymentOutboxEntityToPaymentOutboxMessage(PaymentOutboxEntity paymentOutboxEntity, Class<G> outboxMessageClazz) {
        var outboxMessage = super.mapOutboxEntityToOutboxMessage(paymentOutboxEntity, outboxMessageClazz);
        outboxMessage.setPaymentStatus(paymentOutboxEntity.getPaymentStatus());

        return outboxMessage;
    }
}
