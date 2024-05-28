package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.WarehouseServiceConfiguration;
import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.OrderProcessedOutboxMapper;
import com.product.ordering.application.ports.output.publisher.OrderApprovedMessagePublisher;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaMessageCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
class OrderApprovedKafkaEventPublisher implements OrderApprovedMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApprovedKafkaEventPublisher.class);

    private final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher;
    private final WarehouseServiceConfiguration warehouseServiceConfiguration;
    private final KafkaMessageCallbackHelper kafkaMessageHelper;
    private final OrderProcessedOutboxMapper orderProcessedOutboxMapper;

    OrderApprovedKafkaEventPublisher(final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher,
                                     final WarehouseServiceConfiguration warehouseServiceConfiguration,
                                     final KafkaMessageCallbackHelper kafkaMessageHelper,
                                     final OrderProcessedOutboxMapper orderProcessedOutboxMapper) {

        this.kafkaPublisher = kafkaPublisher;
        this.warehouseServiceConfiguration = warehouseServiceConfiguration;
        this.kafkaMessageHelper = kafkaMessageHelper;
        this.orderProcessedOutboxMapper = orderProcessedOutboxMapper;
    }

    public void publish(OrderProcessedOutboxMessage orderOutboxMessage, Consumer<OrderProcessedOutboxMessage> outboxCallback) {
        var orderId = orderOutboxMessage.getAggregateId().toString();
        var sagaId = orderOutboxMessage.getSagaId().toString();

        try {
            var warehouseApprovedEventKafkaProjection = orderProcessedOutboxMapper.mapOrderProcessedOutboxMessageToWarehouseApprovalEventKafkaProjection(orderOutboxMessage);
            kafkaPublisher.send(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                orderId,
                                warehouseApprovedEventKafkaProjection,
                                kafkaMessageHelper.kafkaCallback(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                                                 orderId,
                                                                 warehouseApprovedEventKafkaProjection.getClass().getName(),
                                                                 orderOutboxMessage,
                                                                 outboxCallback));
        } catch (Exception e) {
            LOGGER.error("Error during sending WarehouseApprovalEvent to kafka. Order id: {}, SagaId : {}, error: {}", orderId,
                                                                                                                       sagaId,
                                                                                                                       e.getMessage());
        }
    }
}
