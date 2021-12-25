package krendel.spring.boot.advanced.sample.data.model;

import com.scentbird.hurma.hibernate.PostgreSQLEnum;

public enum Authority implements PostgreSQLEnum {

    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN;

    public static final String COLUMN_DEFINITION = "authority";

    @Override
    public String columnDefinition() {
        return COLUMN_DEFINITION;
    }

}
