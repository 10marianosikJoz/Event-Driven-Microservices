package com.product.ordering.domain.entity;

import com.product.ordering.domain.OrderDomainDataProvider;
import com.product.ordering.domain.exception.OrderDomainException;
import com.product.ordering.domain.valueobject.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OrderCoreLogicTest {

    private static final List<String> FAILURE_MESSAGE = List.of("Order mistake");

    @Test
    void shouldPayForTheProductOrder() {
        //given
        var orderWithPendingState = OrderDomainDataProvider.orderForPaying();

        //when
        orderWithPendingState.pay();

        //then
        assertThat(orderWithPendingState.orderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldNotBeAbleToPayWhenOrderIsNotInThePendingState() {
        //given
        var orderWithCancellingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLING);
        var orderWithCancelledState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLED);
        var orderWithPaidState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.PAID);
        var orderWithApprovedState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.APPROVED);

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithCancellingState::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithCancelledState::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithPaidState::pay);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithApprovedState::pay);
    }

    @Test
    void shouldNotBeAbleToPayWhenDeliveryAddressIsNotSelected() {
        //given
        var orderWithoutDeliveryAddress = OrderDomainDataProvider.orderWithoutDeliveryAddress();
        var expectedMessage = "Delivery address is not provided.";

        //when
        var orderException = assertThrows(OrderDomainException.class, orderWithoutDeliveryAddress::pay);

        //then
        assertThat(orderException.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldNotBeAbleToPayWhenDeliveryMethodIsNotSelected() {
        //given
        var orderWithoutDeliveryMethod = OrderDomainDataProvider.orderWithoutDeliveryMethod();
        var expectedMessage = "Delivery method is not selected.";

        //when
        var orderException = assertThrows(OrderDomainException.class, orderWithoutDeliveryMethod::pay);

        //then
        assertThat(orderException.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldNotBeAbleToPayWhenPaymentMethodIsNotSelected() {
        //given
        var orderWithoutPaymentMethod = OrderDomainDataProvider.orderWithoutPaymentMethod();
        var expectedMessage = "Payment method is not selected.";

        //when
        var orderException = assertThrows(OrderDomainException.class, orderWithoutPaymentMethod::pay);

        //then
        assertThat(orderException.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    void shouldAddAdditionalAmountOfMoneyWhenCourierOptionIsSelected() {
        //given
        var orderWithCourierDeliveryMethod = OrderDomainDataProvider.orderWithCourierDeliveryMethod();
        var expectedPrice = new BigDecimal("390.00");

        //when
        orderWithCourierDeliveryMethod.pay();

        //then
        assertThat(orderWithCourierDeliveryMethod.price().amount()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldSubtractCouponValueFromTotalProductPrice() {
        //given
        var orderWithAppliedCoupon = OrderDomainDataProvider.orderWithValidCoupon();
        var expectedPrice = new BigDecimal("370.00");

        //when
        orderWithAppliedCoupon.pay();

        //then
        assertThat(orderWithAppliedCoupon.price().amount()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldBeAbleToApproveOrder() {
        //given
        var orderForApprove = OrderDomainDataProvider.orderForApproving();

        //when
        orderForApprove.approve();

        //then
        assertThat(orderForApprove.orderStatus()).isEqualTo(OrderStatus.APPROVED);
    }

    @Test
    void shouldNotBeAbleToApproveWhenOrderStatusInNotCompleted() {
        //given
        var orderWithCancellingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLING);
        var orderWithCancelledState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLED);
        var orderWithApprovedState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.APPROVED);
        var orderWithPendingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.PENDING);

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithCancellingState::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithCancelledState::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithApprovedState::approve);
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithPendingState::approve);
    }

    @Test
    void shouldBeAbleToInitCancelOrderWhenOrderStatusIsCompleted() {
        //given
        var orderWithApprovedState = OrderDomainDataProvider.orderWithPaidState();

        //when
        orderWithApprovedState.initCancel(OrderCoreLogicTest.FAILURE_MESSAGE);

        //then
        assertThat(orderWithApprovedState.orderStatus()).isEqualTo(OrderStatus.CANCELLING);
        assertThat(orderWithApprovedState.failureMessages()).hasSize(1);
    }

    @Test
    void shouldNotBeAbleToInitCancelOrderWhenOrderStatusIsNotCompleted() {
        //given
        var orderWithCancellingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLING);
        var orderWithCancelledState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLED);
        var orderWithPendingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.PENDING);
        var orderWithApprovedState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.APPROVED);

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithCancellingState.initCancel(FAILURE_MESSAGE));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithCancelledState.initCancel(FAILURE_MESSAGE));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithPendingState.initCancel(FAILURE_MESSAGE));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithApprovedState.initCancel(FAILURE_MESSAGE));
    }

    @Test
    void shouldBeAbleToCancelOrder() {
//        //given
        var orderWithPendingState = OrderDomainDataProvider.orderForCancelling();

        //when
        orderWithPendingState.cancel(FAILURE_MESSAGE);

        //then
        assertThat(orderWithPendingState.orderStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(orderWithPendingState.failureMessages()).hasSize(1);
    }

    @Test
    void shouldNotBeAbleToCancelOrderWhenOrderStatusIsNotCancelling() {
        //given
        var orderWithCancelledState = OrderDomainDataProvider.orderWithCancelledState();
        var orderWithApprovedState = OrderDomainDataProvider.orderWithApprovedState();
        var orderWithCompletedState = OrderDomainDataProvider.orderWithPaidState();

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithCancelledState.cancel(FAILURE_MESSAGE));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithApprovedState.cancel(FAILURE_MESSAGE));
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(() -> orderWithCompletedState.cancel(FAILURE_MESSAGE));
    }

    @Test
    void shouldBeAbleToInitializeOrder() {
        //given
        var orderToInitialize = OrderDomainDataProvider.orderToInitialize();

        //when

        orderToInitialize.initializeOrder();

        //then
        assertThat(orderToInitialize.id()).isNotNull();
        assertThat(orderToInitialize.orderStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldPreventInitializeOrderWhenOrderStatusIsProvided() {
        //given
        var orderWithCancellingState = OrderDomainDataProvider.orderWithAppropriateState(OrderStatus.CANCELLING);

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithCancellingState::validateOrders);
    }

    @Test
    void shouldPreventInitializeOrderWhenOrderPriceIsNotGreaterThanZero() {
        //given
        var orderWithIncorrectPrice = OrderDomainDataProvider.orderWithIncorrectPrice();

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithIncorrectPrice::validateOrders);
    }

    @Test
    void shouldPreventInitializeOrderWhenOrderTotalPriceIsNotEqualToAggregatedProductPrices() {
        //given
        var orderWithIncorrectTotalPrice = OrderDomainDataProvider.orderWithIncorrectTotalPrice();

        //except
        assertThatExceptionOfType(OrderDomainException.class).isThrownBy(orderWithIncorrectTotalPrice::validateOrders);
    }
}