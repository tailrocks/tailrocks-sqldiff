CREATE TABLE "accounts" (
    id       BIGINT                 NOT NULL,
    email    CHARACTER VARYING(255) NOT NULL,
    username CHARACTER VARYING(255) NOT NULL
);
COMMENT ON COLUMN "accounts"."email" IS 'account email';
COMMENT ON COLUMN "accounts"."username" IS 'account username';
