package com.product.ordering.system;

import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.application.exception.PaymentNotFoundException;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.PaymentId;
import com.product.ordering.application.ports.output.PaymentRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<PaymentId, Payment> database = new ConcurrentHashMap<>();

    void truncate() {
        database.clear();
    }

    @Override
    public Payment save(Payment payment) {
        database.put(payment.id(), payment);
        return payment;
    }

    @Override
    public Payment fetchByOrderId(OrderId orderId) {
        return database.values().stream()
                                .filter(it -> orderId.equals(it.orderId()))
                                .findFirst()
                                .orElseThrow(() -> new PaymentNotFoundException("Payment with orderId: " + orderId.value() + " not found"));
    }
}
