package com.product.ordering.domain.event;

import com.product.ordering.domain.valueobject.OrderApprovalStatus;

import java.time.Instant;
import java.util.List;

public record WarehouseApprovalEvent(String id,
                                     Instant createdAt,
                                     String orderId,
                                     String sagaId,
                                     String warehouseId,
                                     OrderApprovalStatus orderApprovalStatus,
                                     List<String> failureMessages) {

    public static WarehouseApprovalEventBuilder builder() {
        return new WarehouseApprovalEventBuilder();
    }

    public static class WarehouseApprovalEventBuilder {

        private String id;
        private Instant createdAt;
        private String orderId;
        private String sagaId;
        private String warehouseId;
        private OrderApprovalStatus orderApprovalStatus;
        private List<String> failureMessages;

        private WarehouseApprovalEventBuilder() {}

        public WarehouseApprovalEventBuilder id(String id) {
            this.id = id;
            return this;
        }

        public WarehouseApprovalEventBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WarehouseApprovalEventBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public WarehouseApprovalEventBuilder sagaId(String sagaId) {
            this.sagaId = sagaId;
            return this;
        }

        public WarehouseApprovalEventBuilder warehouseId(String warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public WarehouseApprovalEventBuilder orderApprovalStatus(OrderApprovalStatus orderApprovalStatus) {
            this.orderApprovalStatus = orderApprovalStatus;
            return this;
        }

        public WarehouseApprovalEventBuilder failureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public WarehouseApprovalEvent build() {
            return new WarehouseApprovalEvent(id,
                                              createdAt,
                                              orderId,
                                              sagaId,
                                              warehouseId,
                                              orderApprovalStatus,
                                              failureMessages);
        }
    }
}


