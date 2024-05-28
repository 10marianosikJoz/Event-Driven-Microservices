package com.product.ordering.application;

import com.product.ordering.application.command.WarehouseApprovalHandler;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.application.ports.input.WarehouseApprovalMessageListener;
import org.springframework.stereotype.Service;

@Service
public class WarehouseApplicationService implements WarehouseApprovalMessageListener {

    private final WarehouseApprovalHandler warehouseApprovalHandler;

    WarehouseApplicationService(final WarehouseApprovalHandler warehouseApprovalHandler) {
        this.warehouseApprovalHandler = warehouseApprovalHandler;
    }

    @Override
    public void verifyOrder(OrderPaidEvent orderPaidEvent) {
        warehouseApprovalHandler.verifyOrder(orderPaidEvent);
    }
}
