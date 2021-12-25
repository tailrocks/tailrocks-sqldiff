CREATE TABLE new_table (
    column1 BIGINT
);

CREATE TYPE "account_status" AS ENUM (
    'NEW',
    'NORMAL'
    );

CREATE TABLE "account" (
    id     BIGINT                 NOT NULL,
    status "account_status"       NOT NULL,
    email  CHARACTER VARYING(255) NOT NULL,
    index  INTEGER                NOT NULL DEFAULT 5
);

CREATE INDEX ix_account_status_new ON account USING btree(id) WHERE (status = 'NEW'::"account_status");

CREATE UNIQUE INDEX ix_account_email_index ON account USING btree(email, index);