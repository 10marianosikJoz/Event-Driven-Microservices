package com.product.ordering.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.product.ordering.adapters.repository"})
@EntityScan(basePackages = {"com.product.ordering.entities.entity", "com.product.ordering.entity"})
@SpringBootApplication(scanBasePackages = "com.product")
public class WarehouseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }
}
