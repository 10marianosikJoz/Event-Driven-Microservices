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

--DROP TABLE IF EXISTS warehouse.warehouse_products CASCADE;
--
--CREATE TABLE warehouse.warehouse_products
--(
--    id uuid NOT NULL,
--    warehouse_id uuid NOT NULL,
--    product_id uuid NOT NULL,
--    CONSTRAINT warehouse_products_pkey PRIMARY KEY (id)
--);
--
--ALTER TABLE warehouse.warehouse_products
--    ADD CONSTRAINT "FK_WAREHOUSE_ID" FOREIGN KEY (warehouse_id)
--    REFERENCES warehouse.warehouse (id) MATCH SIMPLE
--    ON UPDATE NO ACTION
--    ON DELETE RESTRICT
--    NOT VALID;
--
--ALTER TABLE warehouse.warehouse_products
--    ADD CONSTRAINT "FK_PRODUCT_ID" FOREIGN KEY (product_id)
--    REFERENCES warehouse.products (id) MATCH SIMPLE
--    ON UPDATE NO ACTION
--    ON DELETE RESTRICT
--    NOT VALID;
--
--DROP MATERIALIZED VIEW IF EXISTS warehouse.warehouse_order_materialized_view;
--
--CREATE MATERIALIZED VIEW warehouse.warehouse_order_materialized_view
--TABLESPACE pg_default
--AS
-- SELECT w.id AS warehouse_id,
--    w.name AS warehouse_name,
--    w.active AS warehouse_active,
--    p.id AS product_id,
--    p.name AS product_name,
--    p.price AS product_price,
--    p.available AS product_available
--   FROM warehouse.warehouse w,
--    warehouse.products p,
--    warehouse.warehouse_products wp
--  WHERE w.id = wp.warehouse_id AND p.id = wp.product_id
--WITH DATA;
--
--refresh materialized VIEW warehouse.warehouse_order_materialized_view;
--
--DROP function IF EXISTS warehouse.refresh_warehouse_order_materialized_view;
--
--CREATE OR replace function warehouse.refresh_warehouse_order_materialized_view()
--returns trigger
--AS '
--BEGIN
--    refresh materialized VIEW warehouse.warehouse_order_materialized_view;
--    return null;
--END;
--'  LANGUAGE plpgsql;
--
--DROP trigger IF EXISTS refresh_warehouse_order_materialized_view ON warehouse.warehouse_products;
--
--CREATE trigger refresh_warehouse_order_materialized_view
--after INSERT OR UPDATE OR DELETE OR truncate
--ON warehouse.warehouse_products FOR each statement
--EXECUTE PROCEDURE warehouse.refresh_warehouse_order_materialized_view();