package com.product.ordering.domain.valueobject;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.DomainException;

import java.util.Objects;
import java.util.UUID;

public record DeliveryAddress(UUID id,
                              String street,
                              String postalCode,
                              String city) {

    public static DeliveryAddressBuilder builder() {
        return new DeliveryAddressBuilder();
    }

    public DeliveryAddress {
        if (street.isBlank() || postalCode.isBlank() || city.isBlank()) {
            throw new DomainException(DomainConstants.ORDER_DELIVERY_ADDRESS_MISSED);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (DeliveryAddress) o;
        return Objects.equals(street, that.street)
                && Objects.equals(postalCode, that.postalCode)
                && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, postalCode, city);
    }

    public static class DeliveryAddressBuilder {

        private DeliveryAddressBuilder() {}

        private UUID id;
        private String street;
        private String postalCode;
        private String city;

        public DeliveryAddressBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DeliveryAddressBuilder street(String street) {
            this.street = street;
            return this;
        }

        public DeliveryAddressBuilder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public DeliveryAddressBuilder city(String city) {
            this.city = city;
            return this;
        }

        public DeliveryAddress build() {
            return new DeliveryAddress(id,
                                       street,
                                       postalCode,
                                       city);
        }
    }
}
