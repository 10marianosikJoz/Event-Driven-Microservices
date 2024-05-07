package com.product.ordering.domain;

import java.math.BigDecimal;
import java.util.UUID;

class WarehouseConstantDataProvider {

    static final UUID WAREHOUSE_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    static final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb47");
    static final UUID ORDER_ITEM_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb43");
    static final UUID ORDER_PROCESSED_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb43");

    static final BigDecimal CORRECT_ORDER_ITEM_PRICE = new BigDecimal("400.00");
    static final BigDecimal INCORRECT_ORDER_ITEM_PRICE = new BigDecimal("500.00");

    static final int QUANTITY = 1;

    static final String WAREHOUSE_NAME = "Warehouse_1";
}
