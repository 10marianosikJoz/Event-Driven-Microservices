package com.product.ordering.application.ports.input;

import com.product.ordering.application.command.projection.OrderPaidEvent;

public interface WarehouseApprovalMessageListener {

    void verifyOrder(OrderPaidEvent orderPaidEvent);
}
