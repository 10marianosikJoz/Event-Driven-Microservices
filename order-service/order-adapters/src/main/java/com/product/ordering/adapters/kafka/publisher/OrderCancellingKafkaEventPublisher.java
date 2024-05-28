package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.outbox.projection.OrderCancellingOutboxMessage;
import com.product.ordering.application.ports.output.publisher.OrderCancellingEventPublisher;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaMessageCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class OrderCancellingKafkaEventPublisher implements OrderCancellingEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCancellingKafkaEventPublisher.class);

    private final KafkaPublisher<OrderCancellingEventKafkaProjection> kafkaPublisher;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final KafkaMessageCallbackHelper kafkaMessageCallbackHelper;

    OrderCancellingKafkaEventPublisher(final KafkaPublisher<OrderCancellingEventKafkaProjection> kafkaPublisher,
                                       final OrderServiceConfiguration orderServiceConfiguration,
                                       final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                       final KafkaMessageCallbackHelper kafkaMessageCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.kafkaMessageCallbackHelper = kafkaMessageCallbackHelper;
    }

    public void publish(OrderCancellingOutboxMessage orderCancellingOutboxMessage, Consumer<OrderCancellingOutboxMessage> outboxCallback) {
        var sagaId = orderCancellingOutboxMessage.getSagaId().toString();
        var orderId = orderCancellingOutboxMessage.getAggregateId().toString();

        LOGGER.info("OrderCancellingEvent received. Order id: {}, sagaId: {}", orderId, sagaId);

        try {
            var orderCancellingEventKafkaProjection = outputMessageKafkaMapper.mapOrderCancellingOutboxMessageToOrderCancellingEventKafkaProjection(orderCancellingOutboxMessage);

            kafkaPublisher.send(orderServiceConfiguration.getOrderCancellingEventsTopicName(),
                    sagaId,
                    orderCancellingEventKafkaProjection,
                    kafkaMessageCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderCancellingEventsTopicName(),
                                                             orderId,
                                                             orderCancellingEventKafkaProjection.getClass().getSimpleName(),
                                                             orderCancellingOutboxMessage,
                                                             outboxCallback));
        } catch(Exception e) {
            LOGGER.info("Error during sending OrderCancellingEvent message to kafka. Order id: {}, error: {}",
                        orderId,
                        e.getMessage());
        }
    }
}
