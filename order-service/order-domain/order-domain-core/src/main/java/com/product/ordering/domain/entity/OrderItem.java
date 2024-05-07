package com.product.ordering.domain.entity;

import com.product.ordering.domain.valueobject.OrderItemId;
import com.product.ordering.domain.valueobject.Quantity;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;

public class OrderItem extends DomainEntity<OrderItemId> {

    public static OrderItemBuilder builder() {
        return new OrderItemBuilder();
    }

    private final Quantity quantity;
    private final Product product;
    private final Money price;
    private final Money subtotal;

    private OrderId orderId;

    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.id(orderItemId);
    }

    boolean isPriceValid() {
        return  price.isGreaterThanZero() &&
                price.equals(product.price())
                && price.multiply(quantity.quantity()).equals(subtotal);
    }

    private OrderItem(OrderItemBuilder orderItemBuilder) {

        super.id(orderItemBuilder.orderItemId);
        this.orderId = orderItemBuilder.orderId;
        this.product = orderItemBuilder.product;
        this.quantity = orderItemBuilder.quantity;
        this.price = orderItemBuilder.price;
        this.subtotal = orderItemBuilder.subtotal;
    }

    public OrderId orderId() {
        return orderId;
    }

    public Money subtotal() {
        return subtotal;
    }

    public Product product() {
        return product;
    }

    public Money price() {
        return price;
    }

    public Quantity quantity() {
        return quantity;
    }

    public static final class OrderItemBuilder {

        private OrderId orderId;
        private OrderItemId orderItemId;
        private Product product;
        private Quantity quantity;
        private Money price;
        private Money subtotal;

        private OrderItemBuilder() {}

        public OrderItemBuilder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderItemBuilder orderItemId(OrderItemId orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public OrderItemBuilder product(Product product) {
            this.product = product;
            return this;
        }

        public OrderItemBuilder quantity(Quantity quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public OrderItemBuilder subTotal(Money subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
