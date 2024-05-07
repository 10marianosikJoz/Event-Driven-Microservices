package com.product.ordering.application;

import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.valueobject.OrderId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public Order save(final Order order) {
        DATABASE.put(order.id(), order);
        return order;
    }

    @Override
    public Order fetchOrder(final OrderId orderId) {
        return DATABASE.get(orderId);
    }
}
