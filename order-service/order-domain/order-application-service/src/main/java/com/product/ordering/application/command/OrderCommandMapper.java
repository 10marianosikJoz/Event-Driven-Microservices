package com.product.ordering.application.command;

import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.CreateOrderResponse;
import com.product.ordering.application.command.projection.OrderDeliveryAddressCommand;
import com.product.ordering.application.command.projection.OrderItemCommand;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.entity.OrderItem;
import com.product.ordering.domain.entity.Product;
import com.product.ordering.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderCommandMapper {

    Order mapCreateOrderCommandToOrderDomainObject(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.customerId()))
                .warehouseId(new WarehouseId(createOrderCommand.warehouseId()))
                .price(new Money(createOrderCommand.price()))
                .deliveryAddress(mapOrderDeliveryAddressCommandToDeliveryAddressDomainObject(createOrderCommand.address()))
                .currency(createOrderCommand.currency())
                .orderItems(mapOrderItemCommandToOrderItemDomainObject(createOrderCommand.orderItems()))
                .deliveryMethod(createOrderCommand.deliveryMethod())
                .paymentMethod(createOrderCommand.paymentMethod())
                .build();
    }

    private DeliveryAddress mapOrderDeliveryAddressCommandToDeliveryAddressDomainObject(OrderDeliveryAddressCommand orderDeliveryAddressCommand) {
        return DeliveryAddress.builder()
                .id(UUID.randomUUID())
                .street(orderDeliveryAddressCommand.street())
                .postalCode(orderDeliveryAddressCommand.postalCode())
                .city(orderDeliveryAddressCommand.city())
                .build();
    }

    private Set<OrderItem> mapOrderItemCommandToOrderItemDomainObject(Set<OrderItemCommand> orderItemCommands) {
        return orderItemCommands.stream()
                                .map(it -> new OrderItemInternalMapper().apply(it))
                                .collect(Collectors.toSet());
    }

    CreateOrderResponse mapOrderDomainObjectToCreateOrderResponse(Order order) {
        return CreateOrderResponse.builder()
                .orderId(order.id().value())
                .orderStatus(order.orderStatus())
                .build();
    }

    static class OrderItemInternalMapper implements Function<OrderItemCommand, OrderItem> {

        @Override
        public OrderItem apply(OrderItemCommand orderItemCommand) {
            return OrderItem.builder()
                    .orderItemId(new OrderItemId(orderItemCommand.orderItemId()))
                    .product(Product.builder()
                                    .productId(new ProductId(orderItemCommand.productId()))
                                    .price(new Money(orderItemCommand.price()))
                                    .build())
                    .quantity(new Quantity(orderItemCommand.quantity()))
                    .price(new Money(orderItemCommand.price()))
                    .subTotal(new Money(orderItemCommand.subtotal()))
                    .build();
        }
    }
}
