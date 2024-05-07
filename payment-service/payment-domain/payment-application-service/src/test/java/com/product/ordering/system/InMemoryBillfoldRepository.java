package com.product.ordering.system;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.application.exception.BillfoldNotFoundException;
import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.valueobject.BillfoldId;
import com.product.ordering.application.ports.output.BillfoldRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryBillfoldRepository implements BillfoldRepository {

    private final Map<BillfoldId, Billfold> database = new ConcurrentHashMap<>();

    void truncate() {
        database.clear();
    }

    @Override
    public Billfold save(Billfold billfold) {
        database.put(billfold.id(), billfold);
        return billfold;
    }

    @Override
    public Billfold fetchByCustomerId(CustomerId customerId) {
        return database.values().stream()
                                .filter(it -> customerId.equals(it.customerId()))
                                .findFirst()
                                .orElseThrow(() -> new BillfoldNotFoundException("Billfold with customerId: " + customerId.value() + " not found"));
    }
}
