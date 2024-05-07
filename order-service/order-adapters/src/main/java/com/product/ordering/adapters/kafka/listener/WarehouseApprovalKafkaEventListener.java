package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.system.kafka.consumer.configuration.KafkaConsumer;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WarehouseApprovalKafkaEventListener implements KafkaConsumer<WarehouseApprovalEventKafkaProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseApprovalKafkaEventListener.class);

    private final WarehouseApprovalKafkaEventListenerHelper warehouseApprovalKafkaEventListenerHelper;

    WarehouseApprovalKafkaEventListener(final WarehouseApprovalKafkaEventListenerHelper warehouseApprovalKafkaEventListenerHelper) {
        this.warehouseApprovalKafkaEventListenerHelper = warehouseApprovalKafkaEventListenerHelper;
    }

    @Override
    @KafkaListener(topics = "${order-service.warehouse-approval-events-topic-name}")
    public void receive(@Payload List<WarehouseApprovalEventKafkaProjection> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOGGER.info("Received WarehouseApprovalEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                    messages.size(), keys, partitions, offsets);

        warehouseApprovalKafkaEventListenerHelper.handleWarehouseApprovalEventData(messages);
    }
}
