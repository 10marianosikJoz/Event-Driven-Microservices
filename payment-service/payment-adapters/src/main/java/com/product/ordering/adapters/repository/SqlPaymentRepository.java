package com.product.ordering.adapters.repository;

import com.product.ordering.domain.entity.Payment;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.entities.entity.PaymentEntity;
import com.product.ordering.application.exception.PaymentNotFoundException;
import com.product.ordering.application.ports.output.PaymentRepository;
import com.product.ordering.entities.mapper.PaymentEntityCommandMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class SqlPaymentRepository implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentEntityCommandMapper paymentEntityCommandMapper;

    SqlPaymentRepository(PaymentJpaRepository paymentJpaRepository,
                                PaymentEntityCommandMapper paymentEntityCommandMapper) {

        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentEntityCommandMapper = paymentEntityCommandMapper;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentEntityCommandMapper.mapPaymentJpaEntityToPaymentDomainObject(
                paymentJpaRepository.save(paymentEntityCommandMapper.mapDomainPaymentObjectToPaymentJpaEntity(payment)));
    }

    @Override
    public Payment fetchByOrderId(OrderId orderId) {
        return paymentJpaRepository.findByOrderId(orderId.value())
                .map(paymentEntityCommandMapper::mapPaymentJpaEntityToPaymentDomainObject)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with orderId: " + orderId.value() + " is not present"));
    }
}

@Repository
interface PaymentJpaRepository extends CrudRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderId(UUID orderId);
}
