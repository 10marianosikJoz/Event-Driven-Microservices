package com.product.ordering.application;

import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.OrderDeliveryAddressCommand;
import com.product.ordering.application.command.projection.OrderItemCommand;
import com.product.ordering.domain.valueobject.OrderStatus;

import java.util.Collections;
import java.util.Set;

class OrderDataProvider {

    static CreateOrderCommand validOrderCommand() {
        return new CreateOrderCommand(OrderConstantDataProvider.CUSTOMER_ID,
                                      OrderConstantDataProvider.WAREHOUSE_ID,
                                      OrderConstantDataProvider.ORDER_PRICE,
                                      OrderConstantDataProvider.CURRENCY,
                                      OrderConstantDataProvider.PAYMENT_METHOD,
                                      OrderConstantDataProvider.PACKAGE_BOX_DELIVERY_METHOD,
                                      OrderConstantDataProvider.VALID_COUPON,
                                      orderItemCommand(),
                                      orderDeliveryAddressCommand(),
                                      OrderStatus.NONE);
    }

    static CreateOrderCommand orderCommandWithIncorrectTotalPrice() {
        return new CreateOrderCommand(OrderConstantDataProvider.CUSTOMER_ID,
                                      OrderConstantDataProvider.WAREHOUSE_ID,
                                      OrderConstantDataProvider.INCORRECT_ORDER_PRICE,
                                      OrderConstantDataProvider.CURRENCY,
                                      OrderConstantDataProvider.PAYMENT_METHOD,
                                      OrderConstantDataProvider.PACKAGE_BOX_DELIVERY_METHOD,
                                      OrderConstantDataProvider.VALID_COUPON,
                                      orderItemCommand(),
                                      orderDeliveryAddressCommand(),
                                      OrderStatus.NONE);
    }

    static Set<OrderItemCommand> orderItemCommand() {
        return Collections.singleton(OrderItemCommand.builder()
                .productId(OrderConstantDataProvider.PRODUCT_ID)
                .quantity(OrderConstantDataProvider.QUANTITY.quantity())
                .price(OrderConstantDataProvider.PRODUCT_PRICE)
                .subtotal(OrderConstantDataProvider.PRODUCT_SUBTOTAL)
                .build());
    }

    static OrderDeliveryAddressCommand orderDeliveryAddressCommand() {
        return OrderDeliveryAddressCommand.builder()
                .street(OrderConstantDataProvider.STREET)
                .postalCode(OrderConstantDataProvider.POSTAL_CODE)
                .city(OrderConstantDataProvider.CITY)
                .build();
    }
}
