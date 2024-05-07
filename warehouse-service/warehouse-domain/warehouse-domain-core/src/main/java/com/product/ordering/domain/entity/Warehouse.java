package com.product.ordering.domain.entity;

import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.domain.exception.WarehouseDomainException;
import com.product.ordering.domain.valueobject.WarehouseName;

public class Warehouse extends DomainEntity<WarehouseId> {

    public static WarehouseBuilder builder() {
        return new WarehouseBuilder();
    }

    private final WarehouseName warehouseName;
    private final boolean isOpen;

    private Warehouse(WarehouseBuilder warehouseBuilder) {

        id(warehouseBuilder.warehouseId);
        warehouseName = warehouseBuilder.warehouseName;
        isOpen = warehouseBuilder.isOpen;
    }

    public void checkIfAvailable() {
        if (!isOpen) {
            throw new WarehouseDomainException("Warehouse with id: " + id() + " is not open");
        }
    }

    public WarehouseName warehouseName() {
        return warehouseName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public static final class WarehouseBuilder {

        private WarehouseId warehouseId;
        private boolean isOpen;
        private WarehouseName warehouseName;

        private WarehouseBuilder() {}

        public WarehouseBuilder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public WarehouseBuilder warehouseName(WarehouseName warehouseName) {
            this.warehouseName = warehouseName;
            return this;
        }

        public WarehouseBuilder isOpen(boolean isOpen) {
            this.isOpen = isOpen;
            return this;
        }

        public Warehouse build() {
            return new Warehouse(this);
        }
    }
}
