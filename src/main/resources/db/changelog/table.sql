DROP TABLE IF EXISTS public.product CASCADE;
DROP TABLE IF EXISTS public.store CASCADE;
DROP TABLE IF EXISTS public.parameter CASCADE;
DROP TABLE IF EXISTS public.characteristic CASCADE;
DROP TABLE IF EXISTS public.category CASCADE ;
DROP TABLE IF EXISTS public.products_categories CASCADE;

CREATE SEQUENCE IF NOT EXISTS store_seq;
CREATE SEQUENCE IF NOT EXISTS product_seq;
CREATE SEQUENCE IF NOT EXISTS parameter_seq;
CREATE SEQUENCE IF NOT EXISTS characteristic_seq;
CREATE SEQUENCE IF NOT EXISTS category_seq;

CREATE TABLE IF NOT EXISTS public.characteristic
(
    characteristic_id   VARCHAR(255)    NOT NULL,

    CONSTRAINT characteristic_pkey      PRIMARY KEY (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.product
(
    product_id          VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('product_seq'),
    name                VARCHAR(255)    NOT NULL,
    country             VARCHAR(255)    ,
    type                VARCHAR(32)     NOT NULL,
    weight              NUMERIC         ,
    weight_with_box     NUMERIC         ,

    CONSTRAINT product_pkey PRIMARY KEY (product_id)
);

CREATE TABLE IF NOT EXISTS public.parameter
(
    parameter_id        VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('parameter_seq'),
    name                VARCHAR(255)    NOT NULL,
    value               VARCHAR(512)    NOT NULL,
    product_id          VARCHAR(32)     NOT NULL,
    characteristic_id   VARCHAR(255)    NOT NULL,

    CONSTRAINT parameter_pkey           PRIMARY KEY (parameter_id),
    CONSTRAINT pp_product_fk            FOREIGN KEY (product_id)        REFERENCES product          (product_id),
    CONSTRAINT pp_characteristic_fk     FOREIGN KEY (characteristic_id) REFERENCES characteristic   (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.store
(
    store_id            VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('store_seq'),
    cost                INT             NOT NULL,
    name                VARCHAR(255)    NOT NULL,
    url                 VARCHAR(255)    NOT NULL,
    product_id          VARCHAR(32)     NOT NULL,

    CONSTRAINT store_pkey               PRIMARY KEY (store_id),
    CONSTRAINT pp_product_fk            FOREIGN KEY (product_id)        REFERENCES product          (product_id)
);

CREATE TABLE IF NOT EXISTS public.category


(
    category_id         VARCHAR(255)             NOT NULL DEFAULT NEXTVAL('category_seq'),
    name                VARCHAR(255)    NOT NULL,


    CONSTRAINT category_pkey            PRIMARY KEY (category_id)
    );

CREATE TABLE IF NOT EXISTS public.products_categories
(
    category_id         VARCHAR(32)             NOT NULL,
    product_id          VARCHAR(32)     NOT NULL,

    CONSTRAINT category_fk              FOREIGN KEY (category_id)       REFERENCES category   (category_id),
    CONSTRAINT product_fk               FOREIGN KEY (product_id)        REFERENCES product  (product_id)
);