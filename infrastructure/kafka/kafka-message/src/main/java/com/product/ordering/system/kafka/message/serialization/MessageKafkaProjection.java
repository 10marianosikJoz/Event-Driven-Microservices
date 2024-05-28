package com.product.ordering.system.kafka.message.serialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

import static java.util.UUID.randomUUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageKafkaProjection<T extends Serializable> implements TypeProjection, Serializable {

    private final String messageId = randomUUID().toString();

    private String dataId;
    private Instant createdAt;
    private T data;
    private String type;
    private String sagaId;

    protected MessageKafkaProjection(String dataId,
                                     Instant createdAt,
                                     T data,
                                     String sagaId) {

        this.dataId = dataId;
        adjustCreatedAt(createdAt);
        this.data = data;
        this.type = this.getClass().getName();
        this.sagaId = sagaId;
    }

    public Instant createdAt() {
        return Instant.now();
    }

    @Override
    public String type() {
        return type;
    }

    private void adjustCreatedAt(Instant createdAt) {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        this.createdAt = createdAt;
    }
}
