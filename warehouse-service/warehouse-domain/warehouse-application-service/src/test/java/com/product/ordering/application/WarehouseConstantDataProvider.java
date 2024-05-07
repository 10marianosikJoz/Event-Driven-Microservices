package com.product.ordering.application;

import java.math.BigDecimal;
import java.util.UUID;

class WarehouseConstantDataProvider {

    static final UUID ORDER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    static final UUID WAREHOUSE_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb42");
    static final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb43");
    static final UUID ORDER_ITEM_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb50");

    static final BigDecimal ORDER_PRICE = new BigDecimal("300.00");
    static final BigDecimal ORDER_ITEM_PRICE = new BigDecimal("300.00");

    static final String ORDER_STATUS = "PAID";

    static final int QUANTITY = 1;

    static final String WAREHOUSE_NAME = "Warehouse_1";
}
