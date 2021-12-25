CREATE TYPE "crawler_page_status" AS ENUM (
    'PENDING',
    'PROCESSING'
    );

CREATE TABLE test_table (
    id              BIGINT                                    NOT NULL,
    created_date    TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    modified_date   TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version         BIGINT                      DEFAULT 0     NOT NULL,
    name            CHARACTER VARYING(255)                    NOT NULL,
    data            JSONB,
    page            CHARACTER VARYING(2083)                   NOT NULL,
    status          "crawler_page_status"                     NOT NULL,
    expired         BOOLEAN,
    old_ids         BIGINT[],
    release_date    DATE,
    rating_value    DOUBLE PRECISION                          NOT NULL,
    year            INTEGER,
    title           TEXT                                      NOT NULL,
    sync_date       TIMESTAMP,
    schedule        TEXT[][],
    squares         INTEGER[3][3],
    pay_by_quarter1 INTEGER[],
    pay_by_quarter2 INTEGER ARRAY[4],
    pay_by_quarter3 INTEGER ARRAY
);
