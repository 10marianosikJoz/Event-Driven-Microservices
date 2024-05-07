package com.product.ordering.application.ports.output.repository;

import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.util.Optional;

public interface WarehouseRepository {

    Optional<Warehouse> findById(WarehouseId warehouseId);
}
