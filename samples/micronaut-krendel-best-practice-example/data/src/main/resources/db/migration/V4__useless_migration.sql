CREATE EXTENSION pg_stat_statements;

CREATE TYPE "test_enum" AS ENUM ('A', 'B', 'C');

CREATE SEQUENCE "test_sequence";

CREATE INDEX "test_index" ON "account" USING btree(id);

ALTER TABLE "account"
    ADD CONSTRAINT "test_constraint" UNIQUE (id);

CREATE VIEW test_view AS
SELECT *
FROM "account";

ALTER TABLE "account"
    ADD COLUMN "test_column" BIGINT;
