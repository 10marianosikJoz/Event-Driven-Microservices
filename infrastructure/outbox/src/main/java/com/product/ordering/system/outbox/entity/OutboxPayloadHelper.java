package com.product.ordering.system.outbox.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.product.ordering.system.outbox.model.OutboxPayload;
import org.springframework.stereotype.Component;

@Component
class OutboxPayloadHelper {

    private final ObjectMapper objectMapper;

    OutboxPayloadHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    <T extends OutboxPayload> T extractMessagePayloadFromJsonNode(JsonNode payload, Class<T> outputType) {
        return objectMapper.convertValue(payload, outputType);
    }

    <T extends OutboxPayload> JsonNode mapMessagePayloadToJsonNode(T payload) {
        return objectMapper.convertValue(payload, JsonNode.class);
    }
}
