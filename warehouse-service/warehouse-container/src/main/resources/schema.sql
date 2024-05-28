DROP SCHEMA IF EXISTS warehouse CASCADE;

CREATE SCHEMA warehouse;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS order_approval_status CASCADE;

CREATE TYPE order_approval_status AS ENUM ('APPROVED', 'REJECTED');
CREATE CAST (CHARACTER VARYING AS order_approval_status) WITH inout AS implicit;

DROP TABLE IF EXISTS warehouse.warehouses CASCADE;

CREATE TABLE warehouse.warehouses
(
    warehouse_id UUID NOT NULL,
    name character varying COLLATE pg_catalog."default" NOT NULL,
    available boolean NOT NULL,
    CONSTRAINT restaurants_pkey PRIMARY KEY (warehouse_id)
);

DROP TABLE IF EXISTS warehouse.order_processed CASCADE;

CREATE TABLE warehouse.order_processed
(
    id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    price NUMERIC(10, 2)  NOT NULL,
    status order_approval_status NOT NULL,
    CONSTRAINT order_processed_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS warehouse.order_items CASCADE;

CREATE TABLE warehouse.order_items
(
    id UUID NOT NULL,
    product_id UUID NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    order_id UUID NOT NULL,
    CONSTRAINT products_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "warehouse".order_processed_outbox_entity CASCADE;

CREATE TABLE "warehouse".order_processed_outbox_entity
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP,
    processed_at TIMESTAMP,
    payload JSONB NOT NULL,
    aggregate_id UUID NOT NULL,
    payload_type VARCHAR(150),
    message_type VARCHAR(150) NOT NULL,
    outbox_status VARCHAR(30) NOT NULL,
    order_approval_status order_approval_status NOT NULL,
    version INTEGER NOT NULL,
    CONSTRAINT order_processed_outbox_entity_pkey PRIMARY KEY (id)
);

CREATE INDEX "outbox_warehouse_order_approval_status"
    ON "warehouse".order_processed_outbox_entity
    (message_type, order_approval_status);

CREATE UNIQUE index "outbox_warehouse_saga_id_order_approval_status"
    on "warehouse".order_processed_outbox_entity
        (message_type, saga_id, order_approval_status, outbox_status);