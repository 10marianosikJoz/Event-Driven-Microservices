package com.product.ordering.application;

import com.product.ordering.application.ports.output.repository.WarehouseRepository;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryWarehouseRepository implements WarehouseRepository {

    static final Map<WarehouseId, Warehouse> DATABASE = new ConcurrentHashMap<>();

    void truncate() {
        DATABASE.clear();
    }

    @Override
    public Optional<Warehouse> findById(WarehouseId warehouseId) {
        var fetched = DATABASE.get(warehouseId);
        return Optional.ofNullable(fetched);
    }

    void putActiveWarehouseToDatabase() {
        var warehouse = WarehouseDataProvider.activeWarehouse();
        DATABASE.put(warehouse.id(), warehouse);
    }

    void putInactiveWarehouseToDatabase() {
        var warehouse = WarehouseDataProvider.inactiveWarehouse();
        DATABASE.put(warehouse.id(), warehouse);
    }
}
