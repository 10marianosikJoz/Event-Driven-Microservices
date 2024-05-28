package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.PaymentServiceConfiguration;
import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.PaymentOutboxMapper;
import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaMessageCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class PaymentStatusKafkaEventPublisher implements PaymentStatusMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusKafkaEventPublisher.class);

    private final KafkaPublisher<PaymentStatusEventKafkaProjection> kafkaPublisher;
    private final PaymentServiceConfiguration paymentServiceConfiguration;
    private final KafkaMessageCallbackHelper kafkaMessageCallbackHelper;
    private final PaymentOutboxMapper paymentOutboxMapper;

    PaymentStatusKafkaEventPublisher(final KafkaPublisher<PaymentStatusEventKafkaProjection> kafkaPublisher,
                                     final PaymentServiceConfiguration paymentServiceConfiguration,
                                     final KafkaMessageCallbackHelper kafkaMessageCallbackHelper,
                                     final PaymentOutboxMapper paymentOutboxMapper) {

        this.kafkaPublisher = kafkaPublisher;
        this.paymentServiceConfiguration = paymentServiceConfiguration;
        this.kafkaMessageCallbackHelper = kafkaMessageCallbackHelper;
        this.paymentOutboxMapper = paymentOutboxMapper;
    }

    @Override
    public void publish(PaymentOutboxMessage paymentOutboxMessage, Consumer<PaymentOutboxMessage> outboxCallback) {
        var sagaId = paymentOutboxMessage.getSagaId().toString();

        try {
            var paymentStatusEventKafkaProjection = paymentOutboxMapper.mapPaymentOutboxMessageToPaymentStatusEventKafkaProjection(paymentOutboxMessage);

            kafkaPublisher.send(paymentServiceConfiguration.getPaymentStatusEventsTopicName(),
                                sagaId,
                                paymentStatusEventKafkaProjection,
                                kafkaMessageCallbackHelper.kafkaCallback(paymentServiceConfiguration.getPaymentStatusEventsTopicName(),
                                                                         paymentOutboxMessage.getAggregateId().toString(),
                                                                         paymentStatusEventKafkaProjection.getClass().getSimpleName(),
                                                                         paymentOutboxMessage,
                                                                         outboxCallback));
        } catch (Exception e) {
            LOGGER.error("Error during sending PaymentStatusEvent message to kafka. Error: {}", e.getMessage());
        }
    }
}
