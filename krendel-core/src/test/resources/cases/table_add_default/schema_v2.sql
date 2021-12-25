CREATE TABLE test_table (
    id            BIGINT                                    NOT NULL,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version       BIGINT                      DEFAULT 0     NOT NULL
);
