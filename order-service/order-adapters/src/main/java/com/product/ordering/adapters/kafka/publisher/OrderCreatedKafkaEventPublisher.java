package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.outbox.projection.OrderCreatedOutboxMessage;
import com.product.ordering.application.ports.output.publisher.OrderCreatedEventPublisher;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaMessageCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class OrderCreatedKafkaEventPublisher implements OrderCreatedEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedKafkaEventPublisher.class);

    private final KafkaPublisher<OrderCreatedEventKafkaProjection> kafkaPublisher;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final KafkaMessageCallbackHelper kafkaMessageCallbackHelper;

    OrderCreatedKafkaEventPublisher(final KafkaPublisher<OrderCreatedEventKafkaProjection> kafkaPublisher,
                                    final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                    final OrderServiceConfiguration orderServiceConfiguration,
                                    final KafkaMessageCallbackHelper kafkaMessageCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.kafkaMessageCallbackHelper = kafkaMessageCallbackHelper;
    }

    public void publish(OrderCreatedOutboxMessage orderCreatedOutboxMessage, Consumer<OrderCreatedOutboxMessage> outboxCallback) {
        var sagaId = orderCreatedOutboxMessage.getSagaId().toString();
        var orderId = orderCreatedOutboxMessage.getAggregateId().toString();

        LOGGER.info("OrderCreatedEvent received. Order id: {}, sagaId: {}", orderId, sagaId);

        try {
            var orderCreatedEventKafkaProjection = outputMessageKafkaMapper.mapOrderCreatedOutboxMessageToOrderCreatedEventKafkaProjection(orderCreatedOutboxMessage);

            kafkaPublisher.send(orderServiceConfiguration.getOrderCreatedEventsTopicName(),
                                sagaId,
                                orderCreatedEventKafkaProjection,
                                kafkaMessageCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderCreatedEventsTopicName(),
                                                                         orderId,
                                                                         orderCreatedEventKafkaProjection.getClass().getSimpleName(),
                                                                         orderCreatedOutboxMessage,
                                                                         outboxCallback));

        } catch (Exception e) {
            LOGGER.error("Error while sending OrderCreatedEvent message to kafka. Order id: {} error: {}",
                    orderId,
                    e.getMessage());
        }
    }
}
