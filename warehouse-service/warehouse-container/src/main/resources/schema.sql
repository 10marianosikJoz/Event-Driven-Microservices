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