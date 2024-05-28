package com.product.ordering.adapters.repository;

import com.product.ordering.application.outbox.repository.OrderOutboxRepository;
import com.product.ordering.entities.entity.outbox.OrderOutboxEntity;
import com.product.ordering.entities.entity.outbox.OrderOutboxEntityMapper;
import com.product.ordering.system.outbox.model.OutboxMessage;
import com.product.ordering.system.outbox.model.OutboxStatus;
import com.product.ordering.system.saga.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class SqlOrderOutboxRepository implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxEntityMapper orderOutboxEntityMapper;

    SqlOrderOutboxRepository(final OrderOutboxJpaRepository orderOutboxJpaRepository,
                             final OrderOutboxEntityMapper orderOutboxEntityMapper) {

        this.orderOutboxJpaRepository = orderOutboxJpaRepository;
        this.orderOutboxEntityMapper = orderOutboxEntityMapper;
    }

    @Override
    public void deleteByOutboxStatusAndSagaStatus(OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        orderOutboxJpaRepository.deleteByOutboxStatusAndSagaStatusIn(outboxStatus, List.of(sagaStatus));
    }

    @Override
    public <G extends OutboxMessage> List<G> findByMessageTypeAndOutboxStatusAndSagaStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return orderOutboxJpaRepository.findByMessageTypeAndOutboxStatusAndSagaStatusIn(outboxMessageClazz.getSimpleName(),
                                                                                        outboxStatus,
                                                                                        List.of(sagaStatus))
                .stream()
                .map(it -> orderOutboxEntityMapper.mapOrderOutboxEntityToOrderOutboxMessage(it, outboxMessageClazz))
                .collect(Collectors.toList());
    }

    @Override
    public void save(OutboxMessage outboxMessage) {
        orderOutboxJpaRepository.save(orderOutboxEntityMapper.mapOrderOutboxMessageToOrderOutboxEntity(outboxMessage));

    }

    @Override
    public <G extends OutboxMessage> Optional<G> findByMessageTypeAndSagaIdAndSagaStatus(Class<G> outboxMessageClazz, UUID sagaId, SagaStatus... sagaStatus) {
        return orderOutboxJpaRepository.findByMessageTypeAndSagaIdAndSagaStatusIn(outboxMessageClazz.getSimpleName(), sagaId, Arrays.asList(sagaStatus))
                .map(it -> orderOutboxEntityMapper.mapOrderOutboxEntityToOrderOutboxMessage(it, outboxMessageClazz));
    }

    @Override
    public List<OutboxMessage> findBySagaIdAndSagaStatus(UUID sagaId, SagaStatus... sagaStatus) {
        return orderOutboxJpaRepository.findBySagaIdAndSagaStatusIn(sagaId, List.of(sagaStatus))
                .stream()
                .map(it -> orderOutboxEntityMapper.mapOrderOutboxEntityToOrderOutboxMessage(it, OutboxMessage.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateSagaStatusAndProcessAtById(SagaStatus sagaStatus, Instant processedAt, UUID id) {
        orderOutboxJpaRepository.updateSagaStatusAndProcessAtById(sagaStatus, processedAt, id);
    }
}

interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {

    void deleteByOutboxStatusAndSagaStatusIn(OutboxStatus outboxStatus, List<SagaStatus> sagaStatus);

    List<OrderOutboxEntity> findByMessageTypeAndOutboxStatusAndSagaStatusIn(String type,
                                                                            OutboxStatus outboxStatus,
                                                                            List<SagaStatus> sagaStatuses);

    Optional<OrderOutboxEntity> findByMessageTypeAndSagaIdAndSagaStatusIn(String type,
                                                                          UUID sagaId,
                                                                          List<SagaStatus> sagaStatus);

    List<OrderOutboxEntity> findBySagaIdAndSagaStatusIn(UUID sagaId, List<SagaStatus> sagaStatus);

    @Modifying
    @Query("update OrderOutboxEntity o set o.sagaStatus = ?1, o.processedAt = ?2 where o.id = ?3")
    void updateSagaStatusAndProcessAtById(SagaStatus sagaStatus, Instant processedAt, UUID id);
}