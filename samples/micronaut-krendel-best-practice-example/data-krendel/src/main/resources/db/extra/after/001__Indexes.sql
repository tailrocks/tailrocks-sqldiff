/* additional schema queries, indexes with condition or something what JPA is not supporting */

CREATE INDEX ix_account_email_not_null ON account USING btree(email)
    WHERE email IS NOT NULL;
