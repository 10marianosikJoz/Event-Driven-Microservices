package com.product.ordering.domain.entity;

import java.util.Objects;

public abstract class DomainEntity<ID> {

    private ID id;

    public ID id() {
        return id;
    }

    public void id(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (DomainEntity<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
