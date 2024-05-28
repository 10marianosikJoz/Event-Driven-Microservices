package com.product.ordering.entities.mapper;

import com.product.ordering.domain.entity.OrderItem;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.valueobject.*;
import com.product.ordering.entities.entity.OrderItemEntity;
import com.product.ordering.entities.entity.OrderProcessedEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderProcessedEntityCommandMapper {

    public OrderProcessed mapOrderProcessedJpaEntityToOrderProcessedDomainObject(OrderProcessedEntity orderProcessedEntity) {
        return OrderProcessed.builder()
                .orderProcessedId(new OrderProcessedId(orderProcessedEntity.getId()))
                .warehouseId(new WarehouseId(orderProcessedEntity.getWarehouseId()))
                .price(new Money(orderProcessedEntity.getPrice()))
                .orderItems(mapOrderItemJpaEntityToOrderItemDomainObject(orderProcessedEntity.getItems()))
                .orderApprovalStatus(orderProcessedEntity.getStatus())
                .build();
    }

    private List<OrderItem> mapOrderItemJpaEntityToOrderItemDomainObject(Set<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                .map(it -> OrderItem.builder()
                        .orderItemId(new OrderItemId(it.getOrderId()))
                        .productId(new ProductId(it.getProductId()))
                        .price(new Money(it.getPrice()))
                        .quantity(new Quantity(it.getQuantity()))
                        .build())
                .collect(Collectors.toList());
    }

    public OrderProcessedEntity mapOrderProcessedDomainObjectToOrderProcessedJpaEntity(OrderProcessed orderProcessed) {
        return OrderProcessedEntity.builder()
                .id(orderProcessed.id().value())
                .warehouseId(orderProcessed.warehouseId().value())
                .price(orderProcessed.price().amount())
                .status(orderProcessed.orderApprovalStatus())
                .items(mapOrderItemDomainObjectToOrderItemJpaEntity(orderProcessed.orderItems(), orderProcessed.id()))
                .build();
    }

    private Set<OrderItemEntity> mapOrderItemDomainObjectToOrderItemJpaEntity(List<OrderItem> orderItems, OrderProcessedId orderId) {
        return orderItems.stream()
                .map(orderItem ->
                        OrderItemEntity.builder()
                                .id(orderItem.id().value())
                                .productId(orderItem.productId().value())
                                .price(orderItem.price().amount())
                                .quantity(orderItem.quantity().amount())
                                .orderId(orderId.value())
                                .build())
                .collect(Collectors.toSet());
    }
}
