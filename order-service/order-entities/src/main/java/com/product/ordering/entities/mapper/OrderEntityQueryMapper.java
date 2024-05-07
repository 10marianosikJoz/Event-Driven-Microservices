package com.product.ordering.entities.mapper;

import com.product.ordering.application.query.DeliveryAddressProjectionView;
import com.product.ordering.application.query.OrderItemProjectionView;
import com.product.ordering.application.query.OrderProjectionView;
import com.product.ordering.entities.entity.DeliveryAddressEntity;
import com.product.ordering.entities.entity.OrderEntity;
import com.product.ordering.entities.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderEntityQueryMapper {

    private static final String FAILURE_MESSAGE_DELIMITER = ";";

    public OrderProjectionView mapOrderEntityToOrderProjectionView(OrderEntity order) {
        return OrderProjectionView.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .warehouseId(order.getWarehouseId())
                .deliveryAddress(mapDeliveryAddressJpaEntityToDeliveryAddressProjectionView(order.getDeliveryAddressEntity()))
                .currency(order.getCurrency())
                .orderItems(mapOrderItemJpaEntityToOrderItemProjectionView(order.getOrderItemEntities()))
                .paymentMethod(order.getPaymentMethod())
                .deliveryMethod(order.getDeliveryMethod())
                .price(order.getPrice())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() != null && !order.getFailureMessages().isEmpty() ?
                                 new ArrayList<>(Arrays.asList(order.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))) : null)
                .build();
    }

    private DeliveryAddressProjectionView mapDeliveryAddressJpaEntityToDeliveryAddressProjectionView(DeliveryAddressEntity deliveryAddressEntity) {
        return new DeliveryAddressProjectionView(deliveryAddressEntity.getId(),
                                                 deliveryAddressEntity.getStreet(),
                                                 deliveryAddressEntity.getPostalCode(),
                                                 deliveryAddressEntity.getCity());
    }

    private Set<OrderItemProjectionView> mapOrderItemJpaEntityToOrderItemProjectionView(Set<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                                .map(it -> new OrderItemProjectionViewInternalMapper().apply(it))
                                .collect(Collectors.toSet());
    }

    static class OrderItemProjectionViewInternalMapper implements Function<OrderItemEntity, OrderItemProjectionView> {

        @Override
        public OrderItemProjectionView apply(OrderItemEntity orderItemEntity) {
            return new OrderItemProjectionView(orderItemEntity.getId(),
                                               orderItemEntity.getOrder().getId(),
                                               orderItemEntity.getQuantity(),
                                               orderItemEntity.getProductId(),
                                               orderItemEntity.getPrice(),
                                               orderItemEntity.getSubTotal());
        }
    }
}
