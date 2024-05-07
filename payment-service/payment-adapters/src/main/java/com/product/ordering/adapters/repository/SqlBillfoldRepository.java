package com.product.ordering.adapters.repository;

import com.product.ordering.domain.entity.Billfold;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.entities.entity.BillfoldEntity;
import com.product.ordering.entities.mapper.BillfoldEntityCommandMapper;
import com.product.ordering.application.exception.BillfoldNotFoundException;
import com.product.ordering.application.ports.output.BillfoldRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class SqlBillfoldRepository implements BillfoldRepository {

    private final BillfoldJpaRepository billfoldJpaRepository;
    private final BillfoldEntityCommandMapper billfoldEntityCommandMapper;

    SqlBillfoldRepository(BillfoldJpaRepository billfoldJpaRepository,
                                 BillfoldEntityCommandMapper billfoldEntityCommandMapper) {

        this.billfoldJpaRepository = billfoldJpaRepository;
        this.billfoldEntityCommandMapper = billfoldEntityCommandMapper;
    }

    @Override
    public Billfold save(Billfold billfold) {
        return billfoldEntityCommandMapper.mapBillfoldJpaEntityToDomainBillfoldObject(
                billfoldJpaRepository.save(billfoldEntityCommandMapper.mapDomainBillfoldObjectToBillfoldJpaEntity(billfold)));
    }

    @Override
    public Billfold fetchByCustomerId(CustomerId customerId) {
        return billfoldJpaRepository.findByCustomerId(customerId.value())
                .map(billfoldEntityCommandMapper::mapBillfoldJpaEntityToDomainBillfoldObject)
                .orElseThrow(() -> new BillfoldNotFoundException("Billfold with customerId: " + customerId.value() + " is not present"));
    }
}

@Repository
interface BillfoldJpaRepository extends CrudRepository<BillfoldEntity, UUID> {

    Optional<BillfoldEntity> findByCustomerId(UUID customerId);
}
