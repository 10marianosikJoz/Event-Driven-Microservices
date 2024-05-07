package com.product.ordering.domain.entity;

import com.product.ordering.domain.constant.DomainConstants;
import com.product.ordering.domain.exception.OrderDomainException;
import com.product.ordering.domain.valueobject.Coupon;
import com.product.ordering.domain.valueobject.DeliveryAddress;
import com.product.ordering.domain.valueobject.PaymentMethod;
import com.product.ordering.domain.valueobject.OrderItemId;
import com.product.ordering.domain.valueobject.Currency;
import com.product.ordering.domain.valueobject.CustomerId;
import com.product.ordering.domain.valueobject.DeliveryMethod;
import com.product.ordering.domain.valueobject.Money;
import com.product.ordering.domain.valueobject.OrderId;
import com.product.ordering.domain.valueobject.OrderStatus;
import com.product.ordering.domain.valueobject.WarehouseId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    private final CustomerId customerId;
    private final WarehouseId warehouseId;
    private final DeliveryAddress deliveryAddress;
    private final Currency currency;
    private final Set<OrderItem> orderItems;
    private final PaymentMethod paymentMethod;
    private final DeliveryMethod deliveryMethod;

    private Money price;
    private Coupon coupon;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    private Order(OrderBuilder orderBuilder) {

        super.id(orderBuilder.orderId);
        this.customerId = orderBuilder.customerId;
        this.warehouseId = orderBuilder.warehouseId;
        this.deliveryAddress = orderBuilder.deliveryAddress;
        this.price = orderBuilder.price;
        this.currency = orderBuilder.currency;
        this.orderItems = Set.copyOf(orderBuilder.orderItems);
        this.paymentMethod = orderBuilder.paymentMethod;
        this.deliveryMethod = orderBuilder.deliveryMethod;
        this.coupon = orderBuilder.coupon;
        this.orderStatus = orderBuilder.orderStatus;
        this.failureMessages = orderBuilder.failureMessages;
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_STATE);
        }

        validateDeliveryAddress();
        validateDeliveryMethod();
        validatePaymentMethod();
        addAdditionalDeliveryCharge();
        subtractCouponValueFromPrice();

        orderStatus = OrderStatus.PAID;
    }

    private void validateDeliveryAddress() {
        if (deliveryAddress == null) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_DELIVERY_ADDRESS);
        }
    }

    private void validateDeliveryMethod() {
        if (deliveryMethod == null) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_DELIVERY_METHOD);
        }
    }

    private void validatePaymentMethod() {
        if (paymentMethod == null) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_PAYMENT_METHOD);
        }
    }

    private void addAdditionalDeliveryCharge() {
        if (deliveryMethod.equals(DeliveryMethod.COURIER)) {
            price = price.add(new Money(BigDecimal.valueOf(DomainConstants.ORDER_DELIVERY_CHARGE)));
        }
    }

    private void subtractCouponValueFromPrice() {
        if (isCouponApplied()) {
            this.price = this.price.subtract(new Money(coupon.money().amount()));
        }
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_STATE);
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages) {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_STATE);
        }
        orderStatus = OrderStatus.CANCELLING;

        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING)) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_STATE);
        }
        orderStatus = OrderStatus.CANCELLED;

        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream()
                                                       .filter(it -> !it.isBlank())
                                                       .toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    public void validateOrders() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() {
        if (orderStatus != null || id() != null) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_STATE);
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException(DomainConstants.ORDER_INCORRECT_TOTAL_PRICE);
        }
    }

    private void validateItemsPrice() {
        var totalValue = calculateTotalPrice();
        if (!price.equals(totalValue)) {
            throw new OrderDomainException("Total price: " + price.amount() +
                    " is not equal to Order items total: " + totalValue.amount());
        }
    }

    private Money calculateTotalPrice() {
        return orderItems.stream()
                .map(orderItem -> {
                    validateItemPrice(orderItem);
                    return orderItem.subtotal();
                }).reduce(Money.ZERO, Money::add);

    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + orderItem.price().amount() +
                    " is not valid for product " + orderItem.product().id());
        }
    }

    public void initializeOrder() {
        id(new OrderId(UUID.randomUUID()));
        orderStatus = OrderStatus.PENDING;
        initializeOrders();
    }

    private void initializeOrders() {
        var itemId = 1L;
        orderItems.forEach(
                orderItem -> orderItem.initializeOrderItem(super.id(),
                                                           new OrderItemId(itemId + 1))
        );
    }

    public void applyCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    private boolean isCouponApplied() {
        return coupon != null;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public WarehouseId warehouseId() {
        return warehouseId;
    }

    public DeliveryAddress deliveryAddress() {
        return deliveryAddress;
    }

    public Money price() {
        return price;
    }

    public Coupon coupon() {
        return coupon;
    }

    public Currency currency() {
        return currency;
    }

    public Set<OrderItem> orderItems() {
        return orderItems;
    }

    public PaymentMethod paymentMethod() { return paymentMethod; }

    public DeliveryMethod deliveryMethod() {
        return deliveryMethod;
    }

    public OrderStatus orderStatus() {
        return orderStatus;
    }

    public List<String> failureMessages() {
        return failureMessages;
    }

    public static final class OrderBuilder {

        private OrderId orderId;
        private CustomerId customerId;
        private WarehouseId warehouseId;
        private DeliveryAddress deliveryAddress;
        private Money price;
        private Currency currency;
        private Set<OrderItem> orderItems;
        private PaymentMethod paymentMethod;
        private DeliveryMethod deliveryMethod;
        private Coupon coupon;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private OrderBuilder() {}

        public OrderBuilder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public OrderBuilder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public OrderBuilder warehouseId(WarehouseId warehouseId) {
            this.warehouseId = warehouseId;
            return this;
        }

        public OrderBuilder deliveryAddress(DeliveryAddress deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public OrderBuilder price(Money price) {
            this.price = price;
            return this;
        }

        public OrderBuilder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public OrderBuilder orderItems(Set<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public OrderBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public OrderBuilder deliveryMethod(DeliveryMethod deliveryMethod) {
            this.deliveryMethod = deliveryMethod;
            return this;
        }

        public OrderBuilder coupon(Coupon coupon) {
            this.coupon = coupon;
            return this;
        }

        public OrderBuilder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public OrderBuilder failureMessages(List<String> failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
