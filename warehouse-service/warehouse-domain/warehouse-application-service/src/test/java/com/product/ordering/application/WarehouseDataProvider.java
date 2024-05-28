package com.product.ordering.application;

import com.product.ordering.application.command.projection.OrderItemProjection;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.valueobject.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

class WarehouseDataProvider {

    static OrderPaidEvent orderPaidEvent() {
        return OrderPaidEvent.builder()
                .orderId(WarehouseConstantDataProvider.ORDER_ID.toString())
                .warehouseId(WarehouseConstantDataProvider.WAREHOUSE_ID.toString())
                .sagaId(UUID.randomUUID().toString())
                .price(WarehouseConstantDataProvider.ORDER_PRICE)
                .orderItem(orderItemProjections())
                .orderStatus(WarehouseConstantDataProvider.ORDER_STATUS)
                .createdAt(Instant.now())
                .build();
    }

    private static List<OrderItemProjection> orderItemProjections() {
        return Collections.singletonList(new OrderItemProjection(WarehouseConstantDataProvider.ORDER_ITEM_ID.toString(),
                WarehouseConstantDataProvider.PRODUCT_ID.toString(),
                WarehouseConstantDataProvider.ORDER_ITEM_PRICE,
                WarehouseConstantDataProvider.QUANTITY));
    }

    static Warehouse activeWarehouse() {
        return Warehouse.builder()
                .warehouseId(new WarehouseId(WarehouseConstantDataProvider.WAREHOUSE_ID))
                .isOpen(true)
                .warehouseName(new WarehouseName(WarehouseConstantDataProvider.WAREHOUSE_NAME))
                .build();
    }

    static Warehouse inactiveWarehouse() {
        return Warehouse.builder()
                .warehouseId(new WarehouseId(WarehouseConstantDataProvider.WAREHOUSE_ID))
                .isOpen(false)
                .warehouseName(new WarehouseName(WarehouseConstantDataProvider.WAREHOUSE_NAME))
                .build();
    }
}