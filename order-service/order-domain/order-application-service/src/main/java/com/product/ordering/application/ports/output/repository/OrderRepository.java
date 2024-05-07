package com.product.ordering.application.ports.output.repository;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.valueobject.OrderId;

public interface OrderRepository {

    Order save(Order order);

    Order fetchOrder(OrderId orderId);
}
