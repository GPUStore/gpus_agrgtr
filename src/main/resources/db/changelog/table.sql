DROP TABLE IF EXISTS public.parameter;
DROP TABLE IF EXISTS public.characteristic;
DROP TABLE IF EXISTS public.product;
DROP TABLE IF EXISTS public.product_characteristic;
DROP TABLE IF EXISTS public.product_characteristic_parameter;

CREATE SEQUENCE IF NOT EXISTS product_seq;
CREATE SEQUENCE IF NOT EXISTS parameter_seq;
CREATE SEQUENCE IF NOT EXISTS characteristic_seq;
CREATE SEQUENCE IF NOT EXISTS product_characteristic_seq;
CREATE SEQUENCE IF NOT EXISTS product_characteristic_parameter_seq;

CREATE TABLE IF NOT EXISTS public.parameter
(
    parameter_id        VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('parameter_seq'),
    name                VARCHAR(255)    NOT NULL,
    value               VARCHAR(512)    NOT NULL,

    CONSTRAINT parameter_pkey PRIMARY KEY (parameter_id)
);

CREATE TABLE IF NOT EXISTS public.characteristic
(
--    characteristic_id   VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('characteristic_seq'),
    characteristic_id                VARCHAR(255)    NOT NULL,

    CONSTRAINT characteristic_pkey      PRIMARY KEY (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.product
(
    product_id          VARCHAR(32)     NOT NULL DEFAULT NEXTVAL('product_seq'),
    cost                INT             NOT NULL,
    country             VARCHAR(255)    ,
    name                VARCHAR(255)    NOT NULL,
    type                VARCHAR(32)     NOT NULL,
    url                 VARCHAR(255)    NOT NULL,
    weight              INT             ,
    weight_with_box     INT             ,

    CONSTRAINT product_pkey PRIMARY KEY (product_id)
);

CREATE TABLE IF NOT EXISTS public.product_characteristic
(
    pp_id               BIGINT          NOT NULL DEFAULT NEXTVAL('product_characteristic_seq'),
    product_id          VARCHAR(32)     NOT NULL,
    characteristic_id   VARCHAR(255)    NOT NULL,

    CONSTRAINT pp_pkey                  PRIMARY KEY (pp_id),
    CONSTRAINT pp_product_fk            FOREIGN KEY (product_id)        REFERENCES product          (product_id),
    CONSTRAINT pp_characteristic_fk     FOREIGN KEY (characteristic_id) REFERENCES characteristic   (characteristic_id)
);

CREATE TABLE IF NOT EXISTS public.product_characteristic_parameter
(
    pcp_id              BIGINT          NOT NULL DEFAULT NEXTVAL('product_characteristic_parameter_seq'),
    product_id          VARCHAR(32)     NOT NULL,
    characteristic_id   VARCHAR(255)    NOT NULL,
    parameter_id        VARCHAR(32)     NOT NULL,

    CONSTRAINT pcp_pkey                 PRIMARY KEY (pcp_id),
    CONSTRAINT pcp_product_fk           FOREIGN KEY (product_id)        REFERENCES product          (product_id),
    CONSTRAINT pcp_characteristic_fk    FOREIGN KEY (characteristic_id) REFERENCES characteristic   (characteristic_id),
    CONSTRAINT pcp_parameter_fk         FOREIGN KEY (parameter_id)      REFERENCES parameter        (parameter_id)
);

