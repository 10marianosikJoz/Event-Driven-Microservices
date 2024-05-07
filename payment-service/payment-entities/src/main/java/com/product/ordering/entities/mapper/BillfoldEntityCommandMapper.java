package com.product.ordering.entities.mapper;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.entities.entity.BillfoldEntity;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.valueobject.BillfoldId;
import org.springframework.stereotype.Component;

@Component
public class BillfoldEntityCommandMapper {

    public BillfoldEntity mapDomainBillfoldObjectToBillfoldJpaEntity(Billfold billfold) {
        return BillfoldEntity.builder()
                .id(billfold.id().value())
                .customerId(billfold.customerId().value())
                .totalBillfoldAmount(billfold.totalBillfoldAmount().amount())
                .build();
    }

    public Billfold mapBillfoldJpaEntityToDomainBillfoldObject(BillfoldEntity billfold) {
        return Billfold.builder()
                .billfoldId(new BillfoldId(billfold.getId()))
                .customerId(new CustomerId(billfold.getCustomerId()))
                .totalBillfoldAmount(new Money(billfold.getTotalBillfoldAmount()))
                .build();
    }
}
