package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.ports.output.publisher.OrderCreatedEventPublisher;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderCreatedKafkaEventPublisher implements OrderCreatedEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedKafkaEventPublisher.class);

    private final KafkaPublisher<OrderCreatedEventKafkaProjection> kafkaPublisher;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    OrderCreatedKafkaEventPublisher(final KafkaPublisher<OrderCreatedEventKafkaProjection> kafkaPublisher,
                                    final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                    final OrderServiceConfiguration orderServiceConfiguration,
                                    final KafkaCallbackHelper kafkaCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        var orderId = orderCreatedEvent.getOrder().id().value().toString();
        LOGGER.info("OrderCreatedEvent received. Order id : {}", orderId);

        try {
            var orderCreatedEventDtoKafka = outputMessageKafkaMapper.mapOrderCreatedEventToOrderCreatedEventKafkaDto(orderCreatedEvent);

            kafkaPublisher.send(orderServiceConfiguration.getOrderCreatedEventsTopicName(),
                                orderId,
                                orderCreatedEventDtoKafka,
                                kafkaCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderCreatedEventsTopicName(),
                                                                  orderId,
                                                                  orderCreatedEventDtoKafka.getClass().getSimpleName()));

        } catch (Exception e) {
            LOGGER.error("Error while sending OrderCreatedEvent message to kafka. Order id: {} error: {}",
                    orderId,
                    e.getMessage());
        }
    }
}
