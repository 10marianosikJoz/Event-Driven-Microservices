package com.product.ordering.entities.entity;

import com.product.ordering.domain.valueobject.Coupon;
import com.product.ordering.domain.valueobject.PaymentMethod;
import com.product.ordering.domain.valueobject.Currency;
import com.product.ordering.domain.valueobject.DeliveryMethod;
import com.product.ordering.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Transient
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private DeliveryAddressEntity deliveryAddressEntity;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<OrderItemEntity> orderItemEntities;

    private UUID customerId;
    private UUID warehouseId;
    private BigDecimal price;
    private String failureMessages;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var order = (OrderEntity) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

