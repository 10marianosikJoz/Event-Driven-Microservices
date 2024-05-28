package com.product.ordering.system.outbox.model;

import com.product.ordering.system.saga.SagaStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OutboxMessage<P extends OutboxPayload> {

    private UUID id;
    private UUID sagaId;
    private Instant createdAt;
    private Instant processedAt;
    private P payload;
    private UUID aggregateId;
    private OutboxStatus outboxStatus;
    private SagaStatus sagaStatus;
    private int version;
}
