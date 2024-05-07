package com.product.ordering.domain;

import java.math.BigDecimal;
import java.util.UUID;

class PaymentConstantDataProvider {

    static final UUID PAYMENT_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    static final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");

    static final BigDecimal ORDER_PRICE = new BigDecimal("400.00");
    static final BigDecimal WRONG_PRICE = new BigDecimal("0");
    static final BigDecimal BILLFOLD_MONEY_SPENT = new BigDecimal("320.10");
    static final BigDecimal BILLFOLD_TOTAL_MONEY_AMOUNT = new BigDecimal("400.00");
}
