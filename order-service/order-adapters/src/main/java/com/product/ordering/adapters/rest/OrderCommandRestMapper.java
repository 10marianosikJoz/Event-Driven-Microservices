package com.product.ordering.adapters.rest;

import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.OrderDeliveryAddressCommand;
import com.product.ordering.application.command.projection.OrderItemCommand;
import com.product.ordering.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
class OrderCommandRestMapper {

    CreateOrderCommand mapCreateOrderRequestToCreateOrderCommand(CreateOrderRequest createOrderRequest) {
        return CreateOrderCommand.builder()
                .customerId(createOrderRequest.customerId())
                .warehouseId(createOrderRequest.warehouseId())
                .address(mapDeliveryAddressCreateRequestToOrderDeliveryAddressCommand(createOrderRequest.deliveryAddress()))
                .currency(Currency.valueOf(createOrderRequest.currency()))
                .orderItems(mapCreateOrderItemRequestToOrderItemCommand(createOrderRequest.orderItems()))
                .paymentMethod(PaymentMethod.valueOf(createOrderRequest.paymentMethod()))
                .deliveryMethod(DeliveryMethod.valueOf(createOrderRequest.deliveryMethod()))
                .price(createOrderRequest.price())
                .coupon(new Coupon(createOrderRequest.coupon(), new Money(BigDecimal.ONE)))
                .build();

    }

    private OrderDeliveryAddressCommand mapDeliveryAddressCreateRequestToOrderDeliveryAddressCommand(CreateOrderRequest.CreateOrderDeliveryAddressRequest deliveryAddressCreateRequest) {
        return OrderDeliveryAddressCommand.builder()
                .street(deliveryAddressCreateRequest.street())
                .postalCode(deliveryAddressCreateRequest.postalCode())
                .city(deliveryAddressCreateRequest.city()).build();

    }

    private Set<OrderItemCommand> mapCreateOrderItemRequestToOrderItemCommand(Set<CreateOrderRequest.CreateOrderItemRequest> createOrderItemRequests) {
        return createOrderItemRequests.stream()
                                      .map(it -> new OrderItemCommandInternalMapper().apply(it))
                                      .collect(Collectors.toSet());
    }

    static class OrderItemCommandInternalMapper implements Function<CreateOrderRequest.CreateOrderItemRequest, OrderItemCommand> {

        @Override
        public OrderItemCommand apply(CreateOrderRequest.CreateOrderItemRequest createOrderItemRequest) {
            return new OrderItemCommand(createOrderItemRequest.orderItemId(),
                                        createOrderItemRequest.productId(),
                                        createOrderItemRequest.quantity(),
                                        createOrderItemRequest.price(),
                                        createOrderItemRequest.subtotal());
        }
    }
}
