package com.product.ordering.domain;

import com.product.ordering.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentDomainServiceTest {

    private final PaymentDomainService paymentDomainService = new PaymentDomainService();

    @Test
    void shouldBeAbleToCompletePaymentProcess() {
        //given
        var payment = PaymentDomainDataProvider.payment();
        var billfold = PaymentDomainDataProvider.billfold();
        var failureMessages = List.of("");
        var billfoldHistory = PaymentDomainDataProvider.billfoldHistory();

        //when
        var paymentEvent = paymentDomainService.completePayment(payment,
                                                                billfold,
                                                                failureMessages,
                                                                billfoldHistory);

        //then
        assertThat(paymentEvent.createdAt()).isNotNull();
        assertThat(payment.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void shouldBeAbleToCancelPaymentProcess() {
        //given
        var payment = PaymentDomainDataProvider.payment();
        var billfold = PaymentDomainDataProvider.billfold();
        var failureMessages = List.of("");
        var billfoldHistory = PaymentDomainDataProvider.billfoldHistory();

        //when
        var paymentEvent = paymentDomainService.cancelPayment(payment,
                                                              billfold,
                                                              failureMessages,
                                                              billfoldHistory);

        //then
        assertThat(paymentEvent.createdAt()).isNotNull();
        assertThat(payment.paymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
}
