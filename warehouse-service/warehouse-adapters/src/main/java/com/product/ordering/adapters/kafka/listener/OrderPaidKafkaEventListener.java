package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.system.kafka.consumer.configuration.KafkaConsumer;
import com.product.ordering.system.kafka.model.event.OrderPaidEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderPaidKafkaEventListener implements KafkaConsumer<OrderPaidEventKafkaProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaidKafkaEventListener.class);

    private final OrderPaidKafkaEventListenerHelper orderPaidKafkaEventListenerHelper;

    OrderPaidKafkaEventListener(final OrderPaidKafkaEventListenerHelper orderPaidKafkaEventListenerHelper) {
        this.orderPaidKafkaEventListenerHelper = orderPaidKafkaEventListenerHelper;
    }

    @Override
    @KafkaListener(topics = "${warehouse-service.order-paid-events-topic-name}")
    public void receive(@Payload List<OrderPaidEventKafkaProjection> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOGGER.info("Received OrderPaidEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                messages.size(),
                keys,
                partitions,
                offsets);

        orderPaidKafkaEventListenerHelper.handleOrderPaidEventData(messages);
    }
}