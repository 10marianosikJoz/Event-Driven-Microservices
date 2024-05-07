package entity;

import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.domain.PaymentDomainDataProvider;
import com.product.ordering.domain.exception.PaymentDomainException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PaymentCoreLogicTest {

    @Test
    void shouldBeAbleToInitializePayment() {
        //given
        var paymentToInitialize = PaymentDomainDataProvider.payment();

        //when
        paymentToInitialize.initializePayment();

        //then
        assertThat(paymentToInitialize.createdAt()).isNotNull();
    }

    @Test
    void shouldBeAbleToCompletePayment() {
        //given
        var paymentToComplete = PaymentDomainDataProvider.payment();

        //when
        paymentToComplete.completePayment();

        //then
        assertThat(paymentToComplete.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void shouldBeAbleToCancelPayment() {
        //given
        var paymentToCancel = PaymentDomainDataProvider.paymentWithVariousState(PaymentStatus.COMPLETED);

        //when
        paymentToCancel.cancelPayment();

        //then
        assertThat(paymentToCancel.paymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }

    @Test
    void shouldBeAbleToRejectPayment() {
        //given
        var paymentToReject = PaymentDomainDataProvider.paymentToReject();

        //when
        paymentToReject.rejectPayment();

        //then
        assertThat(paymentToReject.paymentStatus()).isEqualTo(PaymentStatus.REJECTED);
    }

    @Test
    void shouldNotBeAbleToCompletePaymentWhenPaymentStatusIsCompleted() {
        //given
        var paymentWitCompletedState = PaymentDomainDataProvider.paymentWithVariousState(PaymentStatus.COMPLETED);

        //except
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(paymentWitCompletedState::completePayment);
    }

    @Test
    void shouldNotBeAbleToCompletePaymentWhenPaymentStatusIsCancelled() {
        //given
        var paymentWitCancelledState = PaymentDomainDataProvider.paymentWithVariousState(PaymentStatus.CANCELLED);

        //except
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(paymentWitCancelledState::completePayment);
    }

    @Test
    void shouldNotBeAbleToCancelPaymentWhenPaymentStatusIsCancelled() {
        //given
        var paymentWithCancelledState = PaymentDomainDataProvider.paymentWithVariousState(PaymentStatus.CANCELLED);

        //except
        assertThatExceptionOfType(PaymentDomainException.class).isThrownBy(paymentWithCancelledState::cancelPayment);
    }

    @Test
    void shouldCorrectlyValidatePriceBeforePayment() {
        //given
        var failureMessages = new ArrayList<String>();
        var paymentWithWrongPrice = PaymentDomainDataProvider.paymentWithWrongPrice();

        //then
        assertThatThrownBy(() -> paymentWithWrongPrice.validatePayment(failureMessages)).message()
                                                      .isEqualTo("Payment price must be not null or greater than zero.");
        assertThat(failureMessages).isNotEmpty();
    }
}
