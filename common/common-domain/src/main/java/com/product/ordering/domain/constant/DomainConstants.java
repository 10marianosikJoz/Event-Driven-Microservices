package com.product.ordering.domain.constant;

public class DomainConstants {

    public static final String ORDER_INCORRECT_STATE = "Order is in the incorrect state.";
    public static final String ORDER_INCORRECT_TOTAL_PRICE = "Total price must be greater than zero.";
    public static final String ORDER_INCORRECT_DELIVERY_ADDRESS = "Delivery address is not provided.";
    public static final String ORDER_INCORRECT_PAYMENT_METHOD = "Payment method is not selected.";
    public static final String ORDER_INCORRECT_DELIVERY_METHOD = "Delivery method is not selected.";
    public static final String ORDER_INCORRECT_COUPON_FORMAT = "Incorrect coupon format.";
    public static final String ORDER_DELIVERY_ADDRESS_MISSED = "Delivery address data is missing.";
    public static final String ORDER_PRODUCT_DESCRIPTION_MISSED = "Product description is not provided.";
    public static final String ORDER_INCORRECT_PRODUCT_QUANTITY_VALUE = "Incorrect quantity value.";
    public static final String ORDER_PRODUCT_INCORRECT_RATING_NUMBER = "Rating number is incorrect.";
    public static final Long ORDER_DELIVERY_CHARGE = 20L;

    public static final String PAYMENT_INCORRECT_STATE = "Payment is in the incorrect state";
    public static final String PAYMENT_INCORRECT_PRICE= "Payment price must be not null or greater than zero.";
    public static final String PAYMENT_NOT_ENOUGH_MONEY_IN_THE_BILLFOLD = "Not enough money in the billfold.";

    public static final String INCORRECT_WAREHOUSE_NAME = "Warehouse name should not be null or empty";

    private DomainConstants() {}
}
