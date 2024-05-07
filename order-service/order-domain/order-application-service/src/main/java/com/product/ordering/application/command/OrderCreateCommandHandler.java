package com.product.ordering.application.command;

import com.product.ordering.application.command.projection.CreateOrderCommand;
import com.product.ordering.application.command.projection.CreateOrderResponse;
import com.product.ordering.application.ports.output.publisher.OrderCreatedEventPublisher;
import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
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
    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher;


    public OrderCreateCommandHandler(final OrderDomainService orderDomainService,
                                     final OrderRepository orderRepository,
                                     final OrderCommandMapper orderCommandMapper,
                                     final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventPublisher) {

        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.orderCommandMapper = orderCommandMapper;
        this.orderCreatedEventPublisher = orderCreatedEventPublisher;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        var order = orderCommandMapper.mapCreateOrderCommandToOrderDomainObject(createOrderCommand);
        var orderCreatedEvent = orderDomainService.createOrder(order, orderCreatedEventPublisher);

        orderRepository.save(order);

        orderCreatedEventPublisher.publish(orderCreatedEvent);
        LOGGER.info("Order successfully created with id: {}", orderCreatedEvent.getOrder().id());

        return orderCommandMapper.mapOrderDomainObjectToCreateOrderResponse(order);
    }
}
