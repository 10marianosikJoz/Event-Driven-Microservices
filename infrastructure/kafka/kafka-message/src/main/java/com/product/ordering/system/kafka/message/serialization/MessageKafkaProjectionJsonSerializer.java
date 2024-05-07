package com.product.ordering.system.kafka.message.serialization;

import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class MessageKafkaProjectionJsonSerializer<T extends MessageKafkaProjection> extends JsonSerializer<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageKafkaProjectionJsonSerializer.class);

    @Override
    public byte[] serialize(String topic, MessageKafkaProjection data) {
        try {
            if (data == null) {
                LOGGER.error("Empty message to serializing");
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageKafkaProjection to byte[] array");
        }
    }
}
