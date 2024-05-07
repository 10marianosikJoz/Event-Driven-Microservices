package com.product.ordering.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfiguration {

    private String orderCancellingEventsTopicName;
    private String orderCreatedEventsTopicName;
    private String orderPaidEventsTopicName;
    private String paymentStatusEventsTopicName;
    private String warehouseApprovalEventsTopicName;
}
