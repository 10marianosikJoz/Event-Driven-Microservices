package com.product.ordering.application;

import com.product.ordering.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.UUID;

class OrderConstantDataProvider {

    static final UUID PRODUCT_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    static final UUID WAREHOUSE_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");

    static final String STREET = "Marie Curie";
    static final String POSTAL_CODE = "20-112";
    static final String CITY = "Warsaw";

    static final BigDecimal ORDER_PRICE = new BigDecimal("400.00");
    static final BigDecimal INCORRECT_ORDER_PRICE = new BigDecimal("500.00");
    static final Currency CURRENCY = Currency.PLN;
    static final OrderStatus ORDER_STATUS_PENDING = OrderStatus.PENDING;

    static final BigDecimal PRODUCT_PRICE = new BigDecimal("100.00");
    static final BigDecimal PRODUCT_SUBTOTAL = new BigDecimal("400.00");

    static final PaymentMethod PAYMENT_METHOD = PaymentMethod.CASH;
    static final DeliveryMethod PACKAGE_BOX_DELIVERY_METHOD = DeliveryMethod.PACKAGE_BOX;

    static final Coupon VALID_COUPON = new Coupon("Example coupon", new Money(new BigDecimal("30.00")));

    static final Quantity QUANTITY = new Quantity(4);
}
