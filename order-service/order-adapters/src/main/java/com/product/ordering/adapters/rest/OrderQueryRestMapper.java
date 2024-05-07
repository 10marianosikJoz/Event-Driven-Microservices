package com.product.ordering.adapters.rest;

import com.product.ordering.application.query.DeliveryAddressProjectionView;
import com.product.ordering.application.query.OrderItemProjectionView;
import com.product.ordering.application.query.OrderProjectionView;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
class OrderQueryRestMapper {

    OrderDetailSummaryProjection mapOrderProjectionViewToOrderDetailSummaryProjection(OrderProjectionView orderProjectionView) {
        return OrderDetailSummaryProjection.builder()
                .orderId(orderProjectionView.orderId())
                .customerId(orderProjectionView.customerId())
                .warehouseId(orderProjectionView.warehouseId())
                .deliveryAddress(mapDeliveryAddressProjectionViewToOrderAddressProjection(orderProjectionView.deliveryAddress()))
                .currency(orderProjectionView.currency())
                .orderItems(mapToOrderItemProjectionViewToOrderItemProjection(orderProjectionView.orderItems()))
                .paymentMethod(orderProjectionView.paymentMethod())
                .deliveryMethod(orderProjectionView.deliveryMethod())
                .price(orderProjectionView.price())
                .orderStatus(orderProjectionView.orderStatus())
                .failureMessages(orderProjectionView.failureMessages())
                .build();

    }

    private OrderDetailSummaryProjection.DeliveryAddressProjection mapDeliveryAddressProjectionViewToOrderAddressProjection(DeliveryAddressProjectionView deliveryAddressProjectionView) {
        return new OrderDetailSummaryProjection.DeliveryAddressProjection(deliveryAddressProjectionView.id(),
                                                deliveryAddressProjectionView.street(),
                                                deliveryAddressProjectionView.postalCode(),
                                                deliveryAddressProjectionView.city());
    }

    private Set<OrderDetailSummaryProjection.OrderItemProjection> mapToOrderItemProjectionViewToOrderItemProjection(Set<OrderItemProjectionView> orderItemProjectionView) {
        return orderItemProjectionView.stream()
                                       .map(it -> new OrderItemProjectionInternalMapper().apply(it))
                                       .collect(Collectors.toSet());
    }

        static class OrderItemProjectionInternalMapper implements Function<OrderItemProjectionView, OrderDetailSummaryProjection.OrderItemProjection> {

        @Override
        public OrderDetailSummaryProjection.OrderItemProjection apply(OrderItemProjectionView orderItemProjectionView) {
            return new OrderDetailSummaryProjection.OrderItemProjection(orderItemProjectionView.productId(),
                                                                        orderItemProjectionView.quantity(),
                                                                        orderItemProjectionView.price(),
                                                                        orderItemProjectionView.subtotal());
        }
    }
}
