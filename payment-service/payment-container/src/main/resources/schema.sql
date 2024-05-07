DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS payment_status;

CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED', 'REJECTED');

CREATE CAST (CHARACTER VARYING AS payment_status) WITH inout AS implicit;

DROP TABLE IF EXISTS "payment".payments CASCADE;

CREATE TABLE "payment".payments
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    order_id uuid NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_status payment_status NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "payment".billfold CASCADE;

CREATE TABLE "payment".billfold
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    total_billfold_amount NUMERIC(10,2) NOT NULL,
    CONSTRAINT billfold_pkey PRIMARY KEY (id)
);

CREATE TYPE transaction_type AS ENUM ('CASH', 'NON_CASH', 'CREDIT', 'DEBIT', 'VIRTUAL_BILLFOLD');

CREATE CAST (CHARACTER VARYING AS transaction_type) WITH inout AS implicit;

DROP TABLE IF EXISTS "payment".billfold_history CASCADE;

CREATE TABLE "payment".billfold_history
(
    id uuid NOT NULL,
    customer_id uuid NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    transaction_type transaction_type NOT NULL,
    CONSTRAINT billfold_history_pkey PRIMARY KEY (id)
);