package com.product.ordering.application.ports.input.listener;

import com.product.ordering.application.command.CancelPaymentCommandHandler;
import com.product.ordering.application.command.CompletePaymentCommandHandler;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentRequestMessageListener {

    private final CompletePaymentCommandHandler completePaymentCommandHandler;
    private final CancelPaymentCommandHandler cancelPaymentCommandHandler;

    public PaymentRequestMessageListener(final CompletePaymentCommandHandler completePaymentCommandHandler,
                                         final CancelPaymentCommandHandler cancelPaymentCommandHandler) {

        this.completePaymentCommandHandler = completePaymentCommandHandler;
        this.cancelPaymentCommandHandler = cancelPaymentCommandHandler;
    }

    public void completePayment(CompletePaymentCommand completePaymentCommand) {
        completePaymentCommandHandler.completePayment(completePaymentCommand);
    }

    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        cancelPaymentCommandHandler.cancelPayment(cancelPaymentCommand);
    }
}
