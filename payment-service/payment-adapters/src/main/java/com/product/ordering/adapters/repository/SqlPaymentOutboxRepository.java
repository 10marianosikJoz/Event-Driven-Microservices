package com.product.ordering.adapters.repository;

import com.product.ordering.application.exception.PaymentOutboxNotFoundException;
import com.product.ordering.application.outbox.projection.PaymentOutboxMessage;
import com.product.ordering.application.outbox.repository.PaymentOutboxRepository;
import com.product.ordering.domain.valueobject.PaymentStatus;
import com.product.ordering.entities.entity.outbox.PaymentOutboxEntity;
import com.product.ordering.entities.entity.outbox.PaymentOutboxEntityMapper;
import com.product.ordering.system.outbox.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
class SqlPaymentOutboxRepository implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxEntityMapper paymentOutboxEntityMapper;

    SqlPaymentOutboxRepository(final PaymentOutboxJpaRepository paymentOutboxJpaRepository,
                               final PaymentOutboxEntityMapper paymentOutboxEntityMapper) {

        this.paymentOutboxJpaRepository = paymentOutboxJpaRepository;
        this.paymentOutboxEntityMapper = paymentOutboxEntityMapper;
    }

    @Override
    public <G extends PaymentOutboxMessage> void save(G outboxMessage) {
        paymentOutboxJpaRepository.save(paymentOutboxEntityMapper.mapPaymentOutboxMessageToPaymentOutboxEntity(outboxMessage));
    }

    @Override
    public <G extends PaymentOutboxMessage> List<G> findByMessageTypeAndOutboxStatus(Class<G> outboxMessageClazz, OutboxStatus outboxStatus) {
        return paymentOutboxJpaRepository.findByMessageTypeAndOutboxStatus(outboxMessageClazz.getSimpleName(), outboxStatus)
                .orElseThrow(() -> new PaymentOutboxNotFoundException(outboxMessageClazz.getSimpleName() + " does not exist"))
                .stream()
                .map(it -> paymentOutboxEntityMapper.mapPaymentOutboxEntityToPaymentOutboxMessage(it, outboxMessageClazz))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByOutboxStatus(OutboxStatus outboxStatus) {
        paymentOutboxJpaRepository.deleteByOutboxStatus(outboxStatus);
    }

    @Override
    public <G extends PaymentOutboxMessage> boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(Class<G> outboxMessageClazz,
                                                                                                                UUID sagaId,
                                                                                                                PaymentStatus paymentStatus,
                                                                                                                OutboxStatus outboxStatus) {
        return paymentOutboxJpaRepository.existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(outboxMessageClazz.getSimpleName(),
                                                                                                      sagaId,
                                                                                                      paymentStatus,
                                                                                                      outboxStatus);
    }
}

@Repository
interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {

    Optional<List<PaymentOutboxEntity>> findByMessageTypeAndOutboxStatus(String type, OutboxStatus outboxStatus);

    boolean existsByMessageTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type,
                                                                        UUID sagaId,
                                                                        PaymentStatus paymentStatus,
                                                                        OutboxStatus outboxStatus);

    void deleteByOutboxStatus(OutboxStatus outboxStatus);
}
