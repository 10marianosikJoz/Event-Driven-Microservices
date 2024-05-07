package com.product.ordering.domain.entity;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.domain.exception.PaymentDomainException;
import com.product.ordering.domain.valueobject.PaymentId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Payment extends AggregateRoot<PaymentId> {

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    private final OrderId orderId;
    private final Money price;
    private final CustomerId customerId;

    private Instant createdAt;
    private PaymentStatus paymentStatus;

    private Payment(PaymentBuilder paymentBuilder) {

        id(paymentBuilder.paymentId);
        this.orderId = paymentBuilder.orderId;
        this.price = paymentBuilder.price;
        this.customerId = paymentBuilder.customerId;
        this.createdAt = paymentBuilder.createdAt;
        this.paymentStatus = paymentBuilder.paymentStatus;
    }

    public void initializePayment() {
        id(new PaymentId(UUID.randomUUID()));
        createdAt = Instant.now();
    }

    public void completePayment() {
        if (paymentStatus == PaymentStatus.COMPLETED || paymentStatus == PaymentStatus.CANCELLED) {
            throw new PaymentDomainException(DomainConstants.PAYMENT_INCORRECT_STATE);
        }
        paymentStatus = PaymentStatus.COMPLETED;
    }

    public void cancelPayment() {
        if (paymentStatus == PaymentStatus.CANCELLED) {
            throw new PaymentDomainException(DomainConstants.PAYMENT_INCORRECT_STATE);
        }
        paymentStatus = PaymentStatus.CANCELLED;
    }

    public void rejectPayment() {
        paymentStatus = PaymentStatus.REJECTED;
    }

    public void validatePayment(List<String> failureMessages) {
        if (price == null || !price.isGreaterThanZero()) {
            failureMessages.add(DomainConstants.ORDER_INCORRECT_TOTAL_PRICE);
            throw new PaymentDomainException(DomainConstants.PAYMENT_INCORRECT_PRICE);
        }
    }

    public void updatePaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderId orderId() {
        return orderId;
    }

    public Money price() {
        return price;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public PaymentStatus paymentStatus() {
        return paymentStatus;
    }

    public static final class PaymentBuilder {

        private PaymentId paymentId;
        private OrderId orderId;
        private Money price;
        private CustomerId customerId;
        private Instant createdAt;
        private PaymentStatus paymentStatus;

        private PaymentBuilder() {}

        public PaymentBuilder paymentId(PaymentId paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public PaymentBuilder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public PaymentBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public PaymentBuilder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public PaymentBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public PaymentBuilder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
