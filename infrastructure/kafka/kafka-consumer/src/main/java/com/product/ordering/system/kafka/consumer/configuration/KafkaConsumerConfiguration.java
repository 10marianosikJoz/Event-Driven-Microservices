package com.product.ordering.system.kafka.consumer.configuration;

import com.product.ordering.system.kafka.configuration.KafkaConfigurationData;
import com.product.ordering.system.kafka.configuration.KafkaConsumerConfigurationData;
import com.product.ordering.system.kafka.message.serialization.MessageKafkaDtoJsonDeserializer;
import com.product.ordering.system.kafka.message.serialization.TypeProjection;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Configuration
class KafkaConsumerConfiguration {

    private final KafkaConfigurationData kafkaConfigurationData;
    private final KafkaConsumerConfigurationData kafkaConsumerConfigurationData;

    KafkaConsumerConfiguration(final KafkaConsumerConfigurationData kafkaConsumerConfigurationData,
                               final KafkaConfigurationData kafkaConfigurationData) {

        this.kafkaConfigurationData = kafkaConfigurationData;
        this.kafkaConsumerConfigurationData = kafkaConsumerConfigurationData;
    }

    @Value("${spring.application.name}")
    protected String serviceName;

    @Bean
    public Map<String, Object> consumerConfiguration() {
        var properties = new HashMap<String, Object>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigurationData.getBootstrapServers());
        properties.put(GROUP_ID_CONFIG, this.serviceName);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerConfigurationData.getSessionTimeoutMs());
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerConfigurationData.getHeartbeatIntervalMs());
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerConfigurationData.getMaxPollIntervalMs());
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, kafkaConsumerConfigurationData.getMaxPartitionFetchBytesDefault() *
                                                                        kafkaConsumerConfigurationData.getMaxPartitionFetchBytesBoostFactor());
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerConfigurationData.getMaxPollRecords());
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, MessageKafkaDtoJsonDeserializer.class);
        properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }

    @Bean
    public ConsumerFactory<String, TypeProjection> kafkaConsumerFactory(MessageKafkaDtoJsonDeserializer messageKafkaDtoJsonDeserializer) {
        return new DefaultKafkaConsumerFactory<>(consumerConfiguration(),
                                                 new StringDeserializer(),
                                                 messageKafkaDtoJsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TypeProjection>> kafkaListenerContainerFactory(@Qualifier("kafkaConsumerFactory") ConsumerFactory<String, TypeProjection> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TypeProjection> containerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(kafkaConsumerFactory);
        containerFactory.setBatchListener(kafkaConsumerConfigurationData.getBatchListener());
        containerFactory.setConcurrency(kafkaConsumerConfigurationData.getConcurrencyLevel());
        containerFactory.setAutoStartup(kafkaConsumerConfigurationData.getAutoStartup());
        containerFactory.getContainerProperties().setPollTimeout(kafkaConsumerConfigurationData.getPollTimeoutMs());

        return containerFactory;
    }
}
