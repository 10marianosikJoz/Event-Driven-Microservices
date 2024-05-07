package com.product.ordering.adapters.repository;

import com.product.ordering.domain.entity.BillfoldHistory;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.entities.entity.BillfoldHistoryEntity;
import com.product.ordering.application.ports.output.BillfoldHistoryRepository;
import com.product.ordering.entities.mapper.BillfoldHistoryEntityCommandMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class SqlBillfoldHistoryRepository implements BillfoldHistoryRepository {

    private final BillfoldHistoryJpaRepository billfoldHistoryJpaRepository;
    private final BillfoldHistoryEntityCommandMapper billfoldHistoryEntityCommandMapper;

    SqlBillfoldHistoryRepository(BillfoldHistoryJpaRepository billfoldHistoryJpaRepository,
                                 BillfoldHistoryEntityCommandMapper billfoldHistoryEntityCommandMapper) {

        this.billfoldHistoryJpaRepository = billfoldHistoryJpaRepository;
        this.billfoldHistoryEntityCommandMapper = billfoldHistoryEntityCommandMapper;
    }

    @Override
    public BillfoldHistory save(BillfoldHistory billfoldHistory) {
        return billfoldHistoryEntityCommandMapper.mapBillfoldHistoryJpaEntityToDomainBillfoldHistoryObject(
                billfoldHistoryJpaRepository.save(billfoldHistoryEntityCommandMapper.mapDomainBillfoldHistoryObjectToBillfoldHistoryJpaEntity(
                        billfoldHistory)));
    }

    @Override
    public List<BillfoldHistory> fetchByCustomerId(CustomerId customerId) {
        var billfoldHistory = billfoldHistoryJpaRepository.findByCustomerId(customerId.value());

        return billfoldHistory.stream()
                              .map(billfoldHistoryEntityCommandMapper::mapBillfoldHistoryJpaEntityToDomainBillfoldHistoryObject)
                              .collect(Collectors.toList());

    }
}

@Repository
interface BillfoldHistoryJpaRepository extends CrudRepository<BillfoldHistoryEntity, UUID> {

    List<BillfoldHistoryEntity> findByCustomerId(UUID customerId);
}
