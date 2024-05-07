package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.WarehouseServiceConfiguration;
import com.product.ordering.application.ports.output.publisher.OrderApprovedMessagePublisher;
import com.product.ordering.domain.event.OrderApprovedEvent;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderApprovedKafkaEventPublisher implements OrderApprovedMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderApprovedKafkaEventPublisher.class);

    private final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher;
    private final WarehouseServiceConfiguration warehouseServiceConfiguration;
    private final WarehouseApprovalMapper warehouseApprovalMapper;
    private final KafkaCallbackHelper kafkaMessageHelper;

    OrderApprovedKafkaEventPublisher(final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher,
                                     final WarehouseServiceConfiguration warehouseServiceConfiguration,
                                     final WarehouseApprovalMapper warehouseApprovalMapper,
                                     final KafkaCallbackHelper kafkaMessageHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.warehouseServiceConfiguration = warehouseServiceConfiguration;
        this.warehouseApprovalMapper = warehouseApprovalMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderApprovedEvent orderApprovedEvent) {
        var orderId = orderApprovedEvent.orderProcessed().id().toString();

        try {
            var warehouseApprovedEventKafkaProjection = warehouseApprovalMapper.mapOrderApprovedEventToWarehouseApprovalEventKafkaProjection(orderApprovedEvent);
            kafkaPublisher.send(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                orderId,
                                warehouseApprovedEventKafkaProjection,
                                kafkaMessageHelper.kafkaCallback(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                                                 orderId,
                                                                 warehouseApprovedEventKafkaProjection.getClass().getName()));
        } catch (Exception e) {
            LOGGER.error("Error during sending WarehouseApprovalEvent to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
