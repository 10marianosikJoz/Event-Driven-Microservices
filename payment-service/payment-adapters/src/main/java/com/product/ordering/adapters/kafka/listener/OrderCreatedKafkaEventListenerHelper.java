package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.exception.PaymentApplicationException;
import com.product.ordering.application.exception.PaymentNotFoundException;
import com.product.ordering.application.ports.input.listener.PaymentRequestMessageListener;
import com.product.ordering.application.command.projection.CompletePaymentCommand;
import com.product.ordering.domain.valueobject.OrderStatus;
import com.product.ordering.system.kafka.model.event.OrderCreatedEventKafkaProjection;
import org.postgresql.util.PSQLState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
class OrderCreatedKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreatedKafkaEventListenerHelper.class);

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final InputMessageKafkaMapper inputMessageKafkaMapper;

    OrderCreatedKafkaEventListenerHelper(final PaymentRequestMessageListener paymentRequestMessageListener,
                                         final InputMessageKafkaMapper inputMessageKafkaMapper) {

        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
    }

    void handleOrderCreateEventData(List<OrderCreatedEventKafkaProjection> messages) {
        messages.forEach(it -> {
            try {
                determineOrderStatus(it);
            } catch (DataAccessException e) {
                var sqlException = (SQLException) e.getRootCause();

                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    LOGGER.error("Unique constraint exception with sql state: {} " +
                            "in OrderCreatedKafkaEventListenerHelper occurred. Order id: {}", sqlException.getSQLState(), it.getData().orderId());
                } else {
                    throw new PaymentApplicationException("DataAccessException in OrderCreatedKafkaEventListenerHelper: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                LOGGER.error("Payment not found for order: {}", it.getData().orderId());
            }
        });
    }

    private void determineOrderStatus(OrderCreatedEventKafkaProjection orderCompletedEventKafkaProjection) {
            verifyOrderStatus(orderCompletedEventKafkaProjection);
    }

    private void verifyOrderStatus(OrderCreatedEventKafkaProjection orderCompletedEventKafkaProjection) {
        if (OrderStatus.PENDING.name().equals(orderCompletedEventKafkaProjection.getData().orderStatus())) {
            LOGGER.info("Conducting payment process for order with id: {}", orderCompletedEventKafkaProjection.getDataId());
            conductCompletePaymentProcess(orderCompletedEventKafkaProjection);

        }
    }

    private void conductCompletePaymentProcess(OrderCreatedEventKafkaProjection orderCreatedEventKafkaProjection) {
        var completePaymentCommand = mapOrderCreatedEventKafkaProjectionToCompletePaymentCommand(orderCreatedEventKafkaProjection);
        paymentRequestMessageListener.completePayment(completePaymentCommand);
    }

    private CompletePaymentCommand mapOrderCreatedEventKafkaProjectionToCompletePaymentCommand(OrderCreatedEventKafkaProjection orderCreatedEventKafkaProjection) {
        return inputMessageKafkaMapper.mapOrderEventKafkaProjectionToCompletePaymentCommand(orderCreatedEventKafkaProjection);

    }
}
