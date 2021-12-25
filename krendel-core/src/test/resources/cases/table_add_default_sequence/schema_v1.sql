CREATE SEQUENCE "accounts_id_seq";

CREATE TABLE "accounts" (
    id    BIGINT                 NOT NULL,
    email CHARACTER VARYING(255) NOT NULL
);
