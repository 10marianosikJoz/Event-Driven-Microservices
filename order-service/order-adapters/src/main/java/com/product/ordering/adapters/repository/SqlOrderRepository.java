package com.product.ordering.adapters.repository;

import com.product.ordering.application.ports.output.repository.OrderRepository;
import com.product.ordering.domain.entity.Order;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.entities.entity.OrderEntity;
import com.product.ordering.entities.mapper.OrderEntityCommandMapper;
import com.product.ordering.domain.valueobject.OrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class SqlOrderRepository implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderEntityCommandMapper orderEntityCommandMapper;

    SqlOrderRepository(final OrderJpaRepository orderJpaRepository,
                       final OrderEntityCommandMapper orderEntityCommandMapper) {

        this.orderJpaRepository = orderJpaRepository;
        this.orderEntityCommandMapper = orderEntityCommandMapper;
    }


    @Override
    public Order save(Order order) {
        return orderEntityCommandMapper.mapOrderJpaEntityToOrderDomainObject(
                orderJpaRepository.save(orderEntityCommandMapper.mapDomainOrderObjectToOrderJpaEntity(order)));
    }

    @Override
    public Order fetchOrder(OrderId orderId) {
        return orderJpaRepository.findById(orderId.value())
                                 .map(orderEntityCommandMapper::mapOrderJpaEntityToOrderDomainObject)
                                 .orElseThrow(() -> new OrderNotFoundException("Order with id: " + orderId + " is not present"));
    }
}

@Repository
interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {}

