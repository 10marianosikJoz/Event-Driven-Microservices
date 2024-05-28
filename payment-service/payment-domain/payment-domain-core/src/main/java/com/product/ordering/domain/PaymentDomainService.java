package com.product.ordering.domain;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.event.publisher.DomainEventPublisher;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.event.PaymentCancelledEvent;
import com.product.ordering.domain.event.PaymentCompletedEvent;
import com.product.ordering.domain.event.PaymentEvent;
import com.product.ordering.domain.event.PaymentRejectedEvent;
import com.product.ordering.domain.exception.PaymentDomainException;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.TransactionType;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PaymentDomainService {

    public PaymentEvent completePayment(Payment payment,
                                        Billfold billfold,
                                        List<String> failureMessages,
                                        List<BillfoldHistory> virtualBillfoldHistories) {
        try {
            payment.validatePayment(failureMessages);
            payment.initializePayment();
            validateVirtualBillfoldTotalAmount(payment, billfold, failureMessages);
            billfold.subtractMoneyFromBillfold(payment.price());
            updateVirtualWalletHistory(payment, virtualBillfoldHistories, TransactionType.VIRTUAL_BILLFOLD);
            payment.completePayment();

            return new PaymentCompletedEvent(payment,
                                             Instant.now());

        } catch (PaymentDomainException e) {
            payment.rejectPayment();

            return new PaymentRejectedEvent(payment,
                                            Instant.now(),
                                            failureMessages);
        }
    }

    private void validateVirtualBillfoldTotalAmount(Payment payment,
                                                    Billfold billfold,
                                                    List<String> failureMessages) {

        if (payment.price().isGreaterThan(billfold.totalBillfoldAmount())) {
            failureMessages.add("Not enough money in the billfold.");
            throw new PaymentDomainException(DomainConstants.PAYMENT_NOT_ENOUGH_MONEY_IN_THE_BILLFOLD);
        }
    }

    public PaymentEvent cancelPayment(Payment payment,
                                      Billfold billfold,
                                      List<String> failureMessages,
                                      List<BillfoldHistory> virtualBillfoldHistories) {

        try {
            payment.validatePayment(failureMessages);
            payment.cancelPayment();
            billfold.addMoneyToBillfold(payment.price());
            updateVirtualWalletHistory(payment, virtualBillfoldHistories, TransactionType.VIRTUAL_BILLFOLD);

            return new PaymentCancelledEvent(payment,
                                             Instant.now());

        } catch (PaymentDomainException e) {
            payment.rejectPayment();

            return new PaymentRejectedEvent(payment,
                                            Instant.now(),
                                            failureMessages);
        }
    }

    private void updateVirtualWalletHistory(Payment payment,
                                            List<BillfoldHistory> virtualBillfoldHistories,
                                            TransactionType transactionType) {

        virtualBillfoldHistories.add(BillfoldHistory.builder()
                .billfoldId(new BillfoldHistoryId(UUID.randomUUID()))
                .customerId(new CustomerId(payment.customerId().value()))
                .amount(payment.price())
                .transactionType(transactionType)
                .build());
    }
}
