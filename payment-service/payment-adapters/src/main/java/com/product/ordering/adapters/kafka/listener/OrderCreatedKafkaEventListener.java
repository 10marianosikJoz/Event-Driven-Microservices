package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.system.kafka.consumer.configuration.KafkaConsumer;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderCreatedKafkaEventListener implements KafkaConsumer<OrderCreatedEventKafkaProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedKafkaEventListener.class);

    private final OrderCreatedKafkaEventListenerHelper orderCreatedKafkaEventListenerHelper;

    OrderCreatedKafkaEventListener(final OrderCreatedKafkaEventListenerHelper orderCreatedKafkaEventListenerHelper) {
        this.orderCreatedKafkaEventListenerHelper = orderCreatedKafkaEventListenerHelper;
    }

    @Override
    @KafkaListener(topics = "${payment-service.order-created-events-topic-name}")
    public void receive(@Payload List<OrderCreatedEventKafkaProjection> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOGGER.info("Received OrderCreatedEvents {} messages with keys:{}, partitions:{} and offsets: {}",
                    messages.size(),
                    keys,
                    partitions,
                    offsets);

        orderCreatedKafkaEventListenerHelper.handleOrderCreateEventData(messages);
    }
}
