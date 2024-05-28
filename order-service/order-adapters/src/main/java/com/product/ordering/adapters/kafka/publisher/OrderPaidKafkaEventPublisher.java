package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.outbox.projection.OrderPaidOutboxMessage;
import com.product.ordering.application.ports.output.publisher.OrderPaidEventPublisher;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaMessageCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class OrderPaidKafkaEventPublisher implements OrderPaidEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaidKafkaEventPublisher.class);

    private final KafkaPublisher<OrderPaidEventKafkaProjection> kafkaPublisher;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final KafkaMessageCallbackHelper kafkaMessageCallbackHelper;

    OrderPaidKafkaEventPublisher(final KafkaPublisher<OrderPaidEventKafkaProjection> kafkaPublisher,
                                 final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                 final OrderServiceConfiguration orderServiceConfiguration,
                                 final KafkaMessageCallbackHelper kafkaMessageCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.kafkaMessageCallbackHelper = kafkaMessageCallbackHelper;
    }

    public void publish(OrderPaidOutboxMessage orderPaidOutboxMessage, Consumer<OrderPaidOutboxMessage> outboxCallback) {
        var sagaId = orderPaidOutboxMessage.getSagaId().toString();
        var orderId = orderPaidOutboxMessage.getAggregateId().toString();

        try {
            var orderPaidEventKafkaProjection = outputMessageKafkaMapper.mapOrderPaidOutboxMessageToOrderPaidEventKafkaProjection(orderPaidOutboxMessage);
            LOGGER.info("OrderPaidEvent received. Order id: {}, sagaId: {}", orderId, sagaId);

            kafkaPublisher.send(orderServiceConfiguration.getOrderPaidEventsTopicName(),
                                sagaId,
                                orderPaidEventKafkaProjection,
                                kafkaMessageCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderPaidEventsTopicName(),
                                                                         orderId,
                                                                         orderPaidEventKafkaProjection.getClass().getSimpleName(),
                                                                         orderPaidOutboxMessage,
                                                                         outboxCallback));

        } catch (Exception e) {
            LOGGER.error("Error during sending OrderPaidEvent message to kafka. Order id: {}, error: {}",
                    orderId,
                    e.getMessage());
        }
    }
}
