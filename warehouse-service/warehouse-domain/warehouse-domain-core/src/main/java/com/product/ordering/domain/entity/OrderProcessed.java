package com.product.ordering.domain.entity;

import com.product.ordering.domain.exception.WarehouseDomainException;
import com.product.ordering.domain.valueobject.*;

import java.util.List;

public class OrderProcessed extends AggregateRoot<OrderProcessedId> {

    public static OrderProcessedBuilder builder() {
        return new OrderProcessedBuilder();
    }

    private final WarehouseId warehouseId;
    private final Money price;
    private final List<OrderItem> orderItems;

    private OrderApprovalStatus orderApprovalStatus;

    private OrderProcessed(OrderProcessedBuilder builder) {

        id(builder.orderProcessedId);
        warehouseId = builder.warehouseId;
        price = builder.price;
        orderItems = builder.orderItems;
        orderApprovalStatus = builder.orderApprovalStatus;
    }

    public void initialize() {
        orderItems.forEach(OrderItem::initialize);
    }

    public void validateTotalAmount() {
        var totalProductAmount = orderItems.stream()
                                            .map(it -> it.price().multiply(it.quantity().amount()))
                                            .reduce(Money.ZERO, Money::add);

        if (!totalProductAmount.equals(price)) {
            throw new WarehouseDomainException("Order amount: " + price.amount()
                    + " is different than total products price : " + totalProductAmount.amount());
        }
    }

    public void accept() {
        orderApprovalStatus = OrderApprovalStatus.APPROVED;
    }

    public void reject() {
        orderApprovalStatus = OrderApprovalStatus.REJECTED;
    }

    public WarehouseId warehouseId() {
        return warehouseId;
    }

    public Money price() {
        return price;
    }

    public OrderApprovalStatus orderApprovalStatus() {
        return orderApprovalStatus;
    }

    public List<OrderItem> orderItems() {
        return orderItems;
    }

    public static final class OrderProcessedBuilder {

        private OrderProcessedId orderProcessedId;
        private WarehouseId warehouseId;
        private Money price;
        private List<OrderItem> orderItems;
        private OrderApprovalStatus orderApprovalStatus;

        private OrderProcessedBuilder() {}

        public OrderProcessedBuilder orderProcessedId(OrderProcessedId orderProcessedId) {
            this.orderProcessedId = orderProcessedId;
            return this;
        }

        public OrderProcessedBuilder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public OrderProcessedBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public OrderProcessedBuilder orderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public OrderProcessedBuilder orderApprovalStatus(OrderApprovalStatus orderApprovalStatus) {
            this.orderApprovalStatus = orderApprovalStatus;
            return this;
        }

        public OrderProcessed build() {
            return new OrderProcessed(this);
        }
    }
}
