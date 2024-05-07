package com.product.ordering.system.kafka.consumer.configuration;

import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;

import java.util.List;

public interface KafkaConsumer<T extends MessageKafkaProjection> {

    void receive(List<T> messages,
                 List<String> keys,
                 List<Integer> partitions,
                 List<Long> offsets);
}
