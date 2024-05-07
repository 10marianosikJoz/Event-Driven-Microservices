package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.application.configuration.WarehouseServiceConfiguration;
import com.product.ordering.application.ports.output.publisher.OrderRejectedMessagePublisher;
import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import com.product.ordering.system.kafka.producer.KafkaCallbackHelper;
import com.product.ordering.system.kafka.producer.KafkaPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderRejectedKafkaEventPublisher implements OrderRejectedMessagePublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRejectedKafkaEventPublisher.class);

    private final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher;
    private final WarehouseServiceConfiguration warehouseServiceConfiguration;
    private final WarehouseApprovalMapper warehouseApprovalMapper;
    private final KafkaCallbackHelper kafkaMessageHelper;

    OrderRejectedKafkaEventPublisher(final KafkaPublisher<WarehouseApprovalEventKafkaProjection> kafkaPublisher,
                                     final WarehouseServiceConfiguration warehouseServiceConfiguration,
                                     final WarehouseApprovalMapper warehouseApprovalMapper,
                                     final KafkaCallbackHelper kafkaMessageHelper) {

        this.kafkaPublisher = kafkaPublisher;
        this.warehouseServiceConfiguration = warehouseServiceConfiguration;
        this.warehouseApprovalMapper = warehouseApprovalMapper;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(OrderRejectedEvent orderRejectedEvent) {
        var orderId = orderRejectedEvent.orderProcessed().id().toString();

        try {
            var warehouseApprovedEventKafkaDto = warehouseApprovalMapper.mapOrderRejectedEventToWarehouseApprovalEventKafkaProjection(orderRejectedEvent);
            kafkaPublisher.send(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                orderId,
                                warehouseApprovedEventKafkaDto,
                                kafkaMessageHelper.kafkaCallback(warehouseServiceConfiguration.getWarehouseApprovalEventsTopicName(),
                                orderId,
                                warehouseApprovedEventKafkaDto.getClass().getName()));
        } catch (Exception e) {
            LOGGER.error("Error during sending WarehouseApprovalEvent to kafka. Order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}
