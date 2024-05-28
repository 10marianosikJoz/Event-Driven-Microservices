package com.product.ordering.application.command;

import com.product.ordering.application.exception.WarehouseApplicationServiceException;
import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.projection.mapper.OrderProcessedOutboxMapper;
import com.product.ordering.application.outbox.repository.OrderProcessedOutboxRepository;
import com.product.ordering.application.ports.output.repository.OrderProcessedRepository;
import com.product.ordering.application.ports.output.repository.WarehouseRepository;
import com.product.ordering.domain.WarehouseDomainService;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.event.OrderApprovalEvent;
import com.product.ordering.application.command.projection.OrderPaidEvent;
import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.system.outbox.model.OutboxStatus;
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
    private final OrderProcessedOutboxRepository orderProcessedOutboxRepository;
    private final OrderProcessedOutboxMapper orderProcessedOutboxMapper;


    public WarehouseApprovalHandler(final WarehouseDomainService warehouseDomainService,
                                    final OrderProcessedMapper orderProcessedMapper,
                                    final WarehouseRepository warehouseRepository,
                                    final OrderProcessedRepository orderProcessedRepository,
                                    final OrderProcessedOutboxRepository orderProcessedOutboxRepository,
                                    final OrderProcessedOutboxMapper orderProcessedOutboxMapper) {

        this.warehouseDomainService = warehouseDomainService;
        this.orderProcessedMapper = orderProcessedMapper;
        this.warehouseRepository = warehouseRepository;
        this.orderProcessedRepository = orderProcessedRepository;
        this.orderProcessedOutboxRepository = orderProcessedOutboxRepository;
        this.orderProcessedOutboxMapper = orderProcessedOutboxMapper;
    }

    @Transactional
    public void verifyOrder(OrderPaidEvent orderPaidEvent) {
        LOGGER.info("Processing warehouse approval for order id: {}", orderPaidEvent.orderId());

        var processedMessage = orderProcessedOutboxRepository.existByTypeAndSagaIdAndOutboxStatus(OrderProcessedOutboxMessage.class,
                                                                                                  UUID.fromString(orderPaidEvent.sagaId()),
                                                                                                  OutboxStatus.COMPLETED);

        if (processedMessage) {
            LOGGER.info("Outbox message already saved to database. SagaId: {}", orderPaidEvent.sagaId());
            return;
        }



        var warehouse = fetchWarehouseById(orderPaidEvent);
        var orderProcessed = orderProcessedMapper.mapOrderPaidEventToOrderProcessedJpaEntity(orderPaidEvent);

        var orderApprovalEvent = warehouseDomainService.verifyOrder(warehouse,
                                                                   orderProcessed);

        orderProcessedRepository.save(orderProcessed);

        saveOrderProcessedOutboxMessage(orderPaidEvent, orderApprovalEvent);
    }

    private Warehouse fetchWarehouseById(OrderPaidEvent orderPaidEvent) {
        return warehouseRepository.findById(new WarehouseId(UUID.fromString(orderPaidEvent.warehouseId())))
                                  .orElseThrow(() -> new WarehouseApplicationServiceException("Warehouse with id: " + orderPaidEvent.warehouseId() + " is not present"));
    }

    private void saveOrderProcessedOutboxMessage(OrderPaidEvent orderPaidEvent, OrderApprovalEvent orderApprovalEvent) {
        var orderProcessedOutboxMessage = orderProcessedOutboxMapper.mapOrderProcessedEventToOrderProcessedOutboxMessage(orderApprovalEvent, UUID.fromString(orderPaidEvent.sagaId()));

        orderProcessedOutboxRepository.save(orderProcessedOutboxMessage);
    }
}
