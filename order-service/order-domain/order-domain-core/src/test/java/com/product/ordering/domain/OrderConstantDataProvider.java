package com.product.ordering.domain;

import com.product.ordering.domain.valueobject.Coupon;
import com.product.ordering.domain.valueobject.Currency;
import com.product.ordering.domain.valueobject.DeliveryMethod;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.PaymentMethod;
import com.product.ordering.domain.valueobject.Review;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

class OrderConstantDataProvider {

    static final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    static final UUID PRODUCT_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    static final UUID WAREHOUSE_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");

    static final String STREET = "Marie Curie";
    static final String POSTAL_CODE = "20-112";
    static final String CITY = "Warsaw";

    static final BigDecimal ORDER_PRICE = new BigDecimal("400.00");
    static final Currency CURRENCY = Currency.PLN;

    static final BigDecimal PRODUCT_PRICE = new BigDecimal("100.00");
    static final BigDecimal PRODUCT_SUBTOTAL = new BigDecimal("100.00");
    static final BigDecimal PRODUCTS_SUBTOTAL = new BigDecimal("300.00");
    static final String PRODUCT_DESCRIPTION = "Modern product";
    static final List<Review> PRODUCT_REVIEWS = List.of(new Review(5, "Great product."));

    static final PaymentMethod PAYMENT_METHOD = PaymentMethod.CASH;

    static final DeliveryMethod PACKAGE_BOX_DELIVERY_METHOD = DeliveryMethod.PACKAGE_BOX;
    static final DeliveryMethod COURIER_DELIVERY_METHOD = DeliveryMethod.COURIER;

    static final Coupon VALID_COUPON = new Coupon("Example coupon", new Money(new BigDecimal("30.00")));

    static final List<String> FAILURE_MESSAGE = List.of("Warehouse is closed");
}
