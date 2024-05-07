package com.product.ordering.entities.mapper;

import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentId;
import com.product.ordering.entities.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentEntityCommandMapper {

    public Payment mapPaymentJpaEntityToPaymentDomainObject(PaymentEntity payment) {
        return Payment.builder()
                .paymentId(new PaymentId(payment.getId()))
                .customerId(new CustomerId(payment.getCustomerId()))
                .orderId(new OrderId(payment.getOrderId()))
                .price(new Money(payment.getPrice()))
                .createdAt(payment.getCreatedAt())
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }

    public PaymentEntity mapDomainPaymentObjectToPaymentJpaEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.id().value())
                .customerId(payment.customerId().value())
                .orderId(payment.orderId().value())
                .price(payment.price().amount())
                .createdAt(payment.createdAt())
                .paymentStatus(payment.paymentStatus())
                .build();
    }
}
