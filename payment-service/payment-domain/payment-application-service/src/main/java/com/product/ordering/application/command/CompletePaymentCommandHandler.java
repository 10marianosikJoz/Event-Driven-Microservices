package com.product.ordering.application.command;

import com.product.ordering.application.mapper.PaymentMessageMapper;
import com.product.ordering.application.ports.input.publisher.PaymentStatusMessagePublisher;
import com.product.ordering.domain.PaymentDomainService;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.application.ports.output.BillfoldRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Component
public class CompletePaymentCommandHandler {

    private final PaymentDomainService paymentDomainService;
    private final PaymentMessageMapper paymentMessageMapper;
    private final BillfoldRepository billfoldRepository;
    private final BillfoldHistoryRepository billfoldHistoryRepository;
    private final CommonCommandHandler commonCommandHandler;
    private final PaymentStatusMessagePublisher paymentStatusMessagePublisher;

    public CompletePaymentCommandHandler(final PaymentDomainService paymentDomainService,
                                         final PaymentMessageMapper paymentMessageMapper,
                                         final BillfoldRepository billfoldRepository,
                                         final BillfoldHistoryRepository billfoldHistoryRepository,
                                         final CommonCommandHandler commonCommandHandler,
                                         final PaymentStatusMessagePublisher paymentStatusMessagePublisher) {

        this.paymentDomainService = paymentDomainService;
        this.paymentMessageMapper = paymentMessageMapper;
        this.billfoldRepository = billfoldRepository;
        this.billfoldHistoryRepository = billfoldHistoryRepository;
        this.commonCommandHandler = commonCommandHandler;
        this.paymentStatusMessagePublisher = paymentStatusMessagePublisher;
    }

    @Transactional
    public PaymentEvent completePayment(CompletePaymentCommand completePaymentCommand) {
        var payment = paymentMessageMapper.mapCompletePaymentCommandToPayment(completePaymentCommand);
        var billfold = billfoldRepository.fetchByCustomerId(new CustomerId(completePaymentCommand.customerId()));
        var billfoldHistory = billfoldHistoryRepository.fetchByCustomerId(new CustomerId(completePaymentCommand.customerId()));

        var failureMessages = new ArrayList<String>();

        var paymentEvent = paymentDomainService.completePayment(payment,
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
