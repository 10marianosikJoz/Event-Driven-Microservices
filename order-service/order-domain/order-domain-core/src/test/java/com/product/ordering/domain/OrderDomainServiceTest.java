package com.product.ordering.domain;

import com.product.ordering.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderDomainServiceTest {

    private final OrderDomainService orderDomainService = new OrderDomainService();

    @Test
    void shouldCreateOrder() {
        //given
        var orderWithCompletedState = OrderDomainDataProvider.orderWithoutOrderStatus();

        //when
        var orderCreatedEvent = orderDomainService.createOrder(orderWithCompletedState);

        //then
        assertThat(orderWithCompletedState.id()).isNotNull();
        assertThat(orderCreatedEvent.createdAt()).isNotNull();
        assertThat(orderWithCompletedState.orderItems()).hasSize(2);
        assertThat(orderWithCompletedState.orderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldNotBeAbleToCreateOrderWhenTotalPriceIsLessThanZero() {
        //given
        var orderWithIncorrectPrice = OrderDomainDataProvider.orderWithIncorrectPrice();
        var expectedMessage = "Total price must be greater than zero";

        //then
        assertThatThrownBy(() -> orderDomainService.createOrder(orderWithIncorrectPrice))
                                                    .hasMessageContaining(expectedMessage);
    }

    @Test
    void shouldPayForTheProductOrder() {
        //given
        var orderWithPendingState = OrderDomainDataProvider.orderForPaying();

        //when
        orderDomainService.payOrder(orderWithPendingState);

        //then
        assertThat(orderWithPendingState.orderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldApproveProductOrder() {
        //given
        var orderWithCompletedState = OrderDomainDataProvider.orderForApproving();

        //when
        orderDomainService.approveOrder(orderWithCompletedState);

        //then
        assertThat(orderWithCompletedState.orderStatus()).isEqualTo(OrderStatus.APPROVED);
    }

    @Test
    void shouldInitializeCancellingOrderProcess() {
        //given
        var orderWithCompletedState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.PAID);
        var expectedMessage = "Warehouse is closed";

        //when
        var orderCancelledEvent = orderDomainService.initializeCancelling(orderWithCompletedState,
                                                                          OrderConstantDataProvider.FAILURE_MESSAGE);
        //then
        assertThat(orderCancelledEvent.createdAt()).isNotNull();
        assertThat(orderWithCompletedState.orderStatus()).isEqualTo(OrderStatus.CANCELLING);
        assertThat(orderWithCompletedState.failureMessages().get(0)).isEqualTo(expectedMessage);
    }

    @Test
    void shouldCancelProductOrder() {
        //given
        var orderWithPendingState = OrderDomainDataProvider.orderForCancelling();

        //when
        orderDomainService.cancelOrder(orderWithPendingState, OrderConstantDataProvider.FAILURE_MESSAGE);

        //then
        assertThat(orderWithPendingState.orderStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}