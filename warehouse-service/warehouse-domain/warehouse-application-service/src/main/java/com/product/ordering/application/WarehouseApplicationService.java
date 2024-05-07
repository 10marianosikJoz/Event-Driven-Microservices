package com.product.ordering.application;

import com.product.ordering.application.command.WarehouseApprovalHandler;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.application.ports.input.WarehouseApprovalMessageListener;
import org.springframework.stereotype.Service;

@Service
public class WarehouseApplicationService implements WarehouseApprovalMessageListener {

    private final WarehouseApprovalHandler warehouseApprovalHandler;

    WarehouseApplicationService(WarehouseApprovalHandler warehouseApprovalHandler) {
        this.warehouseApprovalHandler = warehouseApprovalHandler;
    }

    @Override
    public void verifyOrder(OrderPaidEvent orderPaidEvent) {
        var orderApprovalEvent = warehouseApprovalHandler.verifyOrder(orderPaidEvent);
        orderApprovalEvent.run();
    }
}
