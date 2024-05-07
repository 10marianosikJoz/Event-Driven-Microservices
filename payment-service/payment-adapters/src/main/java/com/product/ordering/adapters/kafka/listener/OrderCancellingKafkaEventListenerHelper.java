package com.product.ordering.adapters.kafka.listener;

import com.product.ordering.application.command.CancelPaymentCommandHandler;
import com.product.ordering.application.command.projection.CancelPaymentCommand;
import com.product.ordering.domain.valueobject.OrderStatus;
import com.product.ordering.system.kafka.model.event.OrderCancellingEventKafkaProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class OrderCancellingKafkaEventListenerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCancellingKafkaEventListenerHelper.class);

    private final CancelPaymentCommandHandler cancelPaymentCommandHandler;
    private final InputMessageKafkaMapper inputMessageKafkaMapper;

    OrderCancellingKafkaEventListenerHelper(final CancelPaymentCommandHandler cancelPaymentCommandHandler,
                                            final InputMessageKafkaMapper inputMessageKafkaMapper) {

        this.cancelPaymentCommandHandler = cancelPaymentCommandHandler;
        this.inputMessageKafkaMapper = inputMessageKafkaMapper;
    }

    void handleOrderCancellingEventData(List<OrderCancellingEventKafkaProjection> messages) {
        messages.forEach(this::determineOrderStatus);
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
        cancelPaymentCommandHandler.cancelPayment(cancelPaymentCommand);
    }

    private CancelPaymentCommand mapOrderCancellingEvenntKafkaProjectionToCancelPaymentCommand(OrderCancellingEventKafkaProjection orderCancellingEventKafkaProjection) {
        return inputMessageKafkaMapper.mapOrderEventKafkaProjectionToCancelPaymentCommand(orderCancellingEventKafkaProjection);
    }
}
