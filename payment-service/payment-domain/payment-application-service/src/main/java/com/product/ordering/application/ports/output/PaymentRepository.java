package com.product.ordering.application.ports.output;

import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.entity.Payment;

public interface PaymentRepository {

    Payment save(Payment payment);

    Payment fetchByOrderId(OrderId orderId);
}
