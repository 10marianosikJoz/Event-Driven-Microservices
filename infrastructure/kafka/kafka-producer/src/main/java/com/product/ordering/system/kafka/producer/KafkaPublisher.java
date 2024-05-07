package com.product.ordering.system.kafka.producer;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.function.BiConsumer;

@Component
public class KafkaPublisher<V extends MessageKafkaProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, V> kafkaTemplate;

    KafkaPublisher(final KafkaTemplate<String, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final String topicName,
                     final String key,
                     final V message,
                     final BiConsumer<SendResult<String, V>, Throwable> callback) {

        LOGGER.info("Sending message = {}, to topic = {}", message, topicName);
        try {
            var kafkaCompletableFutureResult = kafkaTemplate.send(topicName, key, message);
            kafkaCompletableFutureResult.whenComplete(callback);
        } catch (KafkaException e) {
            LOGGER.info("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, e.getMessage());
            throw new KafkaProducerException("Error on kafka producer with key: " + key + " and message: " + message);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOGGER.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }
}
