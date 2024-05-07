package com.product.ordering.application.command;

import com.product.ordering.application.exception.WarehouseApplicationServiceException;
import com.product.ordering.application.ports.output.publisher.OrderApprovedMessagePublisher;
import com.product.ordering.application.ports.output.publisher.OrderRejectedMessagePublisher;
import com.product.ordering.application.ports.output.repository.OrderProcessedRepository;
import com.product.ordering.application.ports.output.repository.WarehouseRepository;
import com.product.ordering.domain.WarehouseDomainService;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.event.OrderApprovalEvent;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.domain.exception.WarehouseNotFoundException;
import com.product.ordering.domain.valueobject.WarehouseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class WarehouseApprovalHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarehouseApprovalHandler.class);

    private final WarehouseDomainService warehouseDomainService;
    private final OrderProcessedMapper orderProcessedMapper;
    private final WarehouseRepository warehouseRepository;
    private final OrderProcessedRepository orderProcessedRepository;
    private final OrderApprovedMessagePublisher orderApprovedEventDomainEventPublisher;
    private final OrderRejectedMessagePublisher orderRejectedEventDomainEventPublisher;


    public WarehouseApprovalHandler(final WarehouseDomainService warehouseDomainService,
                                    final OrderProcessedMapper orderProcessedMapper,
                                    final WarehouseRepository warehouseRepository,
                                    final OrderProcessedRepository orderProcessedRepository,
                                    final OrderApprovedMessagePublisher orderApprovedEventDomainEventPublisher,
                                    final OrderRejectedMessagePublisher orderRejectedEventDomainEventPublisher) {

        this.warehouseDomainService = warehouseDomainService;
        this.orderProcessedMapper = orderProcessedMapper;
        this.warehouseRepository = warehouseRepository;
        this.orderProcessedRepository = orderProcessedRepository;
        this.orderApprovedEventDomainEventPublisher = orderApprovedEventDomainEventPublisher;
        this.orderRejectedEventDomainEventPublisher = orderRejectedEventDomainEventPublisher;
    }

    @Transactional
    public OrderApprovalEvent verifyOrder(OrderPaidEvent orderPaidEvent) {
        LOGGER.info("Processing warehouse approval for order id: {}", orderPaidEvent.orderId());

        var warehouse = fetchById(orderPaidEvent);
        var orderProcessed = orderProcessedMapper.mapOrderPaidEventToOrderProcessedJpaEntity(orderPaidEvent);

        var orderApprovalEvent = warehouseDomainService.verifyOrder(warehouse,
                                                                   orderProcessed,
                                                                   orderApprovedEventDomainEventPublisher,
                                                                   orderRejectedEventDomainEventPublisher);

        orderProcessedRepository.save(orderProcessed);

        return orderApprovalEvent;
    }

    private Warehouse fetchById(OrderPaidEvent orderPaidEvent) {
        return warehouseRepository.findById(new WarehouseId(UUID.fromString(orderPaidEvent.warehouseId())))
                                  .orElseThrow(() -> new WarehouseApplicationServiceException("Warehouse with id: " + orderPaidEvent.warehouseId() + " is not present"));
    }
}
