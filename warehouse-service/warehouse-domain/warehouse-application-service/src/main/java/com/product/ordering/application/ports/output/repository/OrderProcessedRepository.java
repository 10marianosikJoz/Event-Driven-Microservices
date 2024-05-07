package com.product.ordering.application.ports.output.repository;

import com.product.ordering.domain.entity.OrderProcessed;

public interface OrderProcessedRepository {

    OrderProcessed save(OrderProcessed orderProcessed);
}
