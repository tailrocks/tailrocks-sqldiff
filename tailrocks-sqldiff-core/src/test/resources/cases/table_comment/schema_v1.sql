CREATE TABLE "accounts" (
    id BIGINT NOT NULL
);
CREATE TABLE "users" (
    id BIGINT NOT NULL
);
CREATE TABLE "orders" (
    id BIGINT NOT NULL
);
COMMENT ON TABLE "users" IS 'users comment';
COMMENT ON TABLE "orders" IS 'orders comment';
