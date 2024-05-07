package com.product.ordering.application.ports.output;

import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.entity.Billfold;

public interface BillfoldRepository {

    Billfold save(Billfold billfold);

    Billfold fetchByCustomerId(CustomerId customerId);
}
