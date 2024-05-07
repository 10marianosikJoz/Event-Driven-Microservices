package com.product.ordering.domain.valueobject;

import java.util.UUID;

public class OrderProcessedId extends BaseId<UUID> {

    public OrderProcessedId(UUID value) {
        super(value);
    }
}
