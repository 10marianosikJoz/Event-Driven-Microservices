INSERT INTO "order".orders(id, customer_id, warehouse_id, currency, payment_method, delivery_method, price, order_status)
VALUES ('43c5c9da-6797-4ee2-8dd9-30a46e3b2451', '08fcccc6-ae0a-49c3-bd98-905d8a1f8d1c',
        'd215b5f8-0249-4dc5-89a3-51fd148cfb66', 'PLN', 'CASH', 'COURIER', 150.00, 'PENDING');

INSERT INTO "order".order_items(id, order_id, product_id, price, quantity, sub_total)
VALUES (1, '43c5c9da-6797-4ee2-8dd9-30a46e3b2451', 'd215b5f8-0249-4dc5-89a3-51fd148cfb47', 150.00, 1, 150.00);

INSERT INTO "order".delivery_address(id, order_id, street, postal_code, city)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb15', '43c5c9da-6797-4ee2-8dd9-30a46e3b2451', 'Diamentowa', '20-000', 'Lublin');

INSERT INTO "order".order_outbox_entity(id, saga_id, created_at, processed_at, payload, aggregate_id, payload_type, message_type,
                                        outbox_status, saga_status, version)
VALUES ('64bcacc7-4175-4069-8e22-c3fa1ddda6b9', '2dbf52f8-4d08-4d03-bf1d-0dfec68497cb', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
   '{
   "price":150.00,
   "orderItems":[
      {
         "quantity":1,
         "productId":"d215b5f8-0249-4dc5-89a3-51fd148cfb47",
         "price":150.00,
         "totalPrice":150.00,
         "subtotal":150.00
      }
   ],
   "orderStatus":"PENDING",
   "orderId":"43c5c9da-6797-4ee2-8dd9-30a46e3b2451",
   "customerId":"08fcccc6-ae0a-49c3-bd98-905d8a1f8d1c",
   "creationDate":"2024-04-20T17:22:34.231229600Z",
   "warehouseId":"d215b5f8-0249-4dc5-89a3-51fd148cfb66",
   "deliveryAddress":{
      "street":"Diamentowa",
      "postalCode":"20-000",
      "city":"Lublin"
   },
   "currency":"PLN",
   "paymentMethod":"CASH",
   "deliveryMethod":"COURIER"
}','43c5c9da-6797-4ee2-8dd9-30a46e3b2451',
  'com.product.ordering.application.outbox.projection.OrderEventPayload',
  'OrderCreatedOutboxMessage',
  'STARTED',
  'STARTED',
   0);