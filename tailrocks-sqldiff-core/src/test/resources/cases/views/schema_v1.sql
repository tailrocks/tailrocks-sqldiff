CREATE TABLE test_table (
    id            BIGINT                                    NOT NULL,
    created_date  TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version       BIGINT                      DEFAULT 0     NOT NULL,
    name          CHARACTER VARYING(255)                    NOT NULL
);

CREATE VIEW hello_world AS
SELECT 'Hello World';

CREATE VIEW vista AS
SELECT 'Hello World';
