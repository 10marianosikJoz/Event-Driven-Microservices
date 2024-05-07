package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.ports.output.publisher.OrderPaidEventPublisher;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderPaidKafkaEventPublisher implements OrderPaidEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaidKafkaEventPublisher.class);

    private final KafkaPublisher<OrderPaidEventKafkaProjection> kafkaPublisher;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    OrderPaidKafkaEventPublisher(final KafkaPublisher<OrderPaidEventKafkaProjection> kafkaPublisher,
                                 final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                 final OrderServiceConfiguration orderServiceConfiguration,
                                 final KafkaCallbackHelper kafkaCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderPaidEvent orderPaidEvent) {
        var orderId = orderPaidEvent.getOrder().id().value().toString();

        try {
            var orderPaidEventKafkaProjection = outputMessageKafkaMapper.mapOrderPaidEventToOrderPaidEventKafkaProjection(orderPaidEvent);
            LOGGER.info("OrderPaidEvent received. Order id : {}", orderId);

            kafkaPublisher.send(orderServiceConfiguration.getOrderPaidEventsTopicName(),
                                orderId,
                                orderPaidEventKafkaProjection,
                                kafkaCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderPaidEventsTopicName(),
                                                                  orderId,
                                                                  orderPaidEventKafkaProjection.getClass().getSimpleName()));

        } catch (Exception e) {
            LOGGER.error("Error during sending OrderPaidEvent message to kafka. Order id: {}, error: {}",
                    orderId,
                    e.getMessage());
        }
    }
}
