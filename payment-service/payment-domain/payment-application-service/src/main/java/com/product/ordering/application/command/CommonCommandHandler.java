package com.product.ordering.application.command;

import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.entity.Payment;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.application.ports.output.BillfoldRepository;
import com.product.ordering.application.ports.output.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonCommandHandler {

    private final PaymentRepository paymentRepository;
    private final BillfoldRepository billfoldRepository;
    private final BillfoldHistoryRepository billfoldHistoryRepository;

    public CommonCommandHandler(final PaymentRepository paymentRepository,
                                final BillfoldRepository billfoldRepository,
                                final BillfoldHistoryRepository billfoldHistoryRepository) {

        this.paymentRepository = paymentRepository;
        this.billfoldRepository = billfoldRepository;
        this.billfoldHistoryRepository = billfoldHistoryRepository;
    }

    void persistToDatabase(Payment payment,
                                   Billfold billfold,
                                   List<BillfoldHistory> billfoldHistories,
                                   List<String> failureMessages) {

        paymentRepository.save(payment);

        if (failureMessages.isEmpty()) {
            billfoldRepository.save(billfold);
            billfoldHistoryRepository.save(billfoldHistories.get(billfoldHistories.size() - 1));
        }
    }
}
