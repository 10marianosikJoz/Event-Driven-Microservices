package com.product.ordering.container;

import com.product.ordering.domain.WarehouseDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BeanConfiguration {

    @Bean
    public WarehouseDomainService warehouseDomainService() {
        return new WarehouseDomainService();
    }
}
