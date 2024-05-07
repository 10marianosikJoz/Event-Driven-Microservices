package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.OrderServiceConfiguration;
import com.product.ordering.application.ports.output.publisher.OrderCancellingEventPublisher;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderCancellingKafkaEventPublisher implements OrderCancellingEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCancellingKafkaEventPublisher.class);

    private final KafkaPublisher<OrderCancellingEventKafkaProjection> kafkaPublisher;
    private final OrderServiceConfiguration orderServiceConfiguration;
    private final OutputMessageKafkaMapper outputMessageKafkaMapper;
    private final KafkaCallbackHelper kafkaCallbackHelper;

    OrderCancellingKafkaEventPublisher(final KafkaPublisher<OrderCancellingEventKafkaProjection> kafkaPublisher,
                                       final OrderServiceConfiguration orderServiceConfiguration,
                                       final OutputMessageKafkaMapper outputMessageKafkaMapper,
                                       final KafkaCallbackHelper kafkaCallbackHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.orderServiceConfiguration = orderServiceConfiguration;
        this.outputMessageKafkaMapper = outputMessageKafkaMapper;
        this.kafkaCallbackHelper = kafkaCallbackHelper;
    }

    @Override
    public void publish(OrderCancellingEvent orderCancellingEvent) {
        var orderId = orderCancellingEvent.getOrder().id().value().toString();
        LOGGER.info("OrderCancellingEvent received. Order id : {}", orderId);

        try {
            var orderCancellingEventKafkaDto = outputMessageKafkaMapper.mapOrderCancellingEventToOrderCancellingEventKafkaDto(orderCancellingEvent);

            kafkaPublisher.send(orderServiceConfiguration.getOrderCancellingEventsTopicName(),
                    orderId,
                    orderCancellingEventKafkaDto,
                    kafkaCallbackHelper.kafkaCallback(orderServiceConfiguration.getOrderCancellingEventsTopicName(),
                                                      orderId,
                                                      orderCancellingEventKafkaDto.getClass().getSimpleName()));
        } catch(Exception e) {
            LOGGER.info("Error during sending OrderCancellingEvent message to kafka. Order id: {}, error: {}",
                        orderId,
                        e.getMessage());
        }
    }
}
