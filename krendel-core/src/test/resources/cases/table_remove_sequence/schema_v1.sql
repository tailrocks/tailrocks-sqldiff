CREATE SEQUENCE "accounts_id_seq";

CREATE TABLE "accounts" (
    id    BIGINT                 NOT NULL,
    email CHARACTER VARYING(255) NOT NULL
);

ALTER TABLE "accounts"
    ALTER COLUMN "id" SET DEFAULT nextval('accounts_id_seq'::REGCLASS);

ALTER SEQUENCE accounts_id_seq OWNED BY accounts.id;
