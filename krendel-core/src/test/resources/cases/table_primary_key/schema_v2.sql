CREATE TABLE "accounts" (
    id BIGINT NOT NULL,
    CONSTRAINT "accounts_pkey" PRIMARY KEY (id)
);
CREATE TABLE "users" (
    id    BIGINT                 NOT NULL,
    email CHARACTER VARYING(255) NOT NULL,
    CONSTRAINT "users_pkey" PRIMARY KEY (id, email)
);
CREATE TABLE "orders" (
    id BIGINT NOT NULL
);