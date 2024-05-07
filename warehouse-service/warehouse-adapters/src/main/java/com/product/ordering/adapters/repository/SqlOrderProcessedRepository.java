package com.product.ordering.adapters.repository;

import com.product.ordering.entities.entity.OrderProcessedEntity;
import com.product.ordering.entities.mapper.OrderProcessedEntityCommandMapper;
import com.product.ordering.domain.entity.OrderProcessed;
import com.product.ordering.application.ports.output.repository.OrderProcessedRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
class SqlOrderProcessedRepository implements OrderProcessedRepository {

    private final OrderProcessedJpaRepository orderProcessedJpaRepository;
    private final OrderProcessedEntityCommandMapper orderProcessedEntityCommandMapper;

    SqlOrderProcessedRepository(final OrderProcessedJpaRepository orderProcessedJpaRepository,
                                final OrderProcessedEntityCommandMapper orderProcessedEntityCommandMapper) {

        this.orderProcessedJpaRepository = orderProcessedJpaRepository;
        this.orderProcessedEntityCommandMapper = orderProcessedEntityCommandMapper;
    }

    @Override
    public OrderProcessed save(OrderProcessed orderProcessed) {
        return orderProcessedEntityCommandMapper
                .mapOrderProcessedJpaEntityToOrderProcessedDomainObject(orderProcessedJpaRepository
                        .save(orderProcessedEntityCommandMapper.
                                mapOrderProcessedDomainObjectToOrderProcessedJpaEntity(orderProcessed)));
    }
}

@Repository
interface OrderProcessedJpaRepository extends CrudRepository<OrderProcessedEntity, UUID> {}
