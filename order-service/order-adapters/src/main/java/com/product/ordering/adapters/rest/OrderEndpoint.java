package com.product.ordering.adapters.rest;

import com.product.ordering.application.command.projection.CreateOrderResponse;
import com.product.ordering.application.ports.input.OrderApplicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/orders")
class OrderEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEndpoint.class);

    private final OrderApplicationService orderApplicationService;
    private final OrderQueryRestMapper orderQueryRestMapper;
    private final OrderCommandRestMapper orderCommandRestMapper;


    OrderEndpoint(final OrderApplicationService orderApplicationService,
                  final OrderCommandRestMapper orderCommandRestMapper,
                  final OrderQueryRestMapper orderQueryRestMapper) {

        this.orderApplicationService = orderApplicationService;
        this.orderCommandRestMapper = orderCommandRestMapper;
        this.orderQueryRestMapper = orderQueryRestMapper;
    }

    @PostMapping
    ResponseEntity<CreateOrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest createOrderRequest) {
        LOGGER.info("Creating an order, customer: {} at warehouse: {}", createOrderRequest.customerId(),
                                                                        createOrderRequest.warehouseId());

        var createdOrder = orderApplicationService.createOrder(orderCommandRestMapper.mapCreateOrderRequestToCreateOrderCommand(createOrderRequest));
        LOGGER.info("Order created with id: {}", createdOrder.orderId());

        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping("/{orderId}")
    ResponseEntity<OrderDetailSummaryProjection> fetchOrder(@PathVariable(name = "orderId") UUID orderId) {
        LOGGER.info("Fetching data about the order: {}", orderId);

        var orderResponse = orderQueryRestMapper.mapOrderProjectionViewToOrderDetailSummaryProjection(orderApplicationService.fetchOrder(orderId));
        LOGGER.info("Order with id: {} fetched", orderId);

        return ResponseEntity.ok(orderResponse);
    }
}
