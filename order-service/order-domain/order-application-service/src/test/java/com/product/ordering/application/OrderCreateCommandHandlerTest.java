package com.product.ordering.application;

import com.product.ordering.application.command.OrderCommandMapper;
import com.product.ordering.application.command.OrderCreateCommandHandler;
import com.product.ordering.application.outbox.projection.OrderEventPayload;
import com.product.ordering.application.outbox.projection.mapper.OrderOutboxMapper;
import com.product.ordering.application.saga.SagaStatusMapper;
import com.product.ordering.domain.OrderDomainService;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.exception.OrderDomainException;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.saga.SagaStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class OrderCreateCommandHandlerTest {

    private final OrderDomainService orderDomainService = new OrderDomainService();
    private final InMemoryOrderRepository inMemoryOrderRepository = new InMemoryOrderRepository();
    private final OrderCommandMapper orderCommandMapper = new OrderCommandMapper();
    private final SagaStatusMapper sagaStatusMapper = new SagaStatusMapper();
    private final OrderOutboxMapper orderOutboxMapper = new OrderOutboxMapper(sagaStatusMapper);
    private final InMemoryOrderOutboxRepository inMemoryOrderOutboxRepository = new InMemoryOrderOutboxRepository();
    private final OrderCreateCommandHandler orderCreateCommandHandler = new OrderCreateCommandHandler(orderDomainService,
                                                                                                      inMemoryOrderRepository,
                                                                                                      orderCommandMapper,
                                                                                                      orderOutboxMapper,
                                                                                                      inMemoryOrderOutboxRepository);
    @AfterEach
    void truncate() {
        inMemoryOrderRepository.truncate();
        inMemoryOrderOutboxRepository.truncate();
    }

    @Test
    void shouldBeAbleToCreateOrder() {
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

    @Test
    void shouldBeAbleToCreateOrderOutboxWhenCreateOrder() {
        //given
        var validCreateOrderCommand = OrderDataProvider.validOrderCommand();

        //when
        var createOrderResponse = orderCreateCommandHandler.createOrder(validCreateOrderCommand);
        var outboxMessage = inMemoryOrderOutboxRepository.findByOrderId(new OrderId(createOrderResponse.orderId())).get();
        //then

        assertThat(outboxMessage)
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("sagaId")
                .hasFieldOrProperty("createdAt")
                .hasFieldOrPropertyWithValue("aggregateId", createOrderResponse.orderId())
                .hasFieldOrPropertyWithValue("outboxStatus", OutboxStatus.STARTED)
                .hasFieldOrPropertyWithValue("sagaStatus", SagaStatus.STARTED)
                .extracting(it -> (OrderEventPayload) it.getPayload())
                .hasFieldOrPropertyWithValue("orderId", createOrderResponse.orderId().toString())
                .hasFieldOrPropertyWithValue("customerId", OrderConstantDataProvider.CUSTOMER_ID.toString())
                .hasFieldOrPropertyWithValue("warehouseId", OrderConstantDataProvider.WAREHOUSE_ID.toString())
                .hasFieldOrPropertyWithValue("price", validCreateOrderCommand.price())
                .hasFieldOrPropertyWithValue("orderStatus", OrderConstantDataProvider.ORDER_STATUS_PENDING.name())
                .hasFieldOrPropertyWithValue("currency", OrderConstantDataProvider.CURRENCY.name())
                .hasFieldOrPropertyWithValue("paymentMethod", OrderConstantDataProvider.PAYMENT_METHOD.name())
                .hasFieldOrPropertyWithValue("deliveryMethod", OrderConstantDataProvider.PACKAGE_BOX_DELIVERY_METHOD.name())
                .hasFieldOrPropertyWithValue("failureMessages", null)
                .extracting(OrderEventPayload::deliveryAddress)
                .satisfies(it -> {
                    assertThat(it)
                            .hasFieldOrPropertyWithValue("street", validCreateOrderCommand.address().street())
                            .hasFieldOrPropertyWithValue("postalCode", validCreateOrderCommand.address().postalCode())
                            .hasFieldOrPropertyWithValue("city", validCreateOrderCommand.address().city());
                });

        assertThat(outboxMessage)
                .extracting(it -> (OrderEventPayload) it.getPayload())
                .extracting(OrderEventPayload::deliveryAddress)
                .hasFieldOrProperty("deliveryAddressId")
                .hasFieldOrPropertyWithValue("street", validCreateOrderCommand.address().street())
                .hasFieldOrPropertyWithValue("postalCode", validCreateOrderCommand.address().postalCode())
                .hasFieldOrPropertyWithValue("city", validCreateOrderCommand.address().city());
    }
}
