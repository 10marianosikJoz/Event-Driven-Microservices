package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.application.exception.PaymentApplicationException;
import com.product.ordering.application.exception.PaymentNotFoundException;
import com.product.ordering.application.ports.input.listener.PaymentRequestMessageListener;
import com.product.ordering.domain.valueobject.OrderStatus;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import org.postgresql.util.PSQLState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
class OrderCancellingKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCancellingKafkaEventListenerHelper.class);

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final InputMessageKafkaMapper inputMessageKafkaMapper;

    OrderCancellingKafkaEventListenerHelper(final PaymentRequestMessageListener paymentRequestMessageListener,
                                            final InputMessageKafkaMapper inputMessageKafkaMapper) {

        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
    }

    void handleOrderCancellingEventData(List<OrderCancellingEventKafkaProjection> messages) {
        messages.forEach(it -> {
            try {
                determineOrderStatus(it);
            } catch (DataAccessException e) {
                var sqlException = (SQLException) e.getRootCause();

                if (sqlException != null && sqlException.getSQLState() != null &&
                        PSQLState.UNIQUE_VIOLATION.getState().equals(sqlException.getSQLState())) {
                    LOGGER.error("Unique constraint exception with sql state: {} " +
                            "in OrderCancellingEventKafkaListenerHelper occurred. Order id: {}", sqlException.getSQLState(), it.getData().orderId());
                } else {
                    throw new PaymentApplicationException("DataAccessException in OrderCancellingEventKafkaListenerHelper: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                LOGGER.error("Payment not found for order: {}", it.getData().orderId());
            }
        });
    }

    private void determineOrderStatus(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        verifyOrderStatus(orderCancellingEventKafkaProjection);
    }

    private void verifyOrderStatus(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        if (OrderStatus.CANCELLING.name().equals(orderCancellingEventKafkaProjection.getData().orderStatus())) {
            LOGGER.info("Conducting cancel payment process for order with id: {}", orderCancellingEventKafkaProjection.getDataId());
            conductCancelPaymentProcess(orderCancellingEventKafkaProjection);
        }
    }

    private void conductCancelPaymentProcess(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        var cancelPaymentCommand = mapOrderCancellingEvenntKafkaProjectionToCancelPaymentCommand(orderCancellingEventKafkaProjection);
        paymentRequestMessageListener.cancelPayment(cancelPaymentCommand);
    }

    private CancelPaymentCommand mapOrderCancellingEvenntKafkaProjectionToCancelPaymentCommand(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        return inputMessageKafkaMapper.mapOrderEventKafkaProjectionToCancelPaymentCommand(orderCancellingEventKafkaProjection);
    }
}
