package com.product.ordering.entities.mapper;

import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.TransactionType;
import com.product.ordering.entities.entity.BillfoldHistoryEntity;
import org.springframework.stereotype.Component;

@Component
public class BillfoldHistoryEntityCommandMapper {

    public BillfoldHistory mapBillfoldHistoryJpaEntityToDomainBillfoldHistoryObject(BillfoldHistoryEntity billfoldHistoryEntity) {
        return BillfoldHistory.builder()
                .billfoldId(new BillfoldHistoryId(billfoldHistoryEntity.getCustomerId()))
                .customerId(new CustomerId(billfoldHistoryEntity.getCustomerId()))
                .amount(new Money(billfoldHistoryEntity.getAmount()))
                .transactionType(billfoldHistoryEntity.getTransactionType())
                .build();
    }

    public BillfoldHistoryEntity mapDomainBillfoldHistoryObjectToBillfoldHistoryJpaEntity(BillfoldHistory billfoldHistory) {
        return BillfoldHistoryEntity.builder()
                .id(billfoldHistory.id().value())
                .customerId(billfoldHistory.customerId().value())
                .amount(billfoldHistory.amount().amount())
                .transactionType(TransactionType.valueOf(billfoldHistory.transactionType().name()))
                .build();
    }
}
