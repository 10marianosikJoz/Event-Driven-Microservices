package com.product.ordering.entities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WarehouseEntity {

    @Id
    private UUID warehouseId;

    @Column(nullable = false)
    private boolean available;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (WarehouseEntity) o;
        return Objects.equals(warehouseId, that.warehouseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warehouseId);
    }
}
