package com.product.ordering.domain.entity;

import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderItemId;
import com.product.ordering.domain.valueobject.ProductId;
import com.product.ordering.domain.valueobject.Quantity;

import java.util.UUID;

public class OrderItem extends DomainEntity<OrderItemId> {

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    private final ProductId productId;
    private final Money price;
    private final Quantity quantity;

    private OrderItem(OrderItemBuilder builder) {

        id(builder.orderItemId);
        productId = builder.productId;
        price = builder.price;
        quantity = builder.quantity;
    }

    public void initialize() {
        id(new OrderItemId(UUID.randomUUID()));
    }

    public ProductId productId() {
        return productId;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public static class OrderItemBuilder {

        private OrderItemId orderItemId;
        private ProductId productId;
        private Money price;
        private Quantity quantity;

        private OrderItemBuilder() {}

        public OrderItemBuilder orderItemId(OrderItemId orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public OrderItemBuilder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public OrderItemBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public OrderItemBuilder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
