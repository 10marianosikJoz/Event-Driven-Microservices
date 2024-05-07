package com.product.ordering.entities.entity;

import com.product.ordering.domain.valueobject.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "billfold_history")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BillfoldHistoryEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private UUID customerId;
    private BigDecimal amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (BillfoldHistoryEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
 }
