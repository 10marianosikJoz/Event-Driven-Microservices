package com.product.ordering.application.mapper;

import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentMessageMapper {

    public Payment mapCompletePaymentCommandToPayment(CompletePaymentCommand completePaymentCommand) {
        return Payment.builder()
                .paymentId(new PaymentId(completePaymentCommand.paymentId()))
                .orderId(new OrderId(completePaymentCommand.orderId()))
                .customerId(new CustomerId(completePaymentCommand.customerId()))
                .price(new Money(completePaymentCommand.price()))
                .build();
    }
}
