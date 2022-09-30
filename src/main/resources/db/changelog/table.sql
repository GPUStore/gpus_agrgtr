DROP TABLE IF EXISTS public.parameter;
DROP TABLE IF EXISTS public.product;
DROP TABLE IF EXISTS public.characteristic;
DROP TABLE IF EXISTS public.product_parameter;

CREATE SEQUENCE IF NOT EXISTS product_seq;
CREATE SEQUENCE IF NOT EXISTS parameter_seq;
CREATE SEQUENCE IF NOT EXISTS characteristic_seq;
CREATE SEQUENCE IF NOT EXISTS product_parameter_seq;

CREATE TABLE IF NOT EXISTS public.parameter
(
    parameter_id        VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('parameter_seq'),
    name                VARCHAR(255)    NOT NULL,
    value               VARCHAR(512)    NOT NULL,

    CONSTRAINT parameter_pkey PRIMARY KEY (parameter_id)
);

CREATE TABLE IF NOT EXISTS public.characteristic
(
    characteristic_id                VARCHAR(255)    NOT NULL,

    CONSTRAINT characteristic_pkey      PRIMARY KEY (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.product
(
    product_id          VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('parameter_seq'),
    name                VARCHAR(255)    NOT NULL,
    cost                INT             NOT NULL,
    country             VARCHAR(255)    ,
    type                VARCHAR(32)     NOT NULL,
    url                 VARCHAR(255)    NOT NULL,
    weight              INT             ,
    weight_with_box     INT             ,

    CONSTRAINT product_pkey PRIMARY KEY (product_id)
);

CREATE TABLE IF NOT EXISTS public.product_parameter
(
    pp_id               BIGINT          NOT NULL DEFAULT NEXTVAL('product_parameter_seq'),
    product_id          VARCHAR(32)     NOT NULL,
    parameter_id        VARCHAR(255)    NOT NULL,

    CONSTRAINT pp_pkey                  PRIMARY KEY (pp_id),
    CONSTRAINT pp_product_fk            FOREIGN KEY (product_id)        REFERENCES product     (product_id),
    CONSTRAINT pp_parameter_fk          FOREIGN KEY (parameter_id)      REFERENCES parameter   (parameter_id)
);

