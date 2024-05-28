package com.product.ordering.application;

import com.product.ordering.application.command.OrderProcessedMapper;
import com.product.ordering.application.command.WarehouseApprovalHandler;
import com.product.ordering.application.outbox.projection.OrderProcessedEventPayload;
import com.product.ordering.application.outbox.projection.mapper.OrderProcessedOutboxMapper;
import com.product.ordering.domain.WarehouseDomainService;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.OrderProcessedId;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WarehouseApplicationServiceTest {

    private final WarehouseDomainService warehouseDomainService = new WarehouseDomainService();
    private final OrderProcessedMapper orderProcessedMapper = new OrderProcessedMapper();
    private final InMemoryWarehouseRepository inMemoryWarehouseRepository = new InMemoryWarehouseRepository();
    private final InMemoryOrderProcessedRepository inMemoryOrderProcessedRepository = new InMemoryOrderProcessedRepository();
    private final InMemoryOrderProcessedOutboxRepository inMemoryOrderProcessedOutboxRepository = new InMemoryOrderProcessedOutboxRepository();
    private final OrderProcessedOutboxMapper orderProcessedOutboxMapper = new OrderProcessedOutboxMapper();
    private final WarehouseApprovalHandler warehouseApprovalHandler = new WarehouseApprovalHandler(warehouseDomainService,
                                                                                                   orderProcessedMapper,
                                                                                                   inMemoryWarehouseRepository,
                                                                                                   inMemoryOrderProcessedRepository,
                                                                                                   inMemoryOrderProcessedOutboxRepository,
                                                                                                   orderProcessedOutboxMapper);

    private final WarehouseApplicationService warehouseApplicationService = new WarehouseApplicationService(warehouseApprovalHandler);

    @AfterEach
    void truncate() {
        inMemoryWarehouseRepository.truncate();
        inMemoryOrderProcessedRepository.truncate();
        inMemoryOrderProcessedOutboxRepository.truncate();
    }

    @Test
    void shouldAcceptOrder() {
        //given
        inMemoryWarehouseRepository.putActiveWarehouseToDatabase();
        var orderPaidEvent = WarehouseDataProvider.orderPaidEvent();

        //when
        warehouseApplicationService.verifyOrder(orderPaidEvent);

        //then
        var orderProcessed = inMemoryOrderProcessedRepository.findById(new OrderProcessedId(UUID.fromString(orderPaidEvent.orderId())));

        assertThat(orderProcessed)
                .isPresent().get()
                .hasFieldOrPropertyWithValue("id.value", UUID.fromString(orderPaidEvent.orderId()))
                .hasFieldOrPropertyWithValue("warehouseId.value", UUID.fromString(orderPaidEvent.warehouseId()))
                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.price())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", OrderApprovalStatus.APPROVED)
                .extracting(OrderProcessed::orderItems)
                .satisfies(it ->
                        assertThat(it.get(0))
                                .hasFieldOrProperty("id.value")
                                .hasFieldOrPropertyWithValue("productId.value", UUID.fromString(orderPaidEvent.orderItem().get(0).productId()))
                                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.orderItem().get(0).price())
                                .hasFieldOrPropertyWithValue("quantity.amount", orderPaidEvent.orderItem().get(0).quantity()));

        var outboxMessage = inMemoryOrderProcessedOutboxRepository.findByOrderProcessedId(new OrderId(orderProcessed.get().id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("orderApprovalStatus", OrderApprovalStatus.APPROVED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", orderProcessed.get().id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (OrderProcessedEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("orderId", orderProcessed.get().id().value().toString())
                .hasFieldOrPropertyWithValue("warehouseId", orderProcessed.get().warehouseId().value().toString())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", orderProcessed.get().orderApprovalStatus().name())
                .hasFieldOrPropertyWithValue("failureMessages", null);
    }


    @Test
    void shouldRejectOrderWhenWarehouseIsUnavailable() {

        //given
        inMemoryWarehouseRepository.putInactiveWarehouseToDatabase();
        var orderPaidEvent = WarehouseDataProvider.orderPaidEvent();

        //when
        warehouseApplicationService.verifyOrder(orderPaidEvent);

        //then
        var orderProcessed = inMemoryOrderProcessedRepository.findById(new OrderProcessedId(UUID.fromString(orderPaidEvent.orderId())));

        assertThat(orderProcessed)
                .isPresent().get()
                .hasFieldOrPropertyWithValue("id.value", UUID.fromString(orderPaidEvent.orderId()))
                .hasFieldOrPropertyWithValue("warehouseId.value", UUID.fromString(orderPaidEvent.warehouseId()))
                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.price())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", OrderApprovalStatus.REJECTED)
                .extracting(OrderProcessed::orderItems)
                .satisfies(it ->
                        assertThat(it.get(0))
                                .hasFieldOrProperty("id.value")
                                .hasFieldOrPropertyWithValue("productId.value", UUID.fromString(orderPaidEvent.orderItem().get(0).productId()))
                                .hasFieldOrPropertyWithValue("price.amount", orderPaidEvent.orderItem().get(0).price())
                                .hasFieldOrPropertyWithValue("quantity.amount", orderPaidEvent.orderItem().get(0).quantity()));

        var outboxMessage = inMemoryOrderProcessedOutboxRepository.findByOrderProcessedId(new OrderId(orderProcessed.get().id().value())).get();

        assertThat(outboxMessage)
                .hasFieldOrPropertyWithValue("orderApprovalStatus", OrderApprovalStatus.REJECTED)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrProperty("processedAt")
                .hasFieldOrPropertyWithValue("aggregateId", orderProcessed.get().id().value())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .extracting(it -> (OrderProcessedEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("orderId", orderProcessed.get().id().value().toString())
                .hasFieldOrPropertyWithValue("warehouseId", orderProcessed.get().warehouseId().value().toString())
                .hasFieldOrPropertyWithValue("orderApprovalStatus", orderProcessed.get().orderApprovalStatus().name())
                .hasFieldOrPropertyWithValue("failureMessages", "Warehouse with id: " + orderProcessed.get().warehouseId() + " is not open");
    }
}