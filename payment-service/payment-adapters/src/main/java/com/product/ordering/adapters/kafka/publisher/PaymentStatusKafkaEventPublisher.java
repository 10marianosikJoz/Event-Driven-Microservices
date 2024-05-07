package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.PaymentServiceConfiguration;
import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class PaymentStatusKafkaEventPublisher implements PaymentStatusMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusKafkaEventPublisher.class);

    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final KafkaPublisher<PaymentStatusEventKafkaProjection> kafkaPublisher;
    private final PaymentServiceConfiguration paymentServiceConfiguration;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    PaymentStatusKafkaEventPublisher(OutputMessageKafkaMapper outputMessageKafkaMapper,
                                     KafkaPublisher<PaymentStatusEventKafkaProjection> kafkaPublisher,
                                     PaymentServiceConfiguration paymentServiceConfiguration,
                                     KafkaCallbackHelper kafkaCallbackHelper) {

        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.kafkaPublisher = kafkaPublisher;
        this.paymentServiceConfiguration = paymentServiceConfiguration;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(PaymentEvent paymentEvent) {
        var orderId = paymentEvent.payment().orderId().value().toString();

        try {
            var paymentStatusEventKafkaDto = outputMessageKafkaMapper.mapPaymentEventToPaymentStatusEventKafkaDto(paymentEvent);

            kafkaPublisher.send(paymentServiceConfiguration.getPaymentStatusEventsTopicName(),
                                orderId,
                                paymentStatusEventKafkaDto,
                                kafkaCallbackHelper.kafkaCallback(paymentServiceConfiguration.getPaymentStatusEventsTopicName(),
                                                                  paymentEvent.payment().id().toString(),
                                                                  paymentStatusEventKafkaDto.getClass().getSimpleName()));
        } catch (Exception e) {
            LOGGER.error("Error during sending PaymentStatusEvent message to kafka. Error: {}", e.getMessage());
        }
    }
}
