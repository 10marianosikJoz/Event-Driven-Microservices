package com.product.ordering.domain;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCancellingEvent;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.OrderPaidEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;

import java.time.Instant;
import java.util.List;

public class OrderDomainService {

    public OrderCreatedEvent createOrder(Order order,
                                         DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainPublisher) {

        order.validateOrders();
        order.initializeOrder();

        return new OrderCreatedEvent(order,
                                     Instant.now(),
                                     orderCreatedEventDomainPublisher);
    }

    public OrderPaidEvent payOrder(Order order,
                                   DomainEventPublisher<OrderPaidEvent> orderCompletedEventDomainPublisher) {
        order.pay();

        return new OrderPaidEvent(order,
                                  Instant.now(),
                                  orderCompletedEventDomainPublisher);
    }

    public void approveOrder(Order order) {
        order.approve();
    }

    public OrderCancellingEvent initializeCancelling(Order order,
                                                     List<String> failureMessages,
                                                     DomainEventPublisher<OrderCancellingEvent> orderCancelledEventDomainPublisher) {
        order.initCancel(failureMessages);

        return new OrderCancellingEvent(order,
                                       Instant.now(),
                                       orderCancelledEventDomainPublisher);
    }

    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
    }
}
