package com.product.ordering.application;

import com.product.ordering.application.ports.output.repository.OrderProcessedRepository;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.valueobject.OrderProcessedId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryOrderProcessedRepository implements OrderProcessedRepository {

    static final Map<OrderProcessedId, OrderProcessed> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public OrderProcessed save(OrderProcessed orderProcessed) {
        DATABASE.put(orderProcessed.id(), orderProcessed);
        return orderProcessed;
    }

    public Optional<OrderProcessed> findById(OrderProcessedId orderProcessedId) {
        var fetched = DATABASE.get(orderProcessedId);
        return Optional.ofNullable(fetched);
    }
}
