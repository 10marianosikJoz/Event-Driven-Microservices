package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.system.kafka.consumer.configuration.KafkaConsumer;
import com.product.ordering.system.kafka.model.event.PaymentStatusEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class PaymentStatusKafkaEventListener implements KafkaConsumer<PaymentStatusEventKafkaProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentStatusKafkaEventListener.class);

    private final PaymentStatusKafkaEventListenerHelper paymentStatusKafkaEventListenerHelper;

    PaymentStatusKafkaEventListener(final PaymentStatusKafkaEventListenerHelper paymentStatusKafkaEventListenerHelper) {
        this.paymentStatusKafkaEventListenerHelper = paymentStatusKafkaEventListenerHelper;
    }

    @Override
    @KafkaListener(topics = "${order-service.payment-status-events-topic-name}")
    public void receive(@Payload List<PaymentStatusEventKafkaProjection> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOGGER.info("Received PaymentStatusEvent {} messages with keys:{}, partitions:{} and offsets: {}",
                    messages.size(), keys, partitions, offsets);

        paymentStatusKafkaEventListenerHelper.handlePaymentStatusEventData(messages);
    }
}
