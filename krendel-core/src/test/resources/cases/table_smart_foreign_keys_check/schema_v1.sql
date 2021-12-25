CREATE TABLE media (
    id        BIGINT NOT NULL,
    parent_id BIGINT
);

ALTER TABLE ONLY media
    ADD CONSTRAINT media_pkey PRIMARY KEY (id);


CREATE TABLE record (
    id       BIGINT NOT NULL,
    name     CHARACTER VARYING(255),
    media_id BIGINT
);

ALTER TABLE ONLY record
    ADD CONSTRAINT record_pkey PRIMARY KEY (id);


ALTER TABLE ONLY record
    ADD CONSTRAINT fk_record_media_id FOREIGN KEY (media_id) REFERENCES media(id);
