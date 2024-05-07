package com.product.ordering.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration(proxyBeanMethods = false)
@ConfigurationProperties(prefix = "payment-service")
public class PaymentServiceConfiguration {

    private String orderCreatedEventsTopicName;
    private String orderCancellingEventsTopicName;
    private String paymentStatusEventsTopicName;
}
