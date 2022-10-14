DROP TABLE IF EXISTS public.category CASCADE ;
DROP TABLE IF EXISTS public.products_categories CASCADE;
DROP SEQUENCE IF EXISTS public.category_seq CASCADE;

CREATE  SEQUENCE  category_seq  AS  int  START  WITH  0  INCREMENT  BY  1 MINVALUE 0;


CREATE TABLE IF NOT EXISTS public.category


(
    name                VARCHAR(255)    NOT NULL,
    category_id              INT             NOT NULL,

    CONSTRAINT category_pkey PRIMARY KEY (category_id)

    );

CREATE TABLE IF NOT EXISTS public.products_categories
(
    category_id         INT    NOT NULL,
    product_id          VARCHAR(32)     NOT NULL,

    CONSTRAINT category_fk     FOREIGN KEY (category_id) REFERENCES category   (category_id),
    CONSTRAINT product_fk     FOREIGN KEY (product_id) REFERENCES product  (product_id)
    );

ALTER TABLE IF EXISTS public.category
    ALTER COLUMN "category_id" ADD GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 0 MINVALUE 0 );