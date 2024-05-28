package saga;

import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.saga.OrderPaymentSaga;
import com.product.ordering.container.OrderServiceApplication;
import com.product.ordering.domain.event.PaymentStatusEvent;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.saga.SagaStatus;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest(classes = OrderServiceApplication.class)
@Sql(value = {"classpath:sql/OrderPaymentSagaTestSetUp.sql"})
@Sql(value = {"classpath:sql/OrderPaymentSagaTestCleanUp.sql"}, executionPhase = AFTER_TEST_METHOD)
class OrderPaymentSagaTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentSagaTest.class);

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private OrderOutboxRepository orderOutboxRepository;

    private final UUID SAGA_ID = UUID.fromString("2dbf52f8-4d08-4d03-bf1d-0dfec68497cb");
    private final UUID ORDER_ID = UUID.fromString("43c5c9da-6797-4ee2-8dd9-30a46e3b2451");
    private final UUID CUSTOMER_ID = UUID.fromString("08fcccc6-ae0a-49c3-bd98-905d8a1f8d1c");
    private final BigDecimal PRICE = new BigDecimal("150.00");

    @Test
    void shouldNotProcessTheSameCompletedPaymentStatusEventAgain() {
        //given
        var paymentEventsBeforeProcessing = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertThat(paymentEventsBeforeProcessing).hasSize(1);

        //when
        orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED));

        //then
        var paymentEventsAfterProcessing = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertThat(paymentEventsAfterProcessing).hasSize(2);

        //when
        orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED));

        //then
        var paymentEventsAfterRepeatingTheSamePaymentEvent = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertThat(paymentEventsAfterRepeatingTheSamePaymentEvent).hasSize(2);

    }

    @Test
    void shouldProcessJustOnceCompletedPaymentStatusEventWhenIsProcessingByThreeThreads() throws InterruptedException {
        //given
        var paymentEventsBeforeProcessing = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertThat(paymentEventsBeforeProcessing).hasSize(1);

        //when
        var countDownLatch = new CountDownLatch(3);

        var firstThread = new Thread(() -> {
            try {
                orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED));
            } catch (OptimisticLockingFailureException e) {
                LOGGER.error("OptimisticLockingFailureException from firstThread");
            } finally {
                countDownLatch.countDown();
            }
        });

        var secondThread = new Thread(() -> {
            try {
                orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED));
            } catch (OptimisticLockingFailureException e) {
                LOGGER.error("OptimisticLockingFailureException from secondThread");
            } finally {
                countDownLatch.countDown();
            }
        });

        var thirdThread = new Thread(() -> {
            try {
                orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED));
            } catch (OptimisticLockingFailureException e) {
                LOGGER.error("OptimisticLockingFailureException from thirdThread");
            } finally {
                countDownLatch.countDown();
            }
        });

        firstThread.start();
        secondThread.start();
        thirdThread.start();

        countDownLatch.await();

        //then
        var paymentEventsAfterRepeatingTheSamePaymentEvent = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertThat(paymentEventsAfterRepeatingTheSamePaymentEvent).hasSize(2);
    }

    @Test
    void shouldProcessJustOnceCompletedPaymentStatusEventWhenIsProcessingByTwoThreads() throws InterruptedException {
        //given
        var paymentEventsBeforeProcessing = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.STARTED);
        assertThat(paymentEventsBeforeProcessing).hasSize(1);

        //when
        var firstThread = new Thread(() -> orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED)));
        var secondThread = new Thread(() -> orderPaymentSaga.process(paymentStatusEvent(PaymentStatus.COMPLETED)));

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        //then
        var paymentEventsAfterRepeatingTheSamePaymentEvent = orderOutboxRepository.findBySagaIdAndSagaStatus(SAGA_ID, SagaStatus.PROCESSING);
        assertThat(paymentEventsAfterRepeatingTheSamePaymentEvent).hasSize(2);
    }

    private PaymentStatusEvent paymentStatusEvent(PaymentStatus paymentStatus) {
        return PaymentStatusEvent.builder()
                .id(UUID.randomUUID().toString())
                .paymentId(UUID.randomUUID().toString())
                .sagaId(SAGA_ID.toString())
                .orderId(ORDER_ID.toString())
                .customerId(CUSTOMER_ID.toString())
                .paymentStatus(paymentStatus)
                .price(PRICE)
                .createdAt(Instant.now())
                .build();
    }
}
