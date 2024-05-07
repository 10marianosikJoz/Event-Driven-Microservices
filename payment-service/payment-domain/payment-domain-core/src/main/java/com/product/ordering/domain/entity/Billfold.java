package com.product.ordering.domain.entity;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.BillfoldId;

public class Billfold extends DomainEntity<BillfoldId> {

    public static BillfoldBuilder builder() {
        return new BillfoldBuilder();
    }

    private final CustomerId customerId;

    private Money totalBillfoldAmount;

    private Billfold(BillfoldBuilder billfoldBuilder) {

        id(billfoldBuilder.billfoldId);
        this.customerId = billfoldBuilder.customerId;
        this.totalBillfoldAmount = billfoldBuilder.totalBillfoldAmount;
    }

    public void subtractMoneyFromBillfold(Money subtractedAmount) {
        this.totalBillfoldAmount = this.totalBillfoldAmount.subtract(subtractedAmount);
    }

    public void addMoneyToBillfold(Money addedAmount) {
        this.totalBillfoldAmount = this.totalBillfoldAmount.add(addedAmount);
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalBillfoldAmount() {
        return totalBillfoldAmount;
    }

    public static final class BillfoldBuilder {

        private BillfoldId billfoldId;
        private CustomerId customerId;
        private Money totalBillfoldAmount;

        private BillfoldBuilder() {}

        public BillfoldBuilder billfoldId(BillfoldId billfoldId) {
            this.billfoldId = billfoldId;
            return this;
        }

        public BillfoldBuilder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public BillfoldBuilder totalBillfoldAmount(Money totalBillfoldAmount) {
            this.totalBillfoldAmount = totalBillfoldAmount;
            return this;
        }

        public Billfold build() {
            return new Billfold(this);
        }
    }
}
