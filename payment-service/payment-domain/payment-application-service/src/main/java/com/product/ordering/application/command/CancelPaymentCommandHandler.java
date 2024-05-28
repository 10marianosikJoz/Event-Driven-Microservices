package com.product.ordering.application.command;

import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.PaymentOutboxMapper;
import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.domain.PaymentDomainService;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.application.ports.output.BillfoldRepository;
import com.product.ordering.application.ports.output.PaymentRepository;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CancelPaymentCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancelPaymentCommandHandler.class);

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final BillfoldRepository billfoldRepository;
    private final BillfoldHistoryRepository billfoldHistoryRepository;
    private final CommonCommandHandler commonCommandHandler;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final PaymentOutboxMapper paymentOutboxMapper;

    public CancelPaymentCommandHandler(final PaymentDomainService paymentDomainService,
                                       final PaymentRepository paymentRepository,
                                       final BillfoldRepository billfoldRepository,
                                       final BillfoldHistoryRepository billfoldHistoryRepository,
                                       final CommonCommandHandler commonCommandHandler,
                                       final PaymentOutboxRepository paymentOutboxRepository,
                                       final PaymentOutboxMapper paymentOutboxMapper) {

        this.paymentDomainService = paymentDomainService;
        this.paymentRepository = paymentRepository;
        this.billfoldRepository = billfoldRepository;
        this.billfoldHistoryRepository = billfoldHistoryRepository;
        this.commonCommandHandler = commonCommandHandler;
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.paymentOutboxMapper = paymentOutboxMapper;
    }

    @Transactional
    public void cancelPayment(CancelPaymentCommand cancelPaymentCommand) {
        var sagaId = cancelPaymentCommand.sagaId();

        var processedMessage = paymentOutboxRepository.existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(PaymentOutboxMessage.class,
                                                                                                                   sagaId,
                                                                                                                   PaymentStatus.COMPLETED,
                                                                                                                   OutboxStatus.COMPLETED);

        if (processedMessage) {
            LOGGER.info("Message already processed. SagaId: {}", cancelPaymentCommand.sagaId());
            return;
        }

        var payment = paymentRepository.fetchByOrderId(new OrderId(cancelPaymentCommand.orderId()));
        var billfold = billfoldRepository.fetchByCustomerId(new CustomerId(cancelPaymentCommand.customerId()));
        var billfoldHistory = billfoldHistoryRepository.fetchByCustomerId(new CustomerId(cancelPaymentCommand.customerId()));

        List<String> failureMessages = new ArrayList<>();

        var paymentEvent = paymentDomainService.cancelPayment(payment,
                                                              billfold,
                                                              failureMessages,
                                                              billfoldHistory);
        commonCommandHandler.persistToDatabase(payment,
                                               billfold,
                                               billfoldHistory,
                                               failureMessages);

        if (payment.paymentStatus().equals(PaymentStatus.CANCELLED)) {
            savePaymentCancelledOutboxMessage(sagaId, paymentEvent);
        } else if (payment.paymentStatus().equals(PaymentStatus.REJECTED)) {
            savePaymentRejectedOutboxMessage(sagaId, paymentEvent);
        }
    }

    private void savePaymentCancelledOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        var paymentCancelledOutboxMessage = paymentOutboxMapper.mapPaymentEventToPaymentCancelledOutboxMessage(paymentEvent, sagaId);

        paymentOutboxRepository.save(paymentCancelledOutboxMessage);
    }

    private void savePaymentRejectedOutboxMessage(UUID sagaId, PaymentEvent paymentEvent) {
        var paymentRejectedOutboxMessage = paymentOutboxMapper.mapPaymentEventToPaymentRejectedOutboxMessage(paymentEvent, sagaId);

        paymentOutboxRepository.save(paymentRejectedOutboxMessage);
    }
}
