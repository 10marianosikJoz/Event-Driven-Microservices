package com.product.ordering.entities.entity.outbox;

import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.outbox.entity.OutboxEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PaymentOutboxEntity extends OutboxEntity {

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
