CREATE TABLE "accounts" (
    id BIGINT NOT NULL,
    CONSTRAINT "accounts_id_unique" UNIQUE (id)
);
CREATE TABLE "users" (
    id    BIGINT                 NOT NULL,
    email CHARACTER VARYING(255) NOT NULL,
    CONSTRAINT "users_unique" UNIQUE (id, email)
);
CREATE TABLE "orders" (
    id BIGINT NOT NULL
);