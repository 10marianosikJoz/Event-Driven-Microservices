package com.product.ordering.application.command;

import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.CreateOrderResponse;
import com.product.ordering.application.outbox.projection.mapper.OrderOutboxMapper;
import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderCreateCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCreateCommandHandler.class);

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderCommandMapper orderCommandMapper;
    private final OrderOutboxMapper orderOutboxMapper;
    private final OrderOutboxRepository orderOutboxRepository;


    public OrderCreateCommandHandler(final OrderDomainService orderDomainService,
                                     final OrderRepository orderRepository,
                                     final OrderCommandMapper orderCommandMapper,
                                     final OrderOutboxMapper orderOutboxMapper,
                                     final OrderOutboxRepository orderOutboxRepository) {

        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderCommandMapper = orderCommandMapper;
        this.orderOutboxMapper = orderOutboxMapper;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        var order = orderCommandMapper.mapCreateOrderCommandToOrderDomainObject(createOrderCommand);
        var orderCreatedEvent = orderDomainService.createOrder(order);

        orderRepository.save(order);
        saveOrderCreatedOutboxMessage(orderCreatedEvent);

        LOGGER.info("Order successfully created with id: {}", orderCreatedEvent.getOrder().id());

        return orderCommandMapper.mapOrderDomainObjectToCreateOrderResponse(order);
    }

    private void saveOrderCreatedOutboxMessage(OrderCreatedEvent orderCreatedEvent) {
        var orderCreatedOutboxMessage = orderOutboxMapper.mapOrderCreatedEventToOrderCreatedOutboxMessage(orderCreatedEvent);

        orderOutboxRepository.save(orderCreatedOutboxMessage);
    }
}
