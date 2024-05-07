package com.product.ordering.domain.entity;


import com.product.ordering.domain.valueobject.Description;
import com.product.ordering.domain.valueobject.Review;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.ProductId;

import java.util.List;

public class Product extends DomainEntity<ProductId> {

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }

    private boolean isAvailable;
    private String name;
    private Money price;
    private Description description;
    private List<Review> reviews;

    Product(ProductBuilder productBuilder) {

        super.id(productBuilder.productId);
        this.isAvailable = productBuilder.isAvailable;
        this.name = productBuilder.name;
        this.price = productBuilder.price;
        this.description = productBuilder.description;
        this.reviews = productBuilder.reviews;
    }

    public Product(ProductId productId) {
        super.id(productId);
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public String name() {
        return name;
    }

    Description description() {
        return description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public Money price() {
        return price;
    }

    List<Review> reviews() {
        return reviews;
    }

    public static final class ProductBuilder {

        private ProductId productId;
        private boolean isAvailable;
        private String name;
        private Money price;
        private Description description;
        private List<Review> reviews;

        private ProductBuilder() {}

        public ProductBuilder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public ProductBuilder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public ProductBuilder description(Description description) {
            this.description = description;
            return this;
        }

        public ProductBuilder reviews(List<Review> reviews) {
            this.reviews = List.copyOf(reviews);
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
