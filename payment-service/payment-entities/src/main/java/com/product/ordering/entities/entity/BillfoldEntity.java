package com.product.ordering.entities.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "billfold")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BillfoldEntity {

    @Id
    private UUID id;

    private UUID customerId;
    private BigDecimal totalBillfoldAmount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (BillfoldEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
