CREATE TABLE test_table (
    id            BIGINT NOT NULL,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    created_date  TIMESTAMP WITHOUT TIME ZONE
);
