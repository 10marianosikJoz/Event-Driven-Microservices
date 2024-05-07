package com.product.ordering.application.ports.input;

import com.product.ordering.application.command.OrderCreateCommandHandler;
import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.CreateOrderResponse;
import com.product.ordering.application.query.OrderProjectionView;
import com.product.ordering.application.query.OrderQueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderApplicationService {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final OrderQueryHandler orderQueryHandler;

    OrderApplicationService(final OrderCreateCommandHandler orderCreateCommandHandler,
                            final OrderQueryHandler orderQueryHandler) {

        this.orderCreateCommandHandler = orderCreateCommandHandler;
        this.orderQueryHandler = orderQueryHandler;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        return orderCreateCommandHandler.createOrder(createOrderCommand);
    }

    public OrderProjectionView fetchOrder(UUID orderId) {
        return orderQueryHandler.orderById(orderId);
    }
}
