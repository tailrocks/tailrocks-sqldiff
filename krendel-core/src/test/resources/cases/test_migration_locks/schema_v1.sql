CREATE TYPE "account_status" AS ENUM (
    'NEW',
    'NORMAL'
    );

CREATE TABLE "account" (
    id     BIGINT                 NOT NULL,
    status "account_status"       NOT NULL,
    email  CHARACTER VARYING(255) NOT NULL,
    index  INTEGER                NOT NULL
);

CREATE INDEX ix_account_status_new ON account USING btree(id);

CREATE UNIQUE INDEX ix_account_email ON account USING btree(email);