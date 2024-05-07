package com.product.ordering.system.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class KafkaCallbackHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCallbackHelper.class);

    public <T> BiConsumer<SendResult<String, T>, Throwable> kafkaCallback(final String topicName,
                                                                          final String itemId,
                                                                          final String messageProjectionName) {

        return (result, exception) -> {
            if (exception == null) {
                var metadata = result.getRecordMetadata();

                LOGGER.info("Send to kafka: {} itemId: {} to topic: {}. Partition: {} offset: {} timestamp: {}", messageProjectionName,
                                                                                                                 itemId,
                                                                                                                 metadata.topic(),
                                                                                                                 metadata.partition(),
                                                                                                                 metadata.offset(),
                                                                                                                 metadata.timestamp());
            } else {
                LOGGER.error("Error during sending {} itemId: {} to topic: {}", messageProjectionName,
                                                                                itemId,
                                                                                topicName,
                                                                                exception);
            }
        };
    }
}
