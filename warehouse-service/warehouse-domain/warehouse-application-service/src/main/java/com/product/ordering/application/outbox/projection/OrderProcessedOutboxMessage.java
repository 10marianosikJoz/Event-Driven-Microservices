package com.product.ordering.application.outbox.projection;

import com.product.ordering.domain.valueobject.OrderApprovalStatus;
import com.product.ordering.system.outbox.model.OutboxMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProcessedOutboxMessage extends OutboxMessage {

    private OrderApprovalStatus orderApprovalStatus;
}
