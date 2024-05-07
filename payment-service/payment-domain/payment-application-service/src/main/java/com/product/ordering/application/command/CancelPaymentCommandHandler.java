package com.product.ordering.application.command;

import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.domain.PaymentDomainService;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.application.ports.output.BillfoldRepository;
import com.product.ordering.application.ports.output.PaymentRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class CancelPaymentCommandHandler {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final BillfoldRepository billfoldRepository;
    private final BillfoldHistoryRepository billfoldHistoryRepository;
    private final CommonCommandHandler commonCommandHandler;
    final PaymentStatusMessagePublisher paymentStatusMessagePublisher;

    public CancelPaymentCommandHandler(final PaymentDomainService paymentDomainService,
                                       final PaymentRepository paymentRepository,
                                       final BillfoldRepository billfoldRepository,
                                       final BillfoldHistoryRepository billfoldHistoryRepository,
                                       final CommonCommandHandler commonCommandHandler,
                                       final PaymentStatusMessagePublisher paymentStatusMessagePublisher) {

        this.paymentDomainService = paymentDomainService;
        this.paymentRepository = paymentRepository;
        this.billfoldRepository = billfoldRepository;
        this.billfoldHistoryRepository = billfoldHistoryRepository;
        this.commonCommandHandler = commonCommandHandler;
        this.paymentStatusMessagePublisher = paymentStatusMessagePublisher;
    }

    @Transactional
    public PaymentEvent cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        var payment = paymentRepository.fetchByOrderId(new OrderId(cancelPaymentCommand.orderId()));
        var billfold = billfoldRepository.fetchByCustomerId(new CustomerId(cancelPaymentCommand.customerId()));
        var billfoldHistory = billfoldHistoryRepository.fetchByCustomerId(new CustomerId(cancelPaymentCommand.customerId()));

        List<String> failureMessages = new ArrayList<>();

        var paymentEvent = paymentDomainService.cancelPayment(payment,
                                                              billfold,
                                                              failureMessages,
                                                              billfoldHistory,
                                                              paymentStatusMessagePublisher);
        commonCommandHandler.persistToDatabase(payment,
                                               billfold,
                                               billfoldHistory,
                                               failureMessages);

        return paymentEvent;
    }
}
