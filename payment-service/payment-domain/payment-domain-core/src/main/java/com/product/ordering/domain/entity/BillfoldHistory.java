package com.product.ordering.domain.entity;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.TransactionType;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;

public class BillfoldHistory extends DomainEntity<BillfoldHistoryId> {

    public static BillfoldHistoryBuilder builder() {
        return new BillfoldHistoryBuilder();
    }

    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    private BillfoldHistory(BillfoldHistoryBuilder billfoldHistoryBuilder) {

        id(billfoldHistoryBuilder.billfoldHistoryId);
        this.customerId = billfoldHistoryBuilder.customerId;
        this.amount = billfoldHistoryBuilder.amount;
        this.transactionType = billfoldHistoryBuilder.transactionType;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money amount() {
        return amount;
    }

    public TransactionType transactionType() {
        return transactionType;
    }

    public static final class BillfoldHistoryBuilder {

        private BillfoldHistoryId billfoldHistoryId;
        private CustomerId customerId;
        private Money amount;
        private TransactionType transactionType;

        private BillfoldHistoryBuilder() {}

        public BillfoldHistoryBuilder billfoldId(BillfoldHistoryId billfoldHistoryId) {
            this.billfoldHistoryId = billfoldHistoryId;
            return this;
        }

        public BillfoldHistoryBuilder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public BillfoldHistoryBuilder amount(Money amount) {
            this.amount = amount;
            return this;
        }

        public BillfoldHistoryBuilder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public BillfoldHistory build() {
            return new BillfoldHistory(this);
        }
    }
}
