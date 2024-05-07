INSERT INTO payment.billfold(id, customer_id, total_billfold_amount)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb21', '4ece4645-2658-4c54-a182-b0edcfa46d00', 800.00);

INSERT INTO payment.billfold_history(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb23', '4ece4645-2658-4c54-a182-b0edcfa46d00', 200.00, 'CREDIT');

INSERT INTO payment.billfold_history(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb24', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 750.00, 'CREDIT');

INSERT INTO payment.billfold_history(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb25', 'd215b5f8-0249-4dc5-89a3-51fd148cfb41', 810.00, 'DEBIT');


INSERT INTO payment.billfold(id, customer_id, total_billfold_amount)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb22', 'd215b5f8-0249-4dc5-89a3-51fd148cfb43', 300.00);

INSERT INTO payment.billfold_history(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb26', 'd215b5f8-0249-4dc5-89a3-51fd148cfb43', 200.00, 'CREDIT');