package com.product.ordering.application.ports.input.listener;

import com.product.ordering.application.command.CancelPaymentCommandHandler;
import com.product.ordering.application.command.CompletePaymentCommandHandler;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.event.PaymentEvent;
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
        var paymentEvent = completePaymentCommandHandler.completePayment(completePaymentCommand);
        fireEvent(paymentEvent);
    }

    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        var paymentEvent = cancelPaymentCommandHandler.cancelPayment(cancelPaymentCommand);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}",
                paymentEvent.payment().id().value(),
                paymentEvent.payment().orderId().value());
        paymentEvent.run();
    }
}
