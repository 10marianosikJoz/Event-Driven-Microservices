package com.product.ordering.application.command;

import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.domain.entity.*;
import com.product.ordering.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderProcessedMapper {

    public OrderProcessed mapOrderPaidEventToOrderProcessedJpaEntity(OrderPaidEvent orderPaidEvent) {
        return OrderProcessed.builder()
                .orderProcessedId(new OrderProcessedId(UUID.fromString(orderPaidEvent.orderId())))
                .warehouseId(new WarehouseId(UUID.fromString(orderPaidEvent.warehouseId())))
                .price(new Money(orderPaidEvent.price()))
                .orderItems(mapOrderPaidProjectionToOrderItemDomainObject(orderPaidEvent))
                .build();
    }

    private List<OrderItem> mapOrderPaidProjectionToOrderItemDomainObject(OrderPaidEvent orderPaidEvent) {
        return orderPaidEvent.orderItem().stream()
                                         .map(it -> OrderItem.builder()
                                                    .productId(new ProductId(UUID.fromString(it.productId())))
                                                    .quantity(new Quantity(it.quantity()))
                                                    .price(new Money(it.price()))
                                                    .build())
                                         .toList();
    }
}
