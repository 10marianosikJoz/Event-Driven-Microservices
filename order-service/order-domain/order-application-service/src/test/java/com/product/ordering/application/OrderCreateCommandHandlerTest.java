package com.product.ordering.application;

import com.product.ordering.application.command.OrderCommandMapper;
import com.product.ordering.application.command.OrderCreateCommandHandler;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.event.OrderCreatedEvent;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.exception.OrderDomainException;
import com.product.ordering.domain.valueobject.OrderId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class OrderCreateCommandHandlerTest {

    private final OrderDomainService orderDomainService = new OrderDomainService();
    private final InMemoryOrderRepository inMemoryOrderRepository = new InMemoryOrderRepository();
    private final OrderCommandMapper orderCommandMapper = new OrderCommandMapper();
    private final DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher = new OrderCratedEventMessagePublisherMock();
    private final OrderCreateCommandHandler orderCreateCommandHandler = new OrderCreateCommandHandler(orderDomainService,
                                                                                                      inMemoryOrderRepository,
                                                                                                      orderCommandMapper,
                                                                                                      orderCreatedEventDomainEventPublisher);
    @AfterEach
    void truncate() {
        inMemoryOrderRepository.truncate();
    }

    @Test
    void shouldCreateOrder() {
       //given
        var validCreateOrderCommand = OrderDataProvider.validOrderCommand();

        //when
        var createOrderResponse = orderCreateCommandHandler.createOrder(validCreateOrderCommand);

        //then
        var order = inMemoryOrderRepository.fetchOrder(new OrderId(createOrderResponse.orderId()));
        assertThat(order).isNotNull()
                .hasFieldOrPropertyWithValue("id", order.id())
                .hasFieldOrPropertyWithValue("customerId", order.customerId())
                .hasFieldOrPropertyWithValue("warehouseId", order.warehouseId())
                .hasFieldOrPropertyWithValue("price", order.price())
                .hasFieldOrPropertyWithValue("orderStatus", OrderConstantDataProvider.ORDER_STATUS_PENDING)
                .extracting(Order::orderItems)
                .satisfies(orderItems -> {
                    assertThat(orderItems.stream().findFirst().get().quantity().quantity()).isEqualTo(4);
                    assertThat(orderItems.stream().findFirst().get().price().amount()).isEqualTo(validCreateOrderCommand.orderItems().stream().findFirst().get().price());
                    assertThat(orderItems.stream().findFirst().get().product().price().amount()).isEqualTo(validCreateOrderCommand.orderItems().stream().findFirst().get().price());
                    assertThat(order)
                            .extracting(Order::deliveryAddress)
                            .hasFieldOrPropertyWithValue("street", validCreateOrderCommand.address().street())
                            .hasFieldOrPropertyWithValue("postalCode", validCreateOrderCommand.address().postalCode())
                            .hasFieldOrPropertyWithValue("city", validCreateOrderCommand.address().city());
                });
    }


    @Test
    void shouldNotBeAbleToCreateOrderWhenTotalPriceIsIncorrect() {
        //given
        var orderCommandWithIncorrectTotalPrice = OrderDataProvider.orderCommandWithIncorrectTotalPrice();

        //when
        var orderDomainException = assertThrows(OrderDomainException.class,
                                                () -> orderCreateCommandHandler.createOrder(orderCommandWithIncorrectTotalPrice));

        //then
        assertThat(orderDomainException.getMessage()).isEqualTo("Total price: " +  OrderConstantDataProvider.INCORRECT_ORDER_PRICE + " is not equal to Order items total: " + OrderConstantDataProvider.ORDER_PRICE);
    }
}
