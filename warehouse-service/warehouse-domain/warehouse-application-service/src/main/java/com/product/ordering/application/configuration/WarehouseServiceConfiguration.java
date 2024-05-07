package com.product.ordering.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "warehouse-service")
public class WarehouseServiceConfiguration {

    private String orderPaidEventsTopicName;
    private String warehouseApprovalEventsTopicName;
}
