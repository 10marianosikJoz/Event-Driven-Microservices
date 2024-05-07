package com.product.ordering.application.query;

import com.product.ordering.domain.valueobject.TransactionType;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record BillfoldHistoryProjectionView(UUID billfoldHistoryId,
                                            UUID customerId,
                                            BigDecimal amount,
                                            TransactionType transactionType) {}
