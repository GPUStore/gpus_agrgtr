DROP TABLE IF EXISTS public.parameter;
DROP TABLE IF EXISTS public.product;
DROP TABLE IF EXISTS public.characteristic;

CREATE SEQUENCE IF NOT EXISTS product_seq;
CREATE SEQUENCE IF NOT EXISTS parameter_seq;
CREATE SEQUENCE IF NOT EXISTS characteristic_seq;

CREATE TABLE IF NOT EXISTS public.characteristic
(
    characteristic_id   VARCHAR(255)    NOT NULL,

    CONSTRAINT characteristic_pkey      PRIMARY KEY (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.product
(
    product_id          VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('product_seq'),
    name                VARCHAR(255)    NOT NULL,
    cost                INT             NOT NULL,
    country             VARCHAR(255)    ,
    type                VARCHAR(32)     NOT NULL,
    url                 VARCHAR(255)    NOT NULL,
    weight              INT             ,
    weight_with_box     INT             ,

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
