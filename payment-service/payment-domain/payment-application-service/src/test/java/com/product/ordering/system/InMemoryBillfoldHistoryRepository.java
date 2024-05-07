package com.product.ordering.system;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.valueobject.BillfoldHistoryId;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryBillfoldHistoryRepository implements BillfoldHistoryRepository {

    private final Map<BillfoldHistoryId, BillfoldHistory> database = new ConcurrentHashMap<>();

    void truncate() {
        database.clear();
    }

    @Override
    public BillfoldHistory save(BillfoldHistory billfoldHistory) {
        database.put(billfoldHistory.id(), billfoldHistory);
        return billfoldHistory;
    }

    @Override
    public List<BillfoldHistory> fetchByCustomerId(CustomerId customerId) {
        return new ArrayList<>(database.values());
    }
}
