package com.product.ordering.system.kafka.message.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageKafkaProjectionJsonDeserializer implements Deserializer<TypeProjection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageKafkaProjectionJsonDeserializer.class);

    private final ObjectMapper objectMapper;

    private JsonDeserializer<MessageKafkaProjection> messageKafkaJsonDeserializer;

    MessageKafkaProjectionJsonDeserializer(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        messageKafkaProjectionDeserializer().configure(configs, isKey);
    }

    @Override
    public TypeProjection deserialize(final String topic, final byte[] data) {
        if (data == null) {
            LOGGER.error("Received empty message to deserializing");
            return null;
        }
        try {
            var item = messageKafkaProjectionDeserializer().deserialize(topic, data);
            var clazz = Class.forName(item.type());
            return objectMapper.readerFor(clazz)
                                .readValue(data);
        } catch (Exception e) {
            throw new SerializationException("Cannot deserialize kafka message to MessageKafkaProjection. Message: " + new String(data));
        }
    }

    @Override
    public void close() {
        messageKafkaProjectionDeserializer().close();
    }

    private JsonDeserializer<MessageKafkaProjection> messageKafkaProjectionDeserializer() {
        return this.messageKafkaJsonDeserializer != null ?
               this.messageKafkaJsonDeserializer :
               new JsonDeserializer<>(MessageKafkaProjection.class, objectMapper);
    }
}
