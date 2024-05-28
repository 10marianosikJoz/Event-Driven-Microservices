package com.product.ordering.application.saga;

import com.product.ordering.domain.valueobject.OrderStatus;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.system.saga.SagaStatus;
import org.springframework.stereotype.Component;

@Component
public class SagaStatusMapper {

    public SagaStatus[] mapPaymentStatusToSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case REJECTED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
            case FAILED, CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
        };
    }

    public SagaStatus mapOrderStatusToSagaStatus(OrderStatus orderStatus) {
        return switch (orderStatus) {
            case PAID -> SagaStatus.PROCESSING;
            case APPROVED -> SagaStatus.SUCCEEDED;
            case CANCELLING -> SagaStatus.COMPENSATING;
            case CANCELLED -> SagaStatus.COMPENSATED;
            default -> SagaStatus.STARTED;
        };
    }
}
