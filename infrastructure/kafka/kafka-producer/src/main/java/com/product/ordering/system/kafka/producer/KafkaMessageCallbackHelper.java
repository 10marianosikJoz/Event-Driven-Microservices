package com.product.ordering.system.kafka.producer;

import com.product.ordering.system.outbox.model.OutboxMessage;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Component
public class KafkaMessageCallbackHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageCallbackHelper.class);

    public <T, U extends OutboxMessage> BiConsumer<SendResult<String, T>, Throwable> kafkaCallback(String topicName,
                                                                                                   String itemId,
                                                                                                   String messageProjectionName,
                                                                                                   U outboxMessage,
                                                                                                   Consumer<U> outboxCallback) {

        return (result, exception) -> {
            if (exception == null) {
                var metadata = result.getRecordMetadata();

                LOGGER.info("Send to kafka: {} itemId: {} to topic: {}. Partition: {} offset: {} timestamp: {}", messageProjectionName,
                                                                                                                 itemId,
                                                                                                                 metadata.topic(),
                                                                                                                 metadata.partition(),
                                                                                                                 metadata.offset(),
                                                                                                                 metadata.timestamp());
                outboxMessage.setCreatedAt(Instant.now());
                outboxMessage.setOutboxStatus(OutboxStatus.COMPLETED);
                outboxCallback.accept(outboxMessage);

            } else {
                LOGGER.error("Error during sending {} itemId: {} to topic: {}", messageProjectionName,
                                                                                itemId,
                                                                                topicName,
                                                                                exception);

                outboxMessage.setOutboxStatus(OutboxStatus.FAILED);
                outboxCallback.accept(outboxMessage);
            }
        };
    }
}
