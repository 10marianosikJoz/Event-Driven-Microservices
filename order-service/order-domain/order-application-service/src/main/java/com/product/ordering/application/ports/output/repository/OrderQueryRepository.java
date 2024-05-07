package com.product.ordering.application.ports.output.repository;

import com.product.ordering.application.query.OrderProjectionView;

import java.util.UUID;

public interface OrderQueryRepository {

    OrderProjectionView fetchOrderProjectionView(UUID orderId);
}
