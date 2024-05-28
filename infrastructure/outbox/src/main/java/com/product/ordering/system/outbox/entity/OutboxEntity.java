package com.product.ordering.system.outbox.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.product.ordering.system.outbox.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JsonAsStringJdbcType;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Convert(attributeName = "entityAttrName", converter = JsonAsStringJdbcType.class)
@MappedSuperclass
public class OutboxEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID sagaId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant processedAt;

    @Column(nullable = false)
    private String payloadType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private JsonNode payload;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String messageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus outboxStatus;

    @Column(nullable = false)
    @Version
    private int version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (OutboxEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

