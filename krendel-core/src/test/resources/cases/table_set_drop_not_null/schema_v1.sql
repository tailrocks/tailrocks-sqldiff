CREATE TABLE test_table (
    id            BIGINT,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    created_date  TIMESTAMP WITHOUT TIME ZONE
);
