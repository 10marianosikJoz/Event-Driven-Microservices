package com.product.ordering.application.ports.output;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.entity.BillfoldHistory;

import java.util.List;

public interface BillfoldHistoryRepository {

    BillfoldHistory save(BillfoldHistory billfoldHistory);

    List<BillfoldHistory> fetchByCustomerId(CustomerId customerId);
}
