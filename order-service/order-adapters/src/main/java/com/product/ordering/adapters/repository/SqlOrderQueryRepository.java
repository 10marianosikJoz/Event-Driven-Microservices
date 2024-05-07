package com.product.ordering.adapters.repository;

import com.product.ordering.application.ports.output.repository.OrderQueryRepository;
import com.product.ordering.application.query.OrderProjectionView;
import com.product.ordering.domain.exception.OrderNotFoundException;
import com.product.ordering.entities.entity.OrderEntity;
import com.product.ordering.entities.mapper.OrderEntityQueryMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class SqlOrderQueryRepository implements OrderQueryRepository {

    private final OrderEntityQueryMapper orderEntityQueryMapper;
    private final OrderQueryJpaRepository orderQueryJpaRepository;

    SqlOrderQueryRepository(final OrderEntityQueryMapper orderEntityQueryMapper,
                            final OrderQueryJpaRepository orderQueryJpaRepository) {

        this.orderEntityQueryMapper = orderEntityQueryMapper;
        this.orderQueryJpaRepository = orderQueryJpaRepository;
    }

    @Override
    public OrderProjectionView fetchOrderProjectionView(UUID orderId) {
        return orderQueryJpaRepository.findById(orderId)
                                      .map(orderEntityQueryMapper::mapOrderEntityToOrderProjectionView)
                                      .orElseThrow(() -> new OrderNotFoundException("Order with id: " + orderId + " is not present"));

    }
}
    @Repository
    interface OrderQueryJpaRepository extends JpaRepository<OrderEntity, UUID> {}
