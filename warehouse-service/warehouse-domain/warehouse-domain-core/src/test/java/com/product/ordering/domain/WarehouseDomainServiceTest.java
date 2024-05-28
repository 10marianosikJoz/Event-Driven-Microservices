package com.product.ordering.domain;

import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseDomainServiceTest {

    private final WarehouseDomainService warehouseDomainService = new WarehouseDomainService();

    @Test
    void shouldAcceptOrder() {
        //given
        var isOpen = true;
        var warehouse = WarehouseDomainDataProvider.warehouse(isOpen);
        var orderProcessed = WarehouseDomainDataProvider.orderProcessed(WarehouseConstantDataProvider.CORRECT_ORDER_ITEM_PRICE);

        //when
        var orderApprovalEvent = warehouseDomainService.verifyOrder(warehouse,
                                                                    orderProcessed);

        //then
        assertThat(orderApprovalEvent.orderProcessed().orderApprovalStatus()).isEqualTo(OrderApprovalStatus.APPROVED);
    }

    @Test
    void shouldRejectOrderWhenWarehouseIsUnavailable() {
        //given
        var isOpen = false;
        var warehouse = WarehouseDomainDataProvider.warehouse(isOpen);
        var orderProcessed = WarehouseDomainDataProvider.orderProcessed(WarehouseConstantDataProvider.CORRECT_ORDER_ITEM_PRICE);

        //when
        var orderApprovalEvent = warehouseDomainService.verifyOrder(warehouse,
                                                                    orderProcessed);

        //then
        assertThat(orderApprovalEvent.orderProcessed().orderApprovalStatus()).isEqualTo(OrderApprovalStatus.REJECTED);
    }

    @Test
    void shouldRejectOrderWhenOrderTotalAmountIsDifferentThanOrderItemsTotalAmount() {
        //given
        var isOpen = true;
        var warehouse = WarehouseDomainDataProvider.warehouse(isOpen);
        var orderProcessed = WarehouseDomainDataProvider.orderProcessed(WarehouseConstantDataProvider.INCORRECT_ORDER_ITEM_PRICE);


        //when
        var orderApprovalEvent = warehouseDomainService.verifyOrder(warehouse,
                                                                    orderProcessed);

        //then
        assertThat(orderApprovalEvent.orderProcessed().orderApprovalStatus()).isEqualTo(OrderApprovalStatus.REJECTED);
    }
}