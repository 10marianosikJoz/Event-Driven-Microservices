package com.product.ordering.domain;

import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.event.OrderApprovedEvent;
import com.product.ordering.domain.event.OrderApprovalEvent;
import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.domain.exception.WarehouseDomainException;

import java.time.Instant;

public class WarehouseDomainService {

    public OrderApprovalEvent verifyOrder(Warehouse warehouse,
                                          OrderProcessed orderProcessed) {

        try {
            orderProcessed.initialize();
            warehouse.checkIfAvailable();
            orderProcessed.validateTotalAmount();
            orderProcessed.accept();

            return new OrderApprovedEvent(orderProcessed,
                                          warehouse.id(),
                                          Instant.now());
        } catch (WarehouseDomainException e) {
            orderProcessed.reject();

            return new OrderRejectedEvent(orderProcessed,
                                          warehouse.id(),
                                          Instant.now(),
                                          e.getMessage());
        }
    }
}
