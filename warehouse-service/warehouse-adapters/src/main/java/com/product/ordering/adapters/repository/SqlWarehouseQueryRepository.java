package com.product.ordering.adapters.repository;

import com.product.ordering.application.ports.output.repository.WarehouseRepository;
import com.product.ordering.domain.entity.Warehouse;
import com.product.ordering.domain.exception.WarehouseNotFoundException;
import com.product.ordering.domain.valueobject.WarehouseId;
import com.product.ordering.entities.entity.WarehouseEntity;
import com.product.ordering.entities.mapper.WarehouseEntityQueryMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class SqlWarehouseQueryRepository implements WarehouseRepository {

    private final WarehouseJpaRepository warehouseJpaRepository;
    private final WarehouseEntityQueryMapper warehouseEntityQueryMapper;

   SqlWarehouseQueryRepository(final WarehouseJpaRepository warehouseJpaRepository,
                               final WarehouseEntityQueryMapper warehouseEntityQueryMapper) {

        this.warehouseJpaRepository = warehouseJpaRepository;
        this.warehouseEntityQueryMapper = warehouseEntityQueryMapper;
    }

    @Override
    public Optional<Warehouse> findById(WarehouseId warehouseId) {
        return Optional.ofNullable(warehouseEntityQueryMapper.mapWarehouseJpaEntityToWarehouseDomainObject(
                warehouseJpaRepository.findById(warehouseId.value())
                                      .orElseThrow(() -> new WarehouseNotFoundException("Warehouse with id : " + warehouseId + " is not present"))
        ));
    }
}

interface WarehouseJpaRepository extends JpaRepository<WarehouseEntity, UUID> {}
