package com.product.ordering.domain.event;


import com.product.ordering.domain.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PaymentStatusEvent(String id,
                                 Instant createdAt,
                                 String orderId,
                                 String paymentId,
                                 String customerId,
                                 PaymentStatus paymentStatus,
                                 BigDecimal price,
                                 List<String> failureMessages) {

    public static PaymentStatusEventBuilder builder() {
        return new PaymentStatusEventBuilder();
    }

    public static class PaymentStatusEventBuilder {

        private String id;
        private Instant createdAt;
        private String orderId;
        private String paymentId;
        private String customerId;
        private PaymentStatus paymentStatus;
        private BigDecimal price;
        private List<String> failureMessages;

        private PaymentStatusEventBuilder() {}

        public PaymentStatusEventBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PaymentStatusEventBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PaymentStatusEventBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentStatusEventBuilder paymentId(String paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentStatusEventBuilder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public PaymentStatusEventBuilder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public PaymentStatusEventBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public PaymentStatusEventBuilder failureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public PaymentStatusEvent build() {
            return new PaymentStatusEvent(id,
                                          createdAt,
                                          orderId,
                                          paymentId,
                                          customerId,
                                          paymentStatus,
                                          price,
                                          failureMessages);
        }
    }
}
