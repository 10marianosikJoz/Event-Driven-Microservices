package com.product.ordering.adapters.repository;

import com.product.ordering.application.outbox.exception.OrderProcessedOutboxNotFoundException;
import com.product.ordering.application.outbox.projection.OrderProcessedOutboxMessage;
import com.product.ordering.application.outbox.repository.OrderProcessedOutboxRepository;
import com.product.ordering.entities.entity.outbox.OrderProcessedOutboxEntity;
import com.product.ordering.entities.entity.outbox.OrderProcessedOutboxEntityMapper;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class SqlRepositoryOrderProcessedOutbox implements OrderProcessedOutboxRepository {

    private final OrderProcessedOutboxJpaRepository orderProcessedOutboxJpaRepository;
    private final OrderProcessedOutboxEntityMapper orderProcessedOutboxEntityMapper;

    SqlRepositoryOrderProcessedOutbox(final OrderProcessedOutboxJpaRepository orderProcessedOutboxJpaRepository,
                                      final OrderProcessedOutboxEntityMapper orderProcessedOutboxEntityMapper) {

        this.orderProcessedOutboxJpaRepository = orderProcessedOutboxJpaRepository;
        this.orderProcessedOutboxEntityMapper = orderProcessedOutboxEntityMapper;
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void save(G orderOutboxMessage) {
        orderProcessedOutboxJpaRepository.save(orderProcessedOutboxEntityMapper.mapOrderProcessedOutboxMessageToOrderProcessedOutboxEntity(orderOutboxMessage));
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> List<G> findByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        return orderProcessedOutboxJpaRepository.findByMessageTypeAndOutboxStatus(outboxMessageClazz.getSimpleName(), outboxStatus)
                .orElseThrow(() -> new OrderProcessedOutboxNotFoundException("OrderProcessed outbox does not exist"))
                .stream()
                .map(it -> orderProcessedOutboxEntityMapper.mapOrderProcessedOutboxEntityToOrderProcessedOutboxMessage(it, outboxMessageClazz))
                .collect(Collectors.toList());
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> boolean existByTypeAndSagaIdAndOutboxStatus(Class<G> outboxMessageClazz, UUID sagaId, OutboxStatus outboxStatus) {
        return orderProcessedOutboxJpaRepository.existsByMessageTypeAndSagaIdAndOutboxStatus(outboxMessageClazz.getSimpleName(),
                                                                                             sagaId,
                                                                                             outboxStatus);
    }

    @Override
    public <G extends OrderProcessedOutboxMessage> void deleteByTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        orderProcessedOutboxJpaRepository.deleteByMessageTypeAndOutboxStatus(outboxMessageClazz.getSimpleName(), outboxStatus);
    }
}

@Repository
interface OrderProcessedOutboxJpaRepository extends JpaRepository<OrderProcessedOutboxEntity, UUID> {

    Optional<List<OrderProcessedOutboxEntity>> findByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    boolean existsByMessageTypeAndSagaIdAndOutboxStatus(String type, UUID sagaID, OutboxStatus outboxStatus);

    void deleteByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);
}