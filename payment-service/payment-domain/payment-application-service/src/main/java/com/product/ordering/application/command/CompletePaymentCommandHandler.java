package com.product.ordering.application.command;

import com.product.ordering.application.mapper.PaymentMessageMapper;
import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.PaymentOutboxMapper;
import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.domain.PaymentDomainService;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.application.ports.output.BillfoldRepository;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Component
public class CompletePaymentCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompletePaymentCommandHandler.class);

    private final PaymentDomainService paymentDomainService;
    private final PaymentMessageMapper paymentMessageMapper;
    private final BillfoldRepository billfoldRepository;
    private final BillfoldHistoryRepository billfoldHistoryRepository;
    private final CommonCommandHandler commonCommandHandler;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final PaymentOutboxMapper paymentOutboxMapper;

    public CompletePaymentCommandHandler(final PaymentDomainService paymentDomainService,
                                         final PaymentMessageMapper paymentMessageMapper,
                                         final BillfoldRepository billfoldRepository,
                                         final BillfoldHistoryRepository billfoldHistoryRepository,
                                         final CommonCommandHandler commonCommandHandler,
                                         final PaymentOutboxRepository paymentOutboxRepository,
                                         final PaymentOutboxMapper paymentOutboxMapper) {

        this.paymentDomainService = paymentDomainService;
        this.paymentMessageMapper = paymentMessageMapper;
        this.billfoldRepository = billfoldRepository;
        this.billfoldHistoryRepository = billfoldHistoryRepository;
        this.commonCommandHandler = commonCommandHandler;
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.paymentOutboxMapper = paymentOutboxMapper;
    }

    @Transactional
    public void completePayment(CompletePaymentCommand completePaymentCommand) {
        var sagaId = completePaymentCommand.sagaId();

        var processedMessage = paymentOutboxRepository.existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(PaymentOutboxMessage.class,
                                                                                                                   sagaId,
                                                                                                                   PaymentStatus.COMPLETED,
                                                                                                                   OutboxStatus.COMPLETED);

        if (processedMessage) {
            LOGGER.info("Message already processed. SagaId: {}", completePaymentCommand.sagaId());
            return;
        }

        var payment = paymentMessageMapper.mapCompletePaymentCommandToPayment(completePaymentCommand);
        var billfold = billfoldRepository.fetchByCustomerId(new CustomerId(completePaymentCommand.customerId()));
        var billfoldHistory = billfoldHistoryRepository.fetchByCustomerId(new CustomerId(completePaymentCommand.customerId()));

        var failureMessages = new ArrayList<String>();

        var paymentEvent = paymentDomainService.completePayment(payment,
                                                                billfold,
                                                                failureMessages,
                                                                billfoldHistory);
        commonCommandHandler.persistToDatabase(payment,
                                               billfold,
                                               billfoldHistory,
                                               failureMessages);


        saveAppropriateOutboxMessage(payment, sagaId, paymentEvent);
    }

    private void saveAppropriateOutboxMessage(Payment payment, UUID sagaId, PaymentEvent paymentEvent) {
        if (payment.paymentStatus().equals(PaymentStatus.COMPLETED)) {
            savePaymentCompletedOutboxMessage(sagaId, paymentEvent);
        } else if (payment.paymentStatus().equals(PaymentStatus.REJECTED)) {
            savePaymentRejectedOutboxMessage(sagaId, paymentEvent);
        }
    }

    private void savePaymentCompletedOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        var paymentCompletedOutboxMessage = paymentOutboxMapper.mapPaymentEventToPaymentCompletedOutboxMessage(paymentEvent, sagaId);

        paymentOutboxRepository.save(paymentCompletedOutboxMessage);
    }

    private void savePaymentRejectedOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        var paymentRejectedOutboxMessage = paymentOutboxMapper.mapPaymentEventToPaymentRejectedOutboxMessage(paymentEvent, sagaId);

        paymentOutboxRepository.save(paymentRejectedOutboxMessage);
    }
}
