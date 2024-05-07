package com.product.ordering.system.kafka.producer;

import com.product.ordering.system.kafka.configuration.KafkaConfigurationData;
import com.product.ordering.system.kafka.configuration.KafkaProducerConfigurationData;
import com.product.ordering.system.kafka.message.serialization.MessageKafkaDtoJsonSerializer;
import com.product.ordering.system.kafka.message.serialization.MessageKafkaProjection;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
class KafkaProducerConfiguration<T extends Serializable> {

    private final KafkaConfigurationData kafkaConfigurationData;
    private final KafkaProducerConfigurationData kafkaProducerConfigurationData;

    KafkaProducerConfiguration(final KafkaConfigurationData kafkaConfigurationData,
                               final KafkaProducerConfigurationData kafkaProducerConfigurationData) {

        this.kafkaConfigurationData = kafkaConfigurationData;
        this.kafkaProducerConfigurationData = kafkaProducerConfigurationData;
    }

    @Bean
    public Map<String, Object> producerConfiguration() {
        var properties = new HashMap<String, Object>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigurationData.getBootstrapServers());
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigurationData.getBatchSize() *
                                                         kafkaProducerConfigurationData.getBatchSizeBoostFactor());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigurationData.getLingerMs());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigurationData.getRequestTimeoutMs());
        properties.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigurationData.getRetryCount());

        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, MessageKafkaDtoJsonSerializer.class);
        properties.put(ACKS_CONFIG, "all");
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

        return properties;
    }

    @Bean
    public KafkaTemplate<String, MessageKafkaProjection<T>> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, MessageKafkaProjection<T>> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfiguration());
    }
}
