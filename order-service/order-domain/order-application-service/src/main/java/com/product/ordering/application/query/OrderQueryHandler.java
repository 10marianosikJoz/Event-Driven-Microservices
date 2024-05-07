package com.product.ordering.application.query;

import com.product.ordering.application.ports.output.repository.OrderQueryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class OrderQueryHandler {

    private final OrderQueryRepository orderQueryRepository;

    OrderQueryHandler(final OrderQueryRepository orderQueryRepository) {
        this.orderQueryRepository = orderQueryRepository;
    }

    @Transactional(readOnly = true)
    public OrderProjectionView orderById(UUID orderId) {
        return orderQueryRepository.fetchOrderProjectionView(orderId);
    }
}
