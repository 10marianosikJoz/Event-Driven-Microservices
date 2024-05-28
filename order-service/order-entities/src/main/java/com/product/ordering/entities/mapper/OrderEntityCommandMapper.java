package com.product.ordering.entities.mapper;

import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.entity.OrderItem;
import com.product.ordering.domain.entity.Product;
import com.product.ordering.domain.valueobject.*;
import com.product.ordering.entities.entity.DeliveryAddressEntity;
import com.product.ordering.entities.entity.OrderEntity;
import com.product.ordering.entities.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderEntityCommandMapper {

    private static final String FAILURE_MESSAGE_DELIMITER = ";";

    public OrderEntity mapDomainOrderObjectToOrderJpaEntity(Order order) {
        var orderEntity = OrderEntity.builder()
                .id(order.id().value())
                .customerId(order.customerId().value())
                .warehouseId(order.warehouseId().value())
                .currency(order.currency())
                .orderItemEntities(mapOrderItemDomainObjectToOrderItemJpaEntity(order.orderItems()))
                .paymentMethod(order.paymentMethod())
                .deliveryMethod(order.deliveryMethod())
                .price(order.price().amount())
                .coupon(order.coupon())
                .orderStatus(order.orderStatus())
                .failureMessages(order.failureMessages() != null ? String.join(FAILURE_MESSAGE_DELIMITER, order.failureMessages()) : null)
                .deliveryAddressEntity(mapDeliveryAddressDomainObjectToDeliveryAddressJpaEntity(order.deliveryAddress()))
                .build();

        orderEntity.getDeliveryAddressEntity().setOrder(orderEntity);
        orderEntity.getOrderItemEntities().forEach(it -> it.setOrder(orderEntity));

        return orderEntity;
    }

    private Set<OrderItemEntity> mapOrderItemDomainObjectToOrderItemJpaEntity(Set<OrderItem> orderItems) {
        return orderItems.stream()
                         .map(it -> new OrderItemEntityInternalMapper().apply(it))
                         .collect(Collectors.toSet());
    }

    private DeliveryAddressEntity mapDeliveryAddressDomainObjectToDeliveryAddressJpaEntity(DeliveryAddress deliveryAddress) {
        return DeliveryAddressEntity.builder()
                .id(deliveryAddress.id())
                .street(deliveryAddress.street())
                .postalCode(deliveryAddress.postalCode())
                .city(deliveryAddress.city())
                .build();
    }

    public Order mapOrderJpaEntityToOrderDomainObject(OrderEntity order) {
        return Order.builder()
                .orderId(new OrderId(order.getId()))
                .customerId(new CustomerId(order.getCustomerId()))
                .warehouseId(new WarehouseId(order.getWarehouseId()))
                .deliveryAddress(mapDeliveryAddressJpaEntityToDeliveryAddressDomainObject(order.getDeliveryAddressEntity()))
                .price(new Money(order.getPrice()))
                .currency(order.getCurrency())
                .orderItems(mapOrderItemJpaEntityToOrderItemDomainObject(order.getOrderItemEntities()))
                .paymentMethod(order.getPaymentMethod())
                .deliveryMethod(order.getDeliveryMethod())
                .coupon(order.getCoupon())
                .orderStatus(order.getOrderStatus())
                .failureMessages(new ArrayList<>(Arrays.asList(order.getFailureMessages())))
                .build();
    }

    private DeliveryAddress mapDeliveryAddressJpaEntityToDeliveryAddressDomainObject(DeliveryAddressEntity deliveryAddressEntity) {
        return DeliveryAddress.builder()
                .id(deliveryAddressEntity.getId())
                .street(deliveryAddressEntity.getStreet())
                .postalCode(deliveryAddressEntity.getPostalCode())
                .city(deliveryAddressEntity.getCity())
                .build();

    }

    private Set<OrderItem> mapOrderItemJpaEntityToOrderItemDomainObject(Set<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                                .map(it -> new OrderItemInternalMapper().apply(it))
                                .collect(Collectors.toSet());
    }

    static class OrderItemInternalMapper implements Function<OrderItemEntity, OrderItem> {

        @Override
        public OrderItem apply(OrderItemEntity orderItemEntity) {
            return OrderItem.builder()
                    .orderId(new OrderId(orderItemEntity.getOrder().getId()))
                    .orderItemId(new OrderItemId(orderItemEntity.getId()))
                    .product(new Product(new ProductId(orderItemEntity.getProductId())))
                    .quantity(new Quantity(orderItemEntity.getQuantity()))
                    .price(new Money(orderItemEntity.getPrice()))
                    .subTotal(new Money(orderItemEntity.getSubTotal()))
                    .build();
        }
    }

    static class OrderItemEntityInternalMapper implements Function<OrderItem, OrderItemEntity> {

        @Override
        public OrderItemEntity apply(OrderItem orderItem) {
            return OrderItemEntity.builder()
                    .id(orderItem.id().value())
                    .productId(orderItem.product().id().value())
                    .quantity(orderItem.quantity().quantity())
                    .price(orderItem.price().amount())
                    .subTotal(orderItem.subtotal().amount())
                    .build();
        }
    }
}
