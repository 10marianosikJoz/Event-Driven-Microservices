package com.product.ordering.container;

import com.product.ordering.domain.OrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BeanConfiguration {

    @Bean
    private OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }
}
