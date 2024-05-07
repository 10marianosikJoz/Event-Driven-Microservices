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

    protected MessageKafkaProjection(final String dataId,
                                     final Instant createdAt,
                                     final T data) {

        this.dataId = dataId;
        adjustCreatedAt(createdAt);
        this.data = data;
        this.type = this.getClass().getName();
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
