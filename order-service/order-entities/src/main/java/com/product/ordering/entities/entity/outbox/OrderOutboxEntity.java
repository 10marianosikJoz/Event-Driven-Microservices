package com.product.ordering.entities.entity.outbox;

import com.product.ordering.system.outbox.entity.OutboxEntity;
import com.product.ordering.system.saga.SagaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderOutboxEntity extends OutboxEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus sagaStatus;
}