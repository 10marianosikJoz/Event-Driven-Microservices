package com.product.ordering.system;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;
import com.product.ordering.domain.valueobject.BillfoldId;
import com.product.ordering.domain.valueobject.PaymentId;
import com.product.ordering.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

class PaymentDataProvider {

    static CompletePaymentCommand validPaymentCompleteCommand() {
        return CompletePaymentCommand.builder()
                .orderId(PaymentConstantDataProvider.ORDER_ID)
                .customerId(PaymentConstantDataProvider.CUSTOMER_ID)
                .price(PaymentConstantDataProvider.PAYMENT_PRICE)
                .build();
    }

    static CompletePaymentCommand invalidPaymentCommandPrice() {
        return CompletePaymentCommand.builder()
                .orderId(PaymentConstantDataProvider.ORDER_ID)
                .customerId(PaymentConstantDataProvider.CUSTOMER_ID)
                .price(PaymentConstantDataProvider.INVALID_PAYMENT_PRICE)
                .build();
    }

    static CancelPaymentCommand validCancelPaymentCommand() {
        return CancelPaymentCommand.builder()
                .orderId(PaymentConstantDataProvider.ORDER_ID)
                .customerId(PaymentConstantDataProvider.CUSTOMER_ID)
                .price(PaymentConstantDataProvider.PAYMENT_PRICE)
                .build();
    }

    static Payment payment(BigDecimal price) {
        return Payment.builder()
                .paymentId(new PaymentId(PaymentConstantDataProvider.PAYMENT_ID))
                .orderId(new OrderId(PaymentConstantDataProvider.ORDER_ID))
                .price(new Money(price))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .createdAt(Instant.now())
                .build();
    }

    static Billfold billfold(BigDecimal price) {
        return Billfold.builder()
                .billfoldId(new BillfoldId(PaymentConstantDataProvider.BILLFOLD_ID))
                .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                .totalBillfoldAmount(new Money(price))
                .build();
    }

    static BillfoldHistory billfoldHistory() {
        return BillfoldHistory.builder()
                        .billfoldId(new BillfoldHistoryId(PaymentConstantDataProvider.BILLFOLD_ID))
                        .customerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID))
                        .amount(new Money(PaymentConstantDataProvider.BILLFOLD_HISTORY_ENTRY_AMOUNT))
                        .transactionType(TransactionType.VIRTUAL_BILLFOLD)
                        .build();
    }
}
