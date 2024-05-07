package com.product.ordering.system.kafka.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigurationData {

    private String bootstrapServers;
    private Integer numOfPartitions;
    private String replicationFactor;
}
