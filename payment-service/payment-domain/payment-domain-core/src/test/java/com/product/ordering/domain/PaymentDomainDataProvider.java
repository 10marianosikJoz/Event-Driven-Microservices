package com.product.ordering.domain;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.valueobject.PaymentId;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;
import com.product.ordering.domain.valueobject.BillfoldId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class PaymentDomainDataProvider {

    public static Payment payment() {
        return Payment.builder()
                .orderId(new OrderId(PaymentConstantDataProvider.ORDER_ID))
                .price(new Money(PaymentConstantDataProvider.ORDER_PRICE))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .build();
    }

    public static Payment paymentToReject() {
        return Payment.builder()
                .paymentId(new PaymentId(PaymentConstantDataProvider.PAYMENT_ID))
                .orderId(new OrderId(PaymentConstantDataProvider.ORDER_ID))
                .price(new Money(PaymentConstantDataProvider.ORDER_PRICE))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .createdAt(Instant.now())
                .build();
    }

    public static Payment paymentWithWrongPrice() {
        return Payment.builder()
                .paymentId(new PaymentId(PaymentConstantDataProvider.PAYMENT_ID))
                .orderId(new OrderId(PaymentConstantDataProvider.ORDER_ID))
                .price(new Money(PaymentConstantDataProvider.WRONG_PRICE))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .createdAt(Instant.now())
                .build();
    }

    public static Payment paymentWithVariousState(PaymentStatus paymentStatus) {
        return Payment.builder()
                .paymentId(new PaymentId(PaymentConstantDataProvider.PAYMENT_ID))
                .orderId(new OrderId(PaymentConstantDataProvider.ORDER_ID))
                .price(new Money(PaymentConstantDataProvider.ORDER_PRICE))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .paymentStatus(paymentStatus)
                .createdAt(Instant.now())
                .build();
    }

    static ArrayList<BillfoldHistory> billfoldHistory() {
        var history = new ArrayList<BillfoldHistory>();

        history.add(BillfoldHistory.builder()
                .billfoldId(new BillfoldHistoryId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .amount(new Money(PaymentConstantDataProvider.BILLFOLD_MONEY_SPENT))
                .build());
        return history;
    }

    static Billfold billfold() {
        return Billfold.builder()
                .billfoldId(new BillfoldId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .totalBillfoldAmount(new Money(PaymentConstantDataProvider.BILLFOLD_TOTAL_MONEY_AMOUNT))
                .build();
    }
}
