package com.product.ordering.domain;

import com.product.ordering.domain.entity.OrderItem;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

class WarehouseDomainDataProvider {

    static Warehouse warehouse(boolean isOpen) {
        return Warehouse.builder()
                .warehouseId(new WarehouseId(WarehouseConstantDataProvider.WAREHOUSE_ID))
                .warehouseName(new WarehouseName(WarehouseConstantDataProvider.WAREHOUSE_NAME))
                .isOpen(isOpen)
                .build();
    }

    static OrderProcessed orderProcessed(BigDecimal orderItemPrice) {
        return OrderProcessed.builder()
                .orderProcessedId(new OrderProcessedId(WarehouseConstantDataProvider.ORDER_PROCESSED_ID))
                .warehouseId(new WarehouseId(WarehouseConstantDataProvider.WAREHOUSE_ID))
                .price(new Money(WarehouseConstantDataProvider.CORRECT_ORDER_ITEM_PRICE))
                .orderItems(orderItems(orderItemPrice))
                .build();
    }

    private static List<OrderItem> orderItems(BigDecimal orderItemPrice) {
        return Collections.singletonList(OrderItem.builder()
                .orderItemId(new OrderItemId(WarehouseConstantDataProvider.ORDER_ITEM_ID))
                .productId(new ProductId(WarehouseConstantDataProvider.PRODUCT_ID))
                .price(new Money(orderItemPrice))
                .quantity(new Quantity(WarehouseConstantDataProvider.QUANTITY))
                .build());
    }
}
