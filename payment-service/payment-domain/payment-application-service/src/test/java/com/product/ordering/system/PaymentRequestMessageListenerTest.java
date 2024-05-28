package com.product.ordering.system;

import com.product.ordering.application.mapper.PaymentMessageMapper;
import com.product.ordering.application.outbox.projection.PaymentStatusEventPayload;
import com.product.ordering.application.outbox.projection.mapper.PaymentOutboxMapper;
import com.product.ordering.application.ports.input.listener.PaymentRequestMessageListener;
import com.product.ordering.domain.PaymentDomainService;
import com.product.ordering.domain.valueobject.*;
import com.product.ordering.application.command.CommonCommandHandler;
import com.product.ordering.application.command.CancelPaymentCommandHandler;
import com.product.ordering.application.command.CompletePaymentCommandHandler;
import com.product.ordering.application.exception.BillfoldNotFoundException;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentRequestMessageListenerTest {

    private final PaymentDomainService paymentDomainService = new PaymentDomainService();
    private final PaymentMessageMapper paymentMessageMapper = new PaymentMessageMapper();
    private final InMemoryPaymentRepository inMemoryPaymentRepository = new InMemoryPaymentRepository();
    private final InMemoryBillfoldRepository inMemoryBillfoldRepository = new InMemoryBillfoldRepository();
    private final InMemoryBillfoldHistoryRepository inMemoryBillfoldHistoryRepository = new InMemoryBillfoldHistoryRepository();
    private final CommonCommandHandler commonCommandHandler = new CommonCommandHandler(inMemoryPaymentRepository,
                                                                                       inMemoryBillfoldRepository,
                                                                                       inMemoryBillfoldHistoryRepository);
    private final PaymentOutboxMapper paymentOutboxMapper = new PaymentOutboxMapper();
    private final InMemoryPaymentOutboxRepository inMemoryPaymentOutboxRepository = new InMemoryPaymentOutboxRepository();

    private final CompletePaymentCommandHandler completePaymentCommandHandler = new CompletePaymentCommandHandler(paymentDomainService,
                                                                                                                  paymentMessageMapper,
                                                                                                                  inMemoryBillfoldRepository,
                                                                                                                  inMemoryBillfoldHistoryRepository,
                                                                                                                  commonCommandHandler,
                                                                                                                  inMemoryPaymentOutboxRepository,
                                                                                                                  paymentOutboxMapper);

    private final CancelPaymentCommandHandler cancelPaymentCommandHandler = new CancelPaymentCommandHandler(paymentDomainService,
                                                                                                            inMemoryPaymentRepository,
                                                                                                            inMemoryBillfoldRepository,
                                                                                                            inMemoryBillfoldHistoryRepository,
                                                                                                            commonCommandHandler,
                                                                                                            inMemoryPaymentOutboxRepository,
                                                                                                            paymentOutboxMapper);

    private final PaymentRequestMessageListener paymentRequestMessageListener = new PaymentRequestMessageListener(completePaymentCommandHandler, cancelPaymentCommandHandler);

    @AfterEach
    void truncate() {
        inMemoryPaymentRepository.truncate();
        inMemoryBillfoldRepository.truncate();
        inMemoryBillfoldHistoryRepository.truncate();
        inMemoryPaymentOutboxRepository.truncate();
    }

    @Test
    void shouldBeAbleToCompletePaymentProcess() {
        //given
        var billfold = PaymentDataProvider.billfold(PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT);
        inMemoryBillfoldRepository.save(billfold);
        var paymentCompleteCommand = PaymentDataProvider.validPaymentCompleteCommand();
        var billfoldHistory = PaymentDataProvider.billfoldHistory();
        inMemoryBillfoldHistoryRepository.save(billfoldHistory);

        //when
        paymentRequestMessageListener.completePayment(paymentCompleteCommand);

        //then
        var paymentFromDatabase = inMemoryPaymentRepository.fetchByOrderId(new OrderId(PaymentConstantDataProvider.ORDER_ID));
        assertThat(paymentFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId())
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.COMPLETED);

        var billfoldFromDatabase = inMemoryBillfoldRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("customerId", billfoldFromDatabase.customerId())
                .extracting(Billfold::totalBillfoldAmount).isEqualTo(new Money(PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT.subtract(PaymentConstantDataProvider.PAYMENT_PRICE)));

        var billfoldHistoryFromDatabase = inMemoryBillfoldHistoryRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldHistoryFromDatabase).isNotEmpty();
        assertThat(billfoldHistoryFromDatabase.get(0))
                .hasFieldOrProperty("customerId")
                .hasFieldOrPropertyWithValue("transactionType", TransactionType.VIRTUAL_BILLFOLD)
                .extracting(BillfoldHistory::amount).isEqualTo(new Money(PaymentConstantDataProvider.PAYMENT_PRICE));

        var outboxMessage = inMemoryPaymentOutboxRepository.findLastByPaymentId(new PaymentId(paymentFromDatabase.id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.COMPLETED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", paymentFromDatabase.id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (PaymentStatusEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", paymentFromDatabase.id().value().toString())
                .hasFieldOrPropertyWithValue("createdAt", paymentFromDatabase.createdAt())
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId().value().toString())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId().value().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", paymentFromDatabase.paymentStatus().toString())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price().amount());
    }

    @Test
    void shouldNotBeAbleToCompletePaymentProcessWhenBillfoldIsNotPresent() {
        //given
        var payment = PaymentDataProvider.payment(PaymentConstantDataProvider.PAYMENT_PRICE);
        inMemoryPaymentRepository.save(payment);
        var paymentCompleteCommand = PaymentDataProvider.validPaymentCompleteCommand();
        var expectedMessage = "Billfold with customerId: " + paymentCompleteCommand.customerId() + " not found";

        //when
        var walletNotFoundException = assertThrows(BillfoldNotFoundException.class,
                () -> paymentRequestMessageListener.completePayment(paymentCompleteCommand));

        //then
        assertThat(walletNotFoundException.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldNotBeAbleToCompletePaymentProcessWhenPaymentPriceIsIncorrect() {
        //given
        var billfold = PaymentDataProvider.billfold((PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT));
        inMemoryBillfoldRepository.save(billfold);
        var billfoldHistory = PaymentDataProvider.billfoldHistory();
        inMemoryBillfoldHistoryRepository.save(billfoldHistory);
        var paymentCompleteCommand = PaymentDataProvider.invalidPaymentCommandPrice();

        //when
       paymentRequestMessageListener.completePayment(paymentCompleteCommand);

       //then
        var paymentFromDatabase = inMemoryPaymentRepository.fetchByOrderId(new OrderId(PaymentConstantDataProvider.ORDER_ID));
        assertThat(paymentFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId())
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.REJECTED);

        var billfoldFromDatabase = inMemoryBillfoldRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("customerId", billfoldFromDatabase.customerId())
                .extracting(Billfold::totalBillfoldAmount).isEqualTo(new Money(PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT));

        var billfoldHistoryFromDatabase = inMemoryBillfoldHistoryRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldHistoryFromDatabase).isNotEmpty();
        assertThat(billfoldHistoryFromDatabase.get(0))
                .hasFieldOrProperty("customerId")
                .hasFieldOrPropertyWithValue("transactionType", TransactionType.VIRTUAL_BILLFOLD)
                .extracting(BillfoldHistory::amount).isEqualTo(new Money(PaymentConstantDataProvider.PAYMENT_PRICE));

        var outboxMessage = inMemoryPaymentOutboxRepository.findLastByPaymentId(new PaymentId(paymentFromDatabase.id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.REJECTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", paymentFromDatabase.id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (PaymentStatusEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", paymentFromDatabase.id().value().toString())
                .hasFieldOrPropertyWithValue("createdAt", paymentFromDatabase.createdAt())
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId().value().toString())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId().value().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", paymentFromDatabase.paymentStatus().toString())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price().amount());

    }

    @Test
    void shouldNotBeAbleToCompletePaymentProcessWhenVirtualBillfoldDoesNotHaveEnoughMoney() {
        //given
        var billfold = PaymentDataProvider.billfold((PaymentConstantDataProvider.BILLFOLD_NOT_ENOUGH_AMOUNT));
        inMemoryBillfoldRepository.save(billfold);
        var paymentCompleteCommand = PaymentDataProvider.invalidPaymentCommandPrice();
        var billfoldHistory = PaymentDataProvider.billfoldHistory();
        inMemoryBillfoldHistoryRepository.save(billfoldHistory);

        //when
        paymentRequestMessageListener.completePayment(paymentCompleteCommand);

        //then
        var paymentFromDatabase = inMemoryPaymentRepository.fetchByOrderId(new OrderId(PaymentConstantDataProvider.ORDER_ID));
        assertThat(paymentFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId())
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.REJECTED);

        var billfoldFromDatabase = inMemoryBillfoldRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("customerId", billfoldFromDatabase.customerId())
                .extracting(Billfold::totalBillfoldAmount).isEqualTo(new Money(PaymentConstantDataProvider.BILLFOLD_NOT_ENOUGH_AMOUNT));

        var billfoldHistoryFromDatabase = inMemoryBillfoldHistoryRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldHistoryFromDatabase).isNotEmpty();
        assertThat(billfoldHistoryFromDatabase.get(0))
                .hasFieldOrProperty("customerId")
                .hasFieldOrPropertyWithValue("transactionType", TransactionType.VIRTUAL_BILLFOLD)
                .extracting(BillfoldHistory::amount).isEqualTo(new Money(PaymentConstantDataProvider.PAYMENT_PRICE));

        var outboxMessage = inMemoryPaymentOutboxRepository.findLastByPaymentId(new PaymentId(paymentFromDatabase.id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.REJECTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", paymentFromDatabase.id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (PaymentStatusEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", paymentFromDatabase.id().value().toString())
                .hasFieldOrPropertyWithValue("createdAt", paymentFromDatabase.createdAt())
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId().value().toString())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId().value().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", paymentFromDatabase.paymentStatus().toString())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price().amount());

    }

    @Test
    void shouldBeAbleToCancelPaymentProcess() {
        //given
        var payment = PaymentDataProvider.payment(PaymentConstantDataProvider.PAYMENT_PRICE);
        inMemoryPaymentRepository.save(payment);
        var billfold = PaymentDataProvider.billfold((PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT));
        inMemoryBillfoldRepository.save(billfold);
        var cancelPaymentCommand = PaymentDataProvider.validCancelPaymentCommand();
        var billfoldHistory = PaymentDataProvider.billfoldHistory();
        inMemoryBillfoldHistoryRepository.save(billfoldHistory);

        //when
        paymentRequestMessageListener.cancelPayment(cancelPaymentCommand);

        //then
        var paymentFromDatabase = inMemoryPaymentRepository.fetchByOrderId(new OrderId(PaymentConstantDataProvider.ORDER_ID));
        assertThat(paymentFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId())
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.CANCELLED);

        var billfoldFromDatabase = inMemoryBillfoldRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldFromDatabase)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("customerId", billfoldFromDatabase.customerId())
                .extracting(Billfold::totalBillfoldAmount).isEqualTo(new Money(PaymentConstantDataProvider.BILLFOLD_TOTAL_AMOUNT.add(PaymentConstantDataProvider.PAYMENT_PRICE)));

        var billfoldHistoryFromDatabase = inMemoryBillfoldHistoryRepository.fetchByCustomerId(new CustomerId(PaymentConstantDataProvider.CUSTOMER_ID));

        assertThat(billfoldHistoryFromDatabase).isNotEmpty();
        assertThat(billfoldHistoryFromDatabase.get(0))
                .hasFieldOrProperty("customerId")
                .hasFieldOrPropertyWithValue("transactionType", TransactionType.VIRTUAL_BILLFOLD)
                .extracting(BillfoldHistory::amount).isEqualTo(new Money(PaymentConstantDataProvider.BILLFOLD_HISTORY_ENTRY_AMOUNT));

        var outboxMessage = inMemoryPaymentOutboxRepository.findLastByPaymentId(new PaymentId(paymentFromDatabase.id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("paymentStatus", PaymentStatus.CANCELLED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", paymentFromDatabase.id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (PaymentStatusEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("paymentId", paymentFromDatabase.id().value().toString())
                .hasFieldOrPropertyWithValue("createdAt", paymentFromDatabase.createdAt())
                .hasFieldOrPropertyWithValue("orderId", paymentFromDatabase.orderId().value().toString())
                .hasFieldOrPropertyWithValue("customerId", paymentFromDatabase.customerId().value().toString())
                .hasFieldOrPropertyWithValue("paymentStatus", paymentFromDatabase.paymentStatus().toString())
                .hasFieldOrPropertyWithValue("price", paymentFromDatabase.price().amount());
    }
}