package com.product.ordering.entities.mapper;

import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.valueobject.WarehouseName;
import com.product.ordering.entities.entity.WarehouseEntity;
import org.springframework.stereotype.Component;


@Component
public class WarehouseEntityQueryMapper {

    public Warehouse mapWarehouseJpaEntityToWarehouseDomainObject(WarehouseEntity warehouseEntity) {
        return Warehouse.builder()
                .warehouseId(new WarehouseId(warehouseEntity.getWarehouseId()))
                .warehouseName(new WarehouseName(warehouseEntity.getName()))
                .isOpen(warehouseEntity.isAvailable())
                .build();
    }
}
