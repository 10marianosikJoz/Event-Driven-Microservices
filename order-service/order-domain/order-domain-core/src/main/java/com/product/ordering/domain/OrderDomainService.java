package com.product.ordering.domain;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.OrderPaidEvent;

import java.time.Instant;
import java.util.List;

public class OrderDomainService {

    public OrderCreatedEvent createOrder(Order order) {

        order.validateOrders();
        order.initializeOrder();

        return new OrderCreatedEvent(order,
                                     Instant.now());
    }

    public OrderPaidEvent payOrder(Order order) {
        order.pay();

        return new OrderPaidEvent(order,
                                  Instant.now());
    }

    public void approveOrder(Order order) {
        order.approve();
    }

    public OrderCancellingEvent initializeCancelling(Order order,
                                                     List<String> failureMessages) {

        order.initCancel(failureMessages);

        return new OrderCancellingEvent(order,
                                       Instant.now());
    }

    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
    }
}
