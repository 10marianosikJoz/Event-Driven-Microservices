package com.product.ordering.system;

import java.math.BigDecimal;
import java.util.UUID;

class PaymentConstantDataProvider {

    static final UUID PAYMENT_ID = UUID.fromString("0c2a410a-2e72-4241-9a58-628a35280a0a");
    static final UUID BILLFOLD_ID = UUID.fromString("1dadc1bf-9ac8-4cee-9d76-8d33b292384a");
    static final UUID CUSTOMER_ID = UUID.fromString("88ab6738-79d5-422d-8f60-867bdbc15217");
    static final UUID ORDER_ID = UUID.fromString("9851e272-a073-46ff-84a3-a388276395bd");

    static final BigDecimal PAYMENT_PRICE = new BigDecimal("400.00");
    static final BigDecimal INVALID_PAYMENT_PRICE = new BigDecimal("0");
    static final BigDecimal BILLFOLD_TOTAL_AMOUNT = new BigDecimal("2000.00");
    static final BigDecimal BILLFOLD_HISTORY_ENTRY_AMOUNT = new BigDecimal("400.00");
    static final BigDecimal BILLFOLD_NOT_ENOUGH_AMOUNT = new BigDecimal("100.00");
}
