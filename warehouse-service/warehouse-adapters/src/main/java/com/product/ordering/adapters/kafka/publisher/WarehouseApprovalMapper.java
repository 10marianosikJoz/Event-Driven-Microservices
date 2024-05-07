package com.product.ordering.adapters.kafka.publisher;

import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.domain.event.OrderApprovedEvent;
import com.product.ordering.domain.event.OrderRejectedEvent;
import com.product.ordering.system.kafka.model.projection.WarehouseApprovalMessageProjection;
import com.product.ordering.system.kafka.model.event.WarehouseApprovalEventKafkaProjection;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class WarehouseApprovalMapper {

    WarehouseApprovalEventKafkaProjection mapOrderApprovedEventToWarehouseApprovalEventKafkaProjection(OrderApprovedEvent orderApprovedEvent) {
        var orderProcessed = orderApprovedEvent.orderProcessed();
        var warehouseMessageDto = buildWarehouseMessageProjection(orderProcessed);

        return new WarehouseApprovalEventKafkaProjection(warehouseMessageDto,
                                                         orderApprovedEvent.orderProcessed().id().toString(),
                                                         orderApprovedEvent.createdAt());
    }

    WarehouseApprovalEventKafkaProjection mapOrderRejectedEventToWarehouseApprovalEventKafkaProjection(OrderRejectedEvent orderRejectedEvent) {
        var orderProcessed = orderRejectedEvent.orderProcessed();
        var warehouseMessageDto = buildWarehouseMessageProjection(orderProcessed);

        return new WarehouseApprovalEventKafkaProjection(warehouseMessageDto,
                                                         orderRejectedEvent.orderProcessed().id().toString(),
                                                         orderRejectedEvent.createdAt());
    }

    WarehouseApprovalMessageProjection buildWarehouseMessageProjection(OrderProcessed orderProcessed) {
       return WarehouseApprovalMessageProjection.builder()
                .orderId(orderProcessed.id().value().toString())
                .warehouseId(orderProcessed.warehouseId().value().toString())
                .orderApprovalStatus(orderProcessed.orderApprovalStatus().toString())
                .failureMessages(List.of())
                .build();
    }
}
